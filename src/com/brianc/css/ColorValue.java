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
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ColorValue) {
			ColorValue asColor = (ColorValue)obj;
			return color.equals(asColor.color);
		}
		
		return false;
	}
}
