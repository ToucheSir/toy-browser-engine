package com.brianc.layout

import com.brianc.graphics.Renderer

class AnonymousBlockBox : LayoutBox() {
    override val type: BoxType
        get() = BoxType.ANONYMOUS_BLOCK

    internal override fun layout(containingBlock: Dimensions, renderBackend: Renderer) {
    }
}
