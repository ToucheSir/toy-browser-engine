package com.brianc.graphics;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.Scanner;

import com.brianc.css.Color;
import com.brianc.layout.Rect;

public class RenderText implements DisplayCommand {
	private String text;
	private Rect rect;
	private Color color;

	public RenderText(String text, Color color, Rect rect) {
		this.text = text;
		this.rect = rect;
		this.color = color;
	}
	
	@Override
	public void paint(Graphics2D g, Canvas canvas) {
		g.setColor(new java.awt.Color(color.r, color.g, color.b, color.a));
		FontMetrics currentFontMetrics = g.getFontMetrics();
		int textLengthPx = currentFontMetrics.stringWidth(text);
		// FIXME this should *really* be the rect width, but there's no layout code
		// for inline elements nor any cascading or inheritance of styles
		int maxWidth = canvas.getWidth();
		int drawX = (int)rect.x;
		int drawY = (int)rect.y + currentFontMetrics.getHeight();
		
		if (textLengthPx <= maxWidth) {
			g.drawString(text, drawX, drawY);
		} else {
			Scanner wordIter = new Scanner(text);
			StringBuilder currentLine = new StringBuilder();
			
			while (wordIter.hasNext()) {
				
			}
		}
		
	}
}
