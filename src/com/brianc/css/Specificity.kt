package com.brianc.css

import kotlin.comparisons.compareValuesBy

data class Specificity(val a: Int, val b: Int, val c: Int) : Comparable<Specificity> {
    override fun compareTo(other: Specificity) = compareValuesBy(this, other, { it.a }, { it.b }, { it.c })
}
