package com.brianc.css

interface Value {
    fun clone(): Value

    fun toPx(): Float {
        return 0f
    }
}
