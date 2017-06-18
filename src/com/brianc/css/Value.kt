package com.brianc.css

sealed class Value

data class KeywordValue(val keyword: String) : Value() {
    override fun toString() = "KeywordValue($keyword)"
}

data class LengthValue(val length: Float, var unit: Unit) : Value() {
    fun toPx() = length * unit.conversionFactor
    override fun toString() = "LengthValue($length, $unit)"
}

data class ColorValue(val color: Color) : Value() {
    override fun toString() = "ColorValue($color)"
}
