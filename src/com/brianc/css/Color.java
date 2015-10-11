package com.brianc.css;

public class Color {
	public final short r;
	public final short g;
	public final short b;
	public final short a;

	public Color(short r, short g, short b, short a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	@Override
	public String toString() {
		return String.format("rgba(%d,%d,%d,%d)", r, g, b, a);
	}

}
