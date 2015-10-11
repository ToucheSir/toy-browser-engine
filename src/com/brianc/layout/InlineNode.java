package com.brianc.layout;

import com.brianc.style.StyledNode;

public class InlineNode implements BoxType {
	final StyledNode node;
	
	public InlineNode(final StyledNode node) {
		this.node = node;
	}
	
	
	@Override
	public Type getType() {
		return Type.INLINE_NODE;
	}
	
	@Override
	public StyledNode getStyle() throws UnsupportedOperationException {
		return node;
	}
}
