package com.brianc.layout;

import java.awt.geom.Rectangle2D.Float;

@SuppressWarnings("serial")
public class Rect extends Float {
	public Rect() {
		super();
	}

	public Rect(float x, float y, float w, float h) {
		super(x, y, w, h);
	}

	public Rect(Rect rect) {
		this(rect.x, rect.y, rect.width, rect.height);
	}

	public Rect expandedBy(EdgeSizes edge) {
		return new Rect(x - edge.left, y - edge.right, width + edge.left + edge.right,
				height + edge.top + edge.bottom);
	}
}
