package com.brianc.dom;

import java.util.List;
import java.util.stream.Collectors;

public class ElementNode extends Node {
	private ElementData data;

	public ElementNode(List<Node> children, ElementData data) {
		super(children, NodeType.ELEMENT);
		this.data = data;
	}

	public ElementData getData() {
		return data;
	}

//	@Override
//	public String toString() {
//		String childStr = getChildren().stream().map(Object::toString)
//				.collect(Collectors.joining(""));
//		String attrStr = data.attributes.entrySet().stream()
//				.map(attr -> String.format(" %s=\"%s\"", attr.getKey(), attr.getValue()))
//				.collect(Collectors.joining(""));
//		return String.format("<%s%s>%s</%s>", data.tagName, attrStr, childStr, data.tagName);
//	}

	@Override
	public String toString() {
		String attrStr = data.attributes.entrySet().stream()
				.map(attr -> String.format(" %s=\"%s\"", attr.getKey(), attr.getValue()))
				.collect(Collectors.joining(""));
		return String.format("<%s%s />", data.tagName, attrStr);
	}
}
