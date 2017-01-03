package com.brianc.css

interface Value {
    fun clone(): Value

    open fun toPx(): Float {
        return 0f
    }
}
