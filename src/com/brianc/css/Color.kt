package com.brianc.css

class Color(val r: Short, val g: Short, val b: Short, val a: Short) {

    constructor(r: Int, g: Int, b: Int, a: Int) : this(r.toShort(), g.toShort(), b.toShort(), a.toShort()) {
    }

    override fun toString(): String {
        return String.format("rgba(%d,%d,%d,%d)", r, g, b, a)
    }

    override fun equals(obj: Any?): Boolean {
        if (obj is Color) {
            return r == obj.r && g == obj.g && b == obj.b && a == obj.a
        }

        return false
    }

    companion object {

        val BLACK = Color(0, 0, 0, 255)
    }
}
