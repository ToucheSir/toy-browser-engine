package com.brianc.layout

data class EdgeSizes(var left: Float = 0f, var right: Float = 0f, var top: Float = 0f, var bottom: Float = 0f) {
    constructor(sizes: EdgeSizes) : this(sizes.left, sizes.right, sizes.top, sizes.bottom)
}
