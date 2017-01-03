package com.brianc.graphics

import java.awt.Graphics2D
import java.awt.geom.Point2D

import com.brianc.css.Color
import com.brianc.layout.InlineBox.LineBox
import com.brianc.layout.Rect

class RenderText(private val text: String, private val lines: List<LineBox>, private val rect: Rect, private val color: Color) : DisplayCommand {

    override fun paint(g: Graphics2D) {
        val drawCoords = Point2D.Float(rect.x, rect.y)
        g.color = java.awt.Color(color.r.toInt(), color.g.toInt(), color.b.toInt(), color.a.toInt())

        for (line in lines) {
            /*			TextLayout lineLayout = line.getLayout();

			drawCoords.y += lineLayout.getAscent();
			float dx = lineLayout.isLeftToRight() ? 0 : (rect.width - lineLayout.getAdvance());

			lineLayout.draw(g, drawCoords.x + dx, drawCoords.y);
			drawCoords.y += lineLayout.getDescent() + lineLayout.getLeading();*/
        }
    }
}
