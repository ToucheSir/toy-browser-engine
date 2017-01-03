package com.brianc.layout

class EdgeSizes {
    var left: Float = 0.toFloat()
    var right: Float = 0.toFloat()
    var top: Float = 0.toFloat()
    var bottom: Float = 0.toFloat()

    constructor(left: Float, right: Float, top: Float, bottom: Float) {
        this.left = left
        this.right = right
        this.top = top
        this.bottom = bottom
    }

    constructor() {
    }

    constructor(sizes: EdgeSizes) : this(sizes.left, sizes.right, sizes.top, sizes.bottom) {
    }
}
