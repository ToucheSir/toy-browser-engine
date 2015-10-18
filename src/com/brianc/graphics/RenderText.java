package com.brianc.graphics;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.List;

import com.brianc.css.Color;
import com.brianc.layout.InlineBox.LineBox;
import com.brianc.layout.Rect;

public class RenderText implements DisplayCommand {
	private String text;
	private List<LineBox> lines;
	private Rect rect;
	private Color color;

	public RenderText(String text, List<LineBox> lines, Rect rect, Color fontColor) {
		this.text = text;
		this.lines = lines;
		this.rect = rect;
		this.color = fontColor;
	}
	
	@Override
	public void paint(Graphics2D g) {
		Point2D.Float drawCoords = new Point2D.Float(rect.x, rect.y);
		g.setColor(new java.awt.Color(color.r, color.g, color.b, color.a));

		for (LineBox line : lines) {
/*			TextLayout lineLayout = line.getLayout();
			
			drawCoords.y += lineLayout.getAscent();
			float dx = lineLayout.isLeftToRight() ? 0 : (rect.width - lineLayout.getAdvance());
			
			lineLayout.draw(g, drawCoords.x + dx, drawCoords.y);
			drawCoords.y += lineLayout.getDescent() + lineLayout.getLeading();*/
		}
	}
}
