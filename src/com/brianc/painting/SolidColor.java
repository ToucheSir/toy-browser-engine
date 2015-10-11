package com.brianc.painting;

import com.brianc.css.Color;
import com.brianc.layout.Rect;

public class SolidColor implements DisplayCommand {
	final Color color;
	final Rect rect;

	public SolidColor(Color color, Rect rect) {
		this.color = color;
		this.rect = rect;
	}	
}
