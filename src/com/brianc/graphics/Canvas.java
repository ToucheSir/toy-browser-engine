package com.brianc.graphics;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.brianc.css.Color;
import com.brianc.layout.Rect;

public class Canvas {
	private BufferedImage buffer;
	private Graphics2D g;
	private int width;
	private int height;
	
	public Canvas(int width, int height) {
		this.width = width;
		this.height = height;
		buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		g = buffer.createGraphics();
	}
	
	void paintItem(DisplayCommand item) {
		item.paint(g, this);
	}
	
	void endPaint() {
		g.dispose();
	}
	
	public BufferedImage getBuffer() {
		return buffer;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
