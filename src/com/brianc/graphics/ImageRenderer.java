package com.brianc.graphics;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import com.brianc.layout.Rect;

public class ImageRenderer implements Renderer {
	private Graphics2D graphicsContext;
	private BufferedImage buffer;
	
	public ImageRenderer(Rect dimensions) {
		buffer = new BufferedImage((int)dimensions.width, (int)dimensions.height, BufferedImage.TYPE_INT_ARGB);
		graphicsContext = buffer.createGraphics();
	}
	
	public Graphics2D getGraphicsContext() {
		return graphicsContext;
	}

	public BufferedImage getBuffer() {
		return buffer;
	}
}
