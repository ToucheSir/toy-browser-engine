package com.brianc.dom;

import java.util.List;

public class CommentNode extends Node {
	private String commentText;

	public CommentNode(List<Node> children, String commentText) {
		super(children, NodeType.COMMENT);
		this.commentText = commentText;
	}
	
	public String getText() {
		return commentText;
	}
}
