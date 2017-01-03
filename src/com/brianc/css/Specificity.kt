package com.brianc.css

import java.util.Comparator

class Specificity(internal var a: Int, internal var b: Int, internal var c: Int) : Comparable<Specificity> {

    override fun compareTo(other: Specificity): Int {
        return cmp!!.compare(this, other)
    }

    companion object {

        private var cmp: Comparator<Specificity>? = null

        init {
            // HACK to placate Java's type inference
            cmp = Comparator.comparingInt<Specificity> { s -> s.a }
            cmp = cmp!!.thenComparingInt { s -> s.b }.thenComparingInt { s -> s.c }
        }
    }
}
