package com.brianc.graphics

import com.brianc.css.Color
import com.brianc.layout.InlineBox
import java.awt.Graphics2D
import java.awt.image.BufferedImage

import com.brianc.layout.Rect
import java.awt.font.FontRenderContext
import java.awt.geom.Point2D
import java.awt.Color as AwtColor

class ImageRenderer(dimensions: Rect) : Renderer {
    val buffer: BufferedImage = BufferedImage(dimensions.width.toInt(), dimensions.height.toInt(), BufferedImage.TYPE_INT_ARGB)
    val graphicsContext: Graphics2D

    override val fontRenderContext: FontRenderContext
        get() = graphicsContext.fontRenderContext

    init {
        graphicsContext = buffer.createGraphics()
    }

    override fun draw(item: DisplayCommand) = with(item) {
        when (this) {
            is SolidColor -> paintColorRect(color, rect)
            is RenderText -> paintText(color, lines, rect, text)
        }
    }

    fun paintColorRect(color: Color, rect: Rect) {
        graphicsContext.color = AwtColor(
                color.r.toInt(), color.g.toInt(),
                color.b.toInt(), color.a.toInt()
        )
        graphicsContext.fill(rect)
    }

    fun paintText(color: Color, lines: List<InlineBox.LineBox>, rect: Rect, text: String) {
        val drawCoords = Point2D.Float(rect.x, rect.y)
        graphicsContext.color = AwtColor(
                color.r.toInt(), color.g.toInt(), color.b.toInt(), color.a.toInt()
        )

        for (frag in lines.flatMap(InlineBox.LineBox::getFragments)) {
            val lineLayout = frag.layout

			drawCoords.y += lineLayout.ascent
			val dx = if (lineLayout.isLeftToRight) 0f else rect.width - lineLayout.advance

			lineLayout.draw(graphicsContext, drawCoords.x + dx, drawCoords.y)
			drawCoords.y += lineLayout.descent + lineLayout.leading
        }
    }
}
