package com.brianc.css;

public class LengthValue implements Value {
	float length;
	Unit unit;

	public LengthValue(float length, Unit unit) {
		super();
		this.length = length;
		this.unit = unit;
	}
	
	public float toPx() {
		return length;
	}
	
	public Value clone() {
		return new LengthValue(length, unit);
	}
	
	@Override
	public String toString() {
		return String.format("LengthValue(%f, %s)", length, unit);
	}
}
