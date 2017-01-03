package com.brianc.css

class ColorValue(val color: Color) : Value {

    override fun clone(): Value {
        return ColorValue(color)
    }

    override fun toString(): String {
        return "ColorValue($color)"
    }

    override fun equals(obj: Any?): Boolean {
        if (obj is ColorValue) {
            return color == obj.color
        }

        return false
    }
}
