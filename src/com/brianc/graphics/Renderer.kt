package com.brianc.graphics

import java.awt.font.FontRenderContext

interface Renderer {
    fun draw(item: DisplayCommand)
    val fontRenderContext: FontRenderContext
}
