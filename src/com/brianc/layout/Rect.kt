package com.brianc.layout

import java.awt.geom.Rectangle2D.Float as FloatRect

@SuppressWarnings("serial")
class Rect : FloatRect {
    constructor() : super() {
    }

    constructor(x: kotlin.Float, y: kotlin.Float, w: kotlin.Float, h: kotlin.Float) : super(x, y, w, h) {
    }

    constructor(rect: Rect) : this(rect.x, rect.y, rect.width, rect.height) {
    }

    fun expandedBy(edge: EdgeSizes): Rect {
        return Rect(x - edge.left, y - edge.right, width + edge.left + edge.right,
                height + edge.top + edge.bottom)
    }
}
