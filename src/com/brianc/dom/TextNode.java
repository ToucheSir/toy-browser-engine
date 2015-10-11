package com.brianc.dom;

import java.util.ArrayList;

public class TextNode extends Node {
	private String text;
	
	public TextNode(String text) {
		super(new ArrayList<>(0), NodeType.TEXT);
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
