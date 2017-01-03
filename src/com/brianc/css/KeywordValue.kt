package com.brianc.css

class KeywordValue(keyword: String) : Value {
    var value: String
        internal set

    init {
        this.value = keyword
    }

    override fun clone(): Value {
        return KeywordValue(value)
    }

    override fun toString(): String {
        return "KeywordValue($value)"
    }

    override fun equals(obj: Any?): Boolean {
        if (obj is KeywordValue) {

            return this.value == obj.value
        }

        return false
    }
}
