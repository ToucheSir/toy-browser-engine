package com.brianc.style;

public enum Display {
	INLINE, BLOCK, NONE;
	
	public static Display find(String name) {
		switch(name) {
		case "block":
			return BLOCK;
		case "none":
			return NONE;
		default:
			return INLINE;
		}
	}
}
