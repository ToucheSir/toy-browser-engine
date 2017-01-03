package com.brianc.dom

import java.util.ArrayList

class TextNode(val text: String) : Node(ArrayList<Node>(0), NodeType.TEXT) {
    override fun toString() = text.trim { it <= ' ' }.replace("\n", "")
}
