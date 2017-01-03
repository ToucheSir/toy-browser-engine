package com.brianc.layout

import java.util.Optional
import java.util.stream.Stream

import com.brianc.css.KeywordValue
import com.brianc.css.LengthValue
import com.brianc.css.Unit
import com.brianc.css.Value
import com.brianc.graphics.Renderer
import com.brianc.style.StyledNode

class BlockBox(override val styledNode: StyledNode) : LayoutBox(), StyledLayoutBox {

    override val type: BoxType
        get() = BoxType.BLOCK_NODE

    internal override fun layout(containingBlock: Dimensions, renderBackend: Renderer) {
        calculateBlockWidth(containingBlock)
        calculateBlockPosition(containingBlock)
        layoutBlockChildren(renderBackend)
        calculateBlockHeight()
    }

    private fun calculateBlockHeight() {
        val heightValue = styledNode.value("height")
        if (heightValue.isPresent && heightValue.get() is LengthValue) {
            dimensions.content.height = heightValue.get().toPx()
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

        dimensions.margin.top = styledNode.lookup("margin-top", "margin", zero).toPx()
        dimensions.margin.bottom = styledNode.lookup("margin-bottom", "margin", zero).toPx()

        dimensions.border.top = styledNode.lookup("border-left", "border", zero).toPx()
        dimensions.border.bottom = styledNode.lookup("border-right", "border", zero).toPx()

        dimensions.padding.top = styledNode.lookup("padding-left", "padding", zero).toPx()
        dimensions.padding.bottom = styledNode.lookup("padding-right", "padding", zero).toPx()

        dimensions.content.x = containingBlock.content.x + dimensions.margin.left
        +dimensions.border.left + dimensions.padding.left

        dimensions.content.y = containingBlock.content.height + containingBlock.content.y
        +dimensions.margin.top + dimensions.border.top + dimensions.padding.left

    }

    private fun calculateBlockWidth(containingBlock: Dimensions) {
        val auto = KeywordValue("auto")
        var width = styledNode.value("width").orElse(auto)
        val zero = LengthValue(0f, Unit.PX)

        var marginLeft = styledNode.lookup("margin-left", "margin", zero)
        var marginRight = styledNode.lookup("margin-right", "margin", zero)

        val borderLeft = styledNode.lookup("border-left-width", "border-width", zero)
        val borderRight = styledNode.lookup("border-right-width", "border-width", zero)

        val paddingLeft = styledNode.lookup("padding-left", "padding", zero)
        val paddingRight = styledNode.lookup("padding-right", "padding", zero)

        val total = listOf(marginLeft, marginRight, borderLeft, borderRight,
                paddingLeft, paddingRight, width).map { v -> v.toPx() }.sum()

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

        if (!widthIsAuto) {
            if (!leftIsAuto && rightIsAuto) {
                marginRight = LengthValue(underflow, Unit.PX)
            } else if (leftIsAuto && !rightIsAuto) {
                marginLeft = LengthValue(underflow, Unit.PX)
            } else if (!leftIsAuto && !rightIsAuto) {
                marginRight = LengthValue(marginRight.toPx() + underflow, Unit.PX)
            } else {
                marginLeft = LengthValue(underflow / 2, Unit.PX)
                marginRight = LengthValue(underflow / 2, Unit.PX)
            }
        } else {
            if (marginLeft == auto) {
                marginLeft = zero
            }

            if (marginRight == auto) {
                marginRight = zero
            }

            if (underflow >= 0) {
                width = LengthValue(underflow, Unit.PX)
            } else {
                width = zero
                marginRight = LengthValue(marginRight.toPx() + underflow, Unit.PX)
            }
        }

        dimensions.content.width = width.toPx()

        dimensions.padding.left = paddingLeft.toPx()
        dimensions.padding.right = paddingRight.toPx()

        dimensions.border.left = borderLeft.toPx()
        dimensions.border.right = borderRight.toPx()

        dimensions.margin.left = marginLeft.toPx()
        dimensions.margin.right = marginRight.toPx()
    }
}
