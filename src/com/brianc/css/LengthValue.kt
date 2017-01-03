package com.brianc.css

class LengthValue(internal var length: Float, internal var unit: Unit) : Value {

    override fun toPx(): Float {
        return length
    }

    override fun clone(): Value {
        return LengthValue(length, unit)
    }

    override fun toString(): String {
        return String.format("LengthValue(%f, %s)", length, unit)
    }

    override fun equals(obj: Any?): Boolean {
        if (obj is LengthValue) {

            return obj.length == length && obj.unit == unit
        }

        return false
    }
}
