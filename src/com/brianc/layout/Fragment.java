package com.brianc.layout;

import java.awt.font.TextLayout;

import com.brianc.style.StyledNode;

public class Fragment {
	StyledNode nodeRef;
	InlineBox box;
	int startPos;
	int endPos;
	TextLayout lineLayout;

	public Fragment(InlineBox box, int startPos, int endPos, TextLayout lineLayout) {
		this.box = box;
		this.nodeRef = box.getStyledNode();
		this.startPos = startPos;
		this.endPos = endPos;
	}
}
