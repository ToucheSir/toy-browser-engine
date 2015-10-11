package com.brianc.painting;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.brianc.css.Color;
import com.brianc.layout.Rect;

public class Canvas {
	private BufferedImage buffer;
	private Graphics2D g;
	
	public Canvas(int width, int height) {
		buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		g = buffer.createGraphics();
	}
	
	void paintItem(DisplayCommand item) {
		if (item instanceof SolidColor) {
			Color color = ((SolidColor)item).color;
			Rect rect = ((SolidColor)item).rect;
			System.out.println(rect);
			g.setColor(new java.awt.Color(color.r, color.g, color.b, color.a));
			g.fill(rect);
		}
	}
	
	void endPaint() {
		g.dispose();
	}
	
	public BufferedImage getBuffer() {
		return buffer;
	}
}
