package com.brianc.html

import java.util.ArrayList
import java.util.HashMap

import com.brianc.AbstractParser
import com.brianc.dom.Node

class HTMLParser(input: String) : AbstractParser(input) {

    fun parse(): Node {
        val nodes = parseNodes()

        if (nodes.size == 1) {
            return nodes[0]
        }

        return Node.createElement("html", HashMap<String, String>(), nodes)
    }

    private fun parseTagName(): String {
        return consumeWhile({ Character.isLetterOrDigit(it) })
    }

    private fun parseNode(): Node {
        when (nextChar()) {
            '<' -> return parseElement()
            else -> return parseText()
        }
    }

    private fun parseText(): Node {
        return Node.createText(consumeWhile { c -> c !== '<' })
    }

    private fun parseElement(): Node {
        assertInput(consumeChar() == '<')
        val tagName = parseTagName()
        val attrs = parseAttributes()
        assertInput(consumeChar() == '>')

        val children = parseNodes()

        assertInput(consumeChar() == '<')
        assertInput(consumeChar() == '/')
        assertInput(parseTagName() == tagName)
        assertInput(consumeChar() == '>')

        return Node.createElement(tagName, attrs, children)
    }

    private fun parseNodes(): List<Node> {
        val nodes = ArrayList<Node>()

        while (true) {
            consumeWhitespace()

            if (eof() || startsWith("</")) {
                break
            }

            nodes.add(parseNode())
        }

        return nodes
    }

    private fun parseAttributes(): Map<String, String> {
        val attributes = HashMap<String, String>()

        while (true) {
            consumeWhitespace()

            if (nextChar() == '>') {
                break
            }

            val attribute = parseAttribute()
            attributes.put(attribute.first, attribute.second)
        }

        return attributes
    }

    private fun parseAttribute(): Pair<String, String> {
        val name = parseTagName()
        assertInput(consumeChar() == '=')
        val value = parseAttributeValue()

        return Pair(name, value)
    }

    private fun parseAttributeValue(): String {
        val openQuote = consumeChar()
        assertInput(openQuote == '"' || openQuote == '\'')
        val value = consumeWhile { c -> c !== openQuote }
        assertInput(consumeChar() == openQuote)

        return value
    }

    private fun assertInput(cond: Boolean) {
        if (!cond) {
            throw IllegalStateException()
        }
    }

    companion object {

        @JvmStatic fun main(args: Array<String>) {
            val toParse = "<html><body><h1>Title</h1><div id=\"main\" class=\"test\"><p>Hello <em>world</em>!</p></div></body></html>"
            val root = HTMLParser(toParse).parse()

            println(root)
        }
    }
}
