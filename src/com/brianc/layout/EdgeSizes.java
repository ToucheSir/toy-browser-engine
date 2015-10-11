package com.brianc.layout;

public class EdgeSizes {
	public float left;
	public float right;
	public float top;
	public float bottom;

	public EdgeSizes(float left, float right, float top, float bottom) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
	}
	
	public EdgeSizes() {
	}
	
	public EdgeSizes(EdgeSizes sizes) {
		this(sizes.left, sizes.right, sizes.top, sizes.bottom);
	}
}
