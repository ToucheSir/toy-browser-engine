package com.brianc.layout;

public class Dimensions {
	public final Rect content;
	public final EdgeSizes padding;
	public final EdgeSizes border;
	public final EdgeSizes margin;
	
	public Dimensions(Rect content, EdgeSizes padding, EdgeSizes border, EdgeSizes margin) {
		this.content = content;
		this.padding = padding;
		this.border = border;
		this.margin = margin;
	}	
	
	public Dimensions() {
		this(new Rect(), new EdgeSizes(), new EdgeSizes(), new EdgeSizes());
	}

	
	public Dimensions(Dimensions dims) {
		this(new Rect(dims.content), new EdgeSizes(dims.padding), new EdgeSizes(dims.border), new EdgeSizes(dims.margin));
	}

	public Rect paddingBox() {
		return content.expandedBy(padding);
	}
	
	public Rect borderBox() {
		return paddingBox().expandedBy(border);
	}
	
	public Rect marginBox() {
		return borderBox().expandedBy(margin);
	}
}

