package com.brianc.graphics

import java.awt.Graphics2D

import com.brianc.css.Color
import com.brianc.layout.Rect

class SolidColor(internal val color: Color, internal val rect: Rect) : DisplayCommand {

    override fun paint(g: Graphics2D) {
        g.color = java.awt.Color(color.r.toInt(), color.g.toInt(), color.b.toInt(), color.a.toInt())
        g.fill(rect)
    }
}
