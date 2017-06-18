package com.brianc.dom

abstract class Node(val children: List<Node>, val type: NodeType) {
    companion object {

        fun createText(text: String): Node {
            return TextNode(text)
        }

        fun createElement(name: String, attributes: Map<String, String>,
                          children: List<Node>): Node {
            return ElementNode(children, ElementData(name, attributes))
        }
    }
}
