package com.brianc.css;

public class Color {
	public final short r;
	public final short g;
	public final short b;
	public final short a;
	
	public static final Color BLACK = new Color(0, 0, 0, 255);

	public Color(short r, short g, short b, short a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public Color(int r, int g, int b, int a) {
		this((short)r, (short)g, (short)b, (short)a);
	}

	@Override
	public String toString() {
		return String.format("rgba(%d,%d,%d,%d)", r, g, b, a);
	}

}
