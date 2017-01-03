package com.brianc.css

data class LengthValue(internal var length: Float, internal var unit: Unit) : Value {
    override fun toPx() = length
    override fun clone() = LengthValue(length, unit)
    override fun toString() = "LengthValue($length, $unit)"
}
