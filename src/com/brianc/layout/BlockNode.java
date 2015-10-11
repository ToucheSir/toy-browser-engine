package com.brianc.layout;

import com.brianc.style.StyledNode;

public class BlockNode implements BoxType {
	final StyledNode node;
	
	public BlockNode(final StyledNode node) {
		this.node = node;
	}

	@Override
	public Type getType() {
		return Type.BLOCK_NODE;
	}
	
	@Override
	public StyledNode getStyle() throws UnsupportedOperationException {
		return node;
	}
}
