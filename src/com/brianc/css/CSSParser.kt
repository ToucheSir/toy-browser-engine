package com.brianc.css

import java.util.ArrayList
import java.util.Optional
import java.util.TreeSet

import com.brianc.AbstractParser

class CSSParser(input: String) : AbstractParser(input) {

    fun parse(): StyleSheet {
        return StyleSheet(parseRules())
    }

    private fun parseSimpleSelector(): SimpleSelector {
        val selector = SimpleSelector()

        while (!eof()) {
            val ch = nextChar()
            if (ch == '#') {
                consumeChar()
                selector.id = Optional.of(parseIdentifier())
            } else if (ch == '.') {
                consumeChar()
                selector.classes.add(parseIdentifier())
            } else if (ch == '*') {
                consumeChar()
            } else if (validIdentifierChar(ch)) {
                selector.tagName = Optional.of(parseIdentifier())
            } else {
                break
            }
        }

        return selector
    }

    private fun parseRules(): List<Rule> {
        val rules = ArrayList<Rule>()

        while (true) {
            consumeWhitespace()
            if (eof())
                break
            rules.add(parseRule())
        }

        return rules
    }

    private fun parseRule(): Rule {
        return Rule(parseSelectors(), parseDeclarations())
    }

    private fun parseDeclarations(): List<Declaration> {
        assertInput(consumeChar() == '{')
        val declarations = ArrayList<Declaration>()
        while (true) {
            consumeWhitespace()
            if (nextChar() == '}') {
                consumeChar()
                break
            }

            declarations.add(parseDeclaration())
        }

        return declarations
    }

    private fun parseDeclaration(): Declaration {
        val propertyName = parseIdentifier()
        consumeWhitespace()

        assertInput(consumeChar() == ':')
        consumeWhitespace()

        val value = parseValue()
        consumeWhitespace()

        assertInput(consumeChar() == ';')

        return Declaration(propertyName, value)
    }

    private fun parseValue(): Value {
        val ch = nextChar()
        if (Character.isDigit(ch)) {
            return parseLength()
        } else if (ch == '#') {
            return parseColor()
        }

        return KeywordValue(parseIdentifier())
    }

    private fun parseColor(): Value {
        assertInput(consumeChar() == '#')

        return ColorValue(
                Color(parseHexPair(), parseHexPair(), parseHexPair(), 255.toShort()))
    }

    private fun parseHexPair(): Short {
        return java.lang.Short.parseShort(consumeChars(2), 16)
    }

    private fun parseLength(): Value {
        return LengthValue(parseFloat(), parseUnit())
    }

    private fun parseUnit(): Unit {
        when (parseIdentifier().toLowerCase()) {
            "px" -> return Unit.PX
            else -> throw IllegalArgumentException("unrecognized unit")
        }
    }

    private fun parseFloat(): Float {
        return java.lang.Float.parseFloat(consumeWhile { c -> Character.isDigit(c!!) || c === '.' })
    }

    private fun parseSelectors(): List<Selector> {
        val selectors = TreeSet<Selector> { a, b -> a.specificity().compareTo(b.specificity()) }

        while (true) {
            selectors.add(parseSimpleSelector())
            consumeWhitespace()

            val ch = nextChar()
            if (ch == ',') {
                consumeChar()
                consumeWhitespace()
            } else if (ch == '{') {
                break
            } else {
                throw IllegalStateException("Unexpected character $ch in selector list")
            }
        }

        return ArrayList(selectors)
    }

    private fun parseIdentifier(): String {
        return consumeWhile({ validIdentifierChar(it) })
    }

    private fun assertInput(cond: Boolean) {
        if (!cond) {
            throw IllegalStateException()
        }
    }

    companion object {

        private fun validIdentifierChar(ch: Char): Boolean {
            return Character.isLetterOrDigit(ch) || ch == '-' || ch == '_'
        }

        @JvmStatic fun main(args: Array<String>) {
            val s = CSSParser("div.note { margin-bottom: 20px; padding: 10px; }").parse()
            println(s)
        }
    }
}
