package com.brianc.css;

public class ColorValue implements Value {
	final Color color;

	public ColorValue(Color color) {
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}
	
	@Override
	public Value clone() {
		return new ColorValue(color);
	}
	
	@Override
	public String toString() {
		return "ColorValue(" + color + ")";
	}
}
