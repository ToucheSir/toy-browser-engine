package com.brianc.layout

import java.awt.font.FontRenderContext
import java.awt.font.LineBreakMeasurer
import java.awt.font.TextLayout
import java.text.AttributedCharacterIterator
import java.text.AttributedString
import java.util.ArrayList
import java.util.Deque
import java.util.HashSet
import java.util.LinkedList
import java.util.stream.Stream

import com.brianc.css.KeywordValue
import com.brianc.css.LengthValue
import com.brianc.css.Unit
import com.brianc.css.Value
import com.brianc.dom.Node
import com.brianc.dom.NodeType
import com.brianc.dom.TextNode
import com.brianc.graphics.Renderer
import com.brianc.style.StyledNode

class InlineBox(override val children: MutableList<LayoutBox> = mutableListOf(),
                override val dimensions: Dimensions = Dimensions(),
                override val styledNode: StyledNode) : StyledLayoutBox {
    val lines: Deque<LineBox>
    private val fragments: MutableSet<Fragment>

    // TODO add list

    fun getFragments(): Set<Fragment> {
        return fragments
    }

    init {
        lines = LinkedList<LineBox>()
        fragments = HashSet<Fragment>()
    }

    private fun layout(containingBlock: Dimensions, inlineRoot: InlineBox, renderBackend: Renderer, lastLine: LineBox) {
        layoutInlineMaxWidth(containingBlock)
        reCalcLines(containingBlock, inlineRoot, lastLine, renderBackend)

        for (child in children) {
            // TODO block children?
            val childAsInline = child as InlineBox
            childAsInline.layout(dimensions, inlineRoot, renderBackend, lastLine)

            lines.addAll(childAsInline.lines)
        }

        calculateInlinePosition(containingBlock)
    }

    private fun calculateInlinePosition(containingBlock: Dimensions) {
        val zero = LengthValue(0f, Unit.PX)
        val maxLineWidth = (lines.map({ l -> l.filledWidth.toDouble() }).max() ?: 0.0).toFloat()
        val totalHeight = lines.map({ l -> l.boxHeight.toDouble() }).sum().toFloat()

        dimensions.margin.top =
                (styledNode.lookup("margin-top", "margin", zero) as LengthValue).toPx()
        dimensions.margin.bottom =
                (styledNode.lookup("margin-bottom", "margin", zero) as LengthValue).toPx()

        dimensions.border.top =
                (styledNode.lookup("border-top-width", "border-width", zero) as LengthValue).toPx()
        dimensions.border.bottom =
                (styledNode.lookup("border-bottom-width", "border-width", zero) as LengthValue).toPx()

        dimensions.padding.top =
                (styledNode.lookup("padding-left", "padding", zero) as LengthValue).toPx()
        dimensions.padding.bottom =
                (styledNode.lookup("padding-right", "padding", zero) as LengthValue).toPx()

        dimensions.content.width = maxLineWidth
        dimensions.content.height = totalHeight

        dimensions.content.x = containingBlock.content.x + dimensions.margin.left + dimensions.padding.left + dimensions.border.left
        dimensions.content.y = containingBlock.content.y
    }

    override fun layout(containingBlock: Dimensions, renderBackend: Renderer) {
        // TODO inline layout as per
        // http://www.w3.org/TR/CSS2/visuren.html#inline-boxes
        // and http://www.w3.org/TR/CSS2/visuren.html#inline-formatting
        if (lines.isEmpty()) {
            lines.add(LineBox(this))
        }

        layout(containingBlock, this, renderBackend, lastLine)
    }

    private val lastLine: LineBox
        get() = lines.last

    private fun reCalcLines(dimensions: Dimensions, inlineRoot: InlineBox, lastLine: LineBox, renderBackend: Renderer) {
        val domNode = styledNode.node

        if (domNode.type == NodeType.TEXT) {
            val nodeText = (domNode as TextNode).text
            val iter = AttributedString(nodeText).iterator
            val renderContext = renderBackend.fontRenderContext

            val measurer = LineBreakMeasurer(iter, renderContext)
            val contentMaxWidth = dimensions.content.width
            var currentLine = lastLine

            var pos = measurer.position
            while (pos < nodeText.length) {
                var lineRemainingWidth = contentMaxWidth

                if (currentLine.filledWidth < contentMaxWidth) {
                    lineRemainingWidth = contentMaxWidth - currentLine.filledWidth
                } else {
                    currentLine = LineBox(inlineRoot)
                    lines.add(currentLine)
                }

                val lineLayout = measurer.nextLayout(lineRemainingWidth)

                val textFrag = Fragment(this, pos, measurer.position, lineLayout)
                fragments.add(textFrag)

                currentLine.addFragment(textFrag)
                pos = measurer.position
            }
        }
    }

    private fun layoutInlineMaxWidth(containingBlock: Dimensions) {
        val auto = KeywordValue("auto")
        val width = auto
        val zero = LengthValue(0f, Unit.PX)

        var marginLeft: Value = styledNode.lookup("margin-left", "margin", zero)
        var marginRight: Value = styledNode.lookup("margin-right", "margin", zero)

        val borderLeft = styledNode.lookup("border-left-width", "border-width", zero)
        val borderRight = styledNode.lookup("border-right-width", "border-width", zero)

        val paddingLeft = styledNode.lookup("padding-left", "padding", zero)
        val paddingRight = styledNode.lookup("padding-right", "padding", zero)

        val total = listOf(marginLeft, marginRight, borderLeft, borderRight, paddingLeft, paddingRight, width)
                .filterIsInstance<LengthValue>()
                .map(LengthValue::toPx)
                .sum()

        val containingWidth = containingBlock.content.width
        if (total > containingWidth) {
            if (marginLeft == auto) {
                marginLeft = zero
            }

            if (marginRight == auto) {
                marginRight = zero
            }
        }

        val leftIsAuto = marginLeft == auto
        val rightIsAuto = marginRight == auto

        if (leftIsAuto) {
            marginLeft = zero
        }

        if (rightIsAuto) {
            marginRight = zero
        }

        dimensions.padding.left = paddingLeft.toPx()
        dimensions.padding.right = paddingRight.toPx()

        dimensions.border.left = borderLeft.toPx()
        dimensions.border.right = borderRight.toPx()

        dimensions.margin.left = (marginLeft as LengthValue).toPx()
        dimensions.margin.right = (marginRight as LengthValue).toPx()

        if (containingWidth > total) {
            dimensions.content.width = containingWidth - total
        } else {
            dimensions.content.width = 0f
        }
    }

    inner class LineBox(private val inlineRoot: InlineBox) {
        private val fragments: MutableList<Fragment>
        var filledWidth: Float = 0f
            private set
        var boxHeight: Float = 0f
            private set
        var lineHeight: Float = 0f
            private set

        init {
            fragments = ArrayList<Fragment>()
        }

        fun addFragment(fragment: Fragment) {
            fragments.add(fragment)
            filledWidth += fragment.layout.advance
            val layout = fragment.layout

            // TODO: This assumes a default line-height of 1.0.
            val fragmentHeight = layout.ascent - layout.descent
            boxHeight = Math.max(boxHeight, fragmentHeight)

            if (inlineRoot.children.contains(fragment.box)) {
                lineHeight = fragmentHeight
            }
        }

        fun getFragments(): List<Fragment> {
            return fragments
        }
    }

}
