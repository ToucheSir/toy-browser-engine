package com.brianc

import java.util.function.Predicate

abstract class AbstractParser(// TODO Stream for efficiency?
        private val input: String) {
    private var pos: Int = 0

    init {
        pos = 0
    }

    protected fun nextChar(): Char {
        return input[pos]
    }

    protected fun startsWith(s: String): Boolean {
        return input.startsWith(s, pos)
    }

    protected fun eof(): Boolean {
        return pos >= input.length
    }

    protected fun consumeChar(): Char {
        return input[pos++]
    }

    protected fun consumeChars(number: Int): String {
        val start = pos
        pos += number

        return input.substring(start, pos)
    }

    protected fun consumeWhile(test: (Char) -> Boolean): String {
        val start = pos

        while (!eof() && test(nextChar())) {
            consumeChar()
        }

        return input.substring(start, pos)
    }

    protected fun consumeWhitespace() {
        consumeWhile(Char::isWhitespace)
    }

}
