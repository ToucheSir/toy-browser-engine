package com.brianc.dom

import java.util.stream.Collectors

class ElementNode(children: List<Node>, val data: ElementData) : Node(children, NodeType.ELEMENT) {

    //	@Override
    //	public String toString() {
    //		String childStr = getChildren().stream().map(Object::toString)
    //				.collect(Collectors.joining(""));
    //		String attrStr = data.attributes.entrySet().stream()
    //				.map(attr -> String.format(" %s=\"%s\"", attr.getKey(), attr.getValue()))
    //				.collect(Collectors.joining(""));
    //		return String.format("<%s%s>%s</%s>", data.tagName, attrStr, childStr, data.tagName);
    //	}

    override fun toString(): String {
        val attrStr = data.attributes.entries.map({ attr -> String.format(" %s=\"%s\"", attr.key, attr.value) }).joinToString("")
        return String.format("<%s%s />", data.tagName, attrStr)
    }
}
