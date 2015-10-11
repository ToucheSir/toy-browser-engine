package com.brianc.layout;

import com.brianc.style.Display;
import com.brianc.style.StyledNode;

public interface BoxType {
	public Type BLOCK_NODE = Type.BLOCK_NODE;
	public Type INLINE_NODE = Type.INLINE_NODE;
	public Type ANONYMOUS_BLOCK = Type.ANONYMOUS_BLOCK;
	
	public enum Type {
		BLOCK_NODE, INLINE_NODE, ANONYMOUS_BLOCK;

		public static BoxType fromDisplay(Display display, StyledNode styleNode) {
			switch (display) {
			case BLOCK:
				return new BlockNode(styleNode);
			case INLINE:
				return new InlineNode(styleNode);
			case NONE:
			default:
				throw new IllegalArgumentException(String
						.format("Display mode '%s' must be one of 'INLINE' or 'BLOCK'", display));
			}
		}
	}
	
	public Type getType();
	
	public default StyledNode getStyle() throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
}
