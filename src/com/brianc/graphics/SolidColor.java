package com.brianc.graphics;

import java.awt.Graphics2D;

import com.brianc.css.Color;
import com.brianc.layout.Rect;

public class SolidColor implements DisplayCommand {
	final Color color;
	final Rect rect;

	public SolidColor(Color color, Rect rect) {
		this.color = color;
		this.rect = rect;
	}	
	
	@Override
	public void paint(Graphics2D g) {
		g.setColor(new java.awt.Color(color.r, color.g, color.b, color.a));
		g.fill(rect);
	}
}
