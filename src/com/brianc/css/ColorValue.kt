package com.brianc.css

data class ColorValue(val color: Color) : Value {
    override fun clone() = ColorValue(color)
    override fun toString() = "ColorValue($color)"
}
