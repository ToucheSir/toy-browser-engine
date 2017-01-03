package com.brianc.graphics

import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.image.RenderedImage

import com.brianc.layout.Rect

class ImageRenderer(dimensions: Rect) : Renderer {
    override val graphicsContext: Graphics2D
    val buffer: BufferedImage = BufferedImage(dimensions.width.toInt(), dimensions.height.toInt(), BufferedImage.TYPE_INT_ARGB)

    init {
        graphicsContext = buffer.createGraphics()
    }
}
