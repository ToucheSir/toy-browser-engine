package com.brianc.dom;

import java.util.List;
import java.util.Map;

public abstract class Node {
	private List<Node> children;
	final NodeType type;

	public Node(List<Node> children, NodeType type) {
		this.children = children;
		this.type = type;
	}

	public List<Node> getChildren() {
		return children;
	}

	public NodeType getType() {
		return type;
	}

	public static Node createText(String text) {
		return new TextNode(text);
	}

	public static Node createElement(String name, Map<String, String> attributes,
			List<Node> children) {
		return new ElementNode(children, new ElementData(name, attributes));
	}
}
