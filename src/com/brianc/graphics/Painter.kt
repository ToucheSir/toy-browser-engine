package com.brianc.graphics

import java.util.ArrayList

import com.brianc.css.Color
import com.brianc.css.ColorValue
import com.brianc.dom.NodeType
import com.brianc.dom.TextNode
import com.brianc.layout.*
import com.brianc.layout.InlineBox.LineBox

object Painter {
    private fun buildDisplayList(layoutRoot: LayoutBox): List<DisplayCommand> {
        val displayList = ArrayList<DisplayCommand>()
        renderLayoutBox(displayList, layoutRoot)

        return displayList
    }

    private fun renderLayoutBox(displayList: MutableList<DisplayCommand>, layoutBox: LayoutBox) {
        when (layoutBox) {
            is BlockBox -> {
                renderBackground(displayList, layoutBox)
                renderBorders(displayList, layoutBox)
                for (child in layoutBox.children) {
                    renderLayoutBox(displayList, child)
                }
            }
            is InlineBox -> {
                val dims = layoutBox.dimensions
                val lines = layoutBox.lines
                val borderBox = dims.borderBox()

                getColor(layoutBox, "border-color")?.let {
                    val firstLine = lines.first
                    val boxHeight = firstLine.boxHeight
                    val lineHeight = firstLine.lineHeight
                    val borderHeight = lineHeight + dims.border.top + dims.border.bottom

                    // left border
                    displayList.add(SolidColor(it, Rect(
                            borderBox.x, borderBox.y + boxHeight - lineHeight,
                            dims.border.left, borderHeight
                    )))

                    val lastLine = lines.last
                    // right border
                    displayList.add(SolidColor(it, Rect(
                            borderBox.x + lastLine.filledWidth + dims.border.left + dims.border.right,
                            borderBox.y + borderHeight * (lines.size - 1),
                            dims.border.right, borderHeight
                    )))

                    val lineX = dims.content.x - dims.border.left
                    var lineY = dims.content.y

                    for (line in layoutBox.lines) {
                        // TODO proper line-height calculations
                        val lineWidth = line.filledWidth + dims.border.left + dims.border.right
                        val lineBoxHeight = line.boxHeight

                        val fragmentX = lineX
                        val fragmentY = lineY
                        for (f in line.getFragments()) {
                            if (layoutBox.getFragments().contains(f)) {
                                renderFragment(f, line, fragmentX, fragmentY, displayList, layoutBox)
                            }
                        }

                        // top border
                        displayList.add(SolidColor(it, Rect(
                                lineX, lineY + lineBoxHeight - lineHeight - dims.border.top,
                                lineWidth, dims.border.top
                        )))
                        // bottom border
                        displayList.add(SolidColor(it, Rect(
                                lineX, lineY + lineBoxHeight, lineWidth, dims.border.bottom
                        )))

                        lineY += borderHeight
                    }

                    layoutBox.children.forEach { renderText(displayList, it as InlineBox) }
                }
            }
        }
    }

    private fun renderFragment(f: Fragment, line: LineBox, fragmentX: Float, fragmentY: Float,
                               displayList: MutableList<DisplayCommand>, layoutBox: LayoutBox) {
    }

    private fun renderText(displayList: MutableList<DisplayCommand>, layoutBox: InlineBox) {
        // there needs to be some major refactoring before this is less ugly.
        // TODO eliminate the current wonky lookup for extracting a node
        // TODO include the parent box when rendering text without using this check
        // FIXME text rendering is not contained because inline layout and cascading(?) do not exist yet.
        val sourceNode = layoutBox.styledNode.node

        if (sourceNode.type == NodeType.TEXT) {
            val colorVal = getColor(layoutBox, "color")
            val fontColor = colorVal ?: Color.BLACK

            val dims = layoutBox.dimensions
            val paddingBox = dims.paddingBox()
            val text = (sourceNode as TextNode).text
            val lines = layoutBox.lines

            displayList.add(RenderText(text, lines.toList(), paddingBox, fontColor))
        }

    }

    private fun renderBorders(displayList: MutableList<DisplayCommand>, layoutBox: StyledLayoutBox) {
        getColor(layoutBox, "border-color")?.let {
            val dims = layoutBox.dimensions
            val borderBox = dims.borderBox()

            // left border
            displayList.add(SolidColor(it, Rect(
                    borderBox.x, borderBox.y, dims.border.left, borderBox.height
            )))
            // right border
            displayList.add(SolidColor(it, Rect(
                    borderBox.x + borderBox.width - dims.border.right, borderBox.y,
                    dims.border.right, borderBox.height
            )))
            // top border
            displayList.add(SolidColor(it, Rect(
                    borderBox.x, borderBox.y, borderBox.width, dims.border.top
            )))
            // bottom border
            displayList.add(SolidColor(it, Rect(
                    borderBox.x, borderBox.y + borderBox.height - dims.border.bottom,
                    borderBox.width, dims.border.bottom
            )))
        }
    }

    private fun renderBackground(displayList: MutableList<DisplayCommand>, layoutBox: StyledLayoutBox) {
        getColor(layoutBox, "background")?.let {
            displayList.add(SolidColor(it, layoutBox.dimensions.borderBox()))
        }
    }

    private fun getColor(layoutBox: StyledLayoutBox, name: String) =
        (layoutBox.styledNode.values[name] as? ColorValue)?.color

    fun paint(layoutRoot: LayoutBox, bounds: Rect, renderer: Renderer) {
        val displayList = buildDisplayList(layoutRoot)
        println(displayList)

        for (item in displayList) {
            renderer.draw(item)
        }
    }
}

