package com.brianc.layout

import com.brianc.graphics.Renderer

class AnonymousBlockBox(override val children: MutableList<LayoutBox> = mutableListOf(),
                        override val dimensions: Dimensions = Dimensions()) : LayoutBox {
    override fun layout(containingBlock: Dimensions, renderBackend: Renderer) = Unit
}
