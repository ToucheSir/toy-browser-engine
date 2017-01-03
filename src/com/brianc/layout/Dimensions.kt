package com.brianc.layout

class Dimensions @JvmOverloads constructor(val content: Rect = Rect(), val padding: EdgeSizes = EdgeSizes(), val border: EdgeSizes = EdgeSizes(), val margin: EdgeSizes = EdgeSizes()) {


    constructor(dims: Dimensions) : this(Rect(dims.content), EdgeSizes(dims.padding), EdgeSizes(dims.border), EdgeSizes(dims.margin)) {
    }

    fun paddingBox(): Rect {
        return content.expandedBy(padding)
    }

    fun borderBox(): Rect {
        return paddingBox().expandedBy(border)
    }

    fun marginBox(): Rect {
        return borderBox().expandedBy(margin)
    }
}

