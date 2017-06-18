package com.brianc.css

data class Color(val r: Short, val g: Short, val b: Short, val a: Short) {
    constructor(r: Int, g: Int, b: Int, a: Int) : this(r.toShort(), g.toShort(), b.toShort(), a.toShort())
    override fun toString() = "rgba($r,$g,$b,$a)"

    companion object {
        val BLACK = Color(0, 0, 0, 255)
    }
}
