package com.brianc.layout

data class Dimensions(val content: Rect = Rect(), val padding: EdgeSizes = EdgeSizes(), val border: EdgeSizes = EdgeSizes(), val margin: EdgeSizes = EdgeSizes()) {
    constructor(dims: Dimensions) : this(Rect(dims.content), EdgeSizes(dims.padding), EdgeSizes(dims.border), EdgeSizes(dims.margin))

    fun paddingBox() = content.expandedBy(padding)
    fun borderBox() = paddingBox().expandedBy(border)
    fun marginBox() = borderBox().expandedBy(margin)
}

