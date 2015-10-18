package com.brianc.graphics;

import java.awt.Graphics2D;

public interface DisplayCommand {
	void paint(Graphics2D g, Canvas canvas);
}
