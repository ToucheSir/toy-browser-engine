package com.brianc.dom;

public class TextNode extends Node {
	private String text;
	
	public TextNode(String text) {
		super(null, NodeType.TEXT);
		this.text = text;
	}

	public String getText() {
		return text;
	}
	
	@Override
	public String toString() {
		return text;
	}
}
