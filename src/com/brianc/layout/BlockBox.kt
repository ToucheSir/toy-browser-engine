package com.brianc.layout

import com.brianc.css.KeywordValue
import com.brianc.css.LengthValue
import com.brianc.css.Unit
import com.brianc.css.Value
import com.brianc.graphics.Renderer
import com.brianc.style.StyledNode

class BlockBox(override val children: MutableList<LayoutBox> = mutableListOf(),
               override val dimensions: Dimensions = Dimensions(),
               override val styledNode: StyledNode) : StyledLayoutBox {
    override val inlineContainer: LayoutBox
        get() = if (children.all { it is InlineBox }) {
            this
        } else {
            children.add(AnonymousBlockBox())
            children.last()
        }

    override fun layout(containingBlock: Dimensions, renderBackend: Renderer) {
        calculateBlockWidth(containingBlock)
        calculateBlockPosition(containingBlock)
        layoutBlockChildren(renderBackend)
        calculateBlockHeight()
    }

    private fun calculateBlockHeight() {
        val heightValue = styledNode.values["height"]
        if (heightValue is LengthValue) {
            dimensions.content.height = heightValue.toPx()
        }
    }

    private fun layoutBlockChildren(renderBackend: Renderer) {
        for (child in children) {
            child.layout(dimensions, renderBackend)

            dimensions.content.height += child.dimensions.marginBox().height
        }
    }

    private fun calculateBlockPosition(containingBlock: Dimensions) {
        val zero = LengthValue(0f, Unit.PX)

        dimensions.margin.top =
                styledNode.lookup("margin-top", "margin", zero).toPx()
        dimensions.margin.bottom =
                styledNode.lookup("margin-bottom", "margin", zero).toPx()

        dimensions.border.top =
                styledNode.lookup("border-left", "border", zero).toPx()
        dimensions.border.bottom =
                styledNode.lookup("border-right", "border", zero).toPx()

        dimensions.padding.top =
                styledNode.lookup("padding-left", "padding", zero).toPx()
        dimensions.padding.bottom =
                styledNode.lookup("padding-right", "padding", zero).toPx()

        dimensions.content.x = containingBlock.content.x + dimensions.margin.left + dimensions.border.left + dimensions.padding.left

        dimensions.content.y = containingBlock.content.height + containingBlock.content.y + dimensions.margin.top + dimensions.border.top + dimensions.padding.left
    }

    private fun calculateBlockWidth(containingBlock: Dimensions) {
        val auto = KeywordValue("auto")
        val width = styledNode.values["width"] ?: auto
        val zero = LengthValue(0f, Unit.PX)

        var marginLeft: Value = styledNode.lookup("margin-left", "margin", zero)
        var marginRight: Value = styledNode.lookup("margin-right", "margin", zero)

        val borderLeft = styledNode.lookup("border-left-width", "border-width", zero)
        val borderRight = styledNode.lookup("border-right-width", "border-width", zero)

        val paddingLeft = styledNode.lookup("padding-left", "padding", zero)
        val paddingRight = styledNode.lookup("padding-right", "padding", zero)

        val total = listOf(marginLeft, marginRight, borderLeft, borderRight,
                paddingLeft, paddingRight, width)
                .filterIsInstance<LengthValue>()
                .map(LengthValue::toPx).sum()

        if (width != auto && total > containingBlock.content.width) {
            if (marginLeft == auto) {
                marginLeft = zero
            }

            if (marginRight == auto) {
                marginRight = zero
            }
        }

        val underflow = containingBlock.content.width - total

        val widthIsAuto = width == auto
        val leftIsAuto = marginLeft == auto
        val rightIsAuto = marginRight == auto

        var leftMarginPx = 0f
        var rightMarginPx = 0f
        var widthPx = 0f

        if (!widthIsAuto) {
            widthPx = (width as LengthValue).toPx()
            when {
                (!leftIsAuto && rightIsAuto) -> {
                    rightMarginPx = underflow
                }
                (leftIsAuto && !rightIsAuto) -> {
                    leftMarginPx = underflow
                }
                (!leftIsAuto && !rightIsAuto) -> {
                    rightMarginPx = (marginRight as LengthValue).toPx() + underflow
                }
                else -> {
                    leftMarginPx = underflow / 2
                    rightMarginPx = underflow / 2
                }
            }
        } else {
            if (marginLeft == auto) {
                leftMarginPx = 0f
            }

            if (marginRight == auto) {
                rightMarginPx = 0f
            }

            if (underflow >= 0) {
                widthPx = underflow
            } else {
                rightMarginPx = (marginRight as LengthValue).toPx() + underflow
            }
        }

        dimensions.content.width = widthPx

        dimensions.padding.left = paddingLeft.toPx()
        dimensions.padding.right = paddingRight.toPx()

        dimensions.border.left = borderLeft.toPx()
        dimensions.border.right = borderRight.toPx()

        dimensions.margin.left = rightMarginPx
        dimensions.margin.right = leftMarginPx
    }
}
