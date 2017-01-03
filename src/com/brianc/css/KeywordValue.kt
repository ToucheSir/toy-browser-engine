package com.brianc.css

data class KeywordValue(val keyword: String) : Value {
    internal val value = keyword
    override fun clone() = KeywordValue(keyword)
    override fun toString() = "KeywordValue($keyword)"
}
