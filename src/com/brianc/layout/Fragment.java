package com.brianc.layout;

import java.awt.font.TextLayout;
import java.util.BitSet;

import com.brianc.style.StyledNode;

public class Fragment {
	StyledNode nodeRef;
	InlineBox box;
	int startPos;
	int endPos;
	TextLayout lineLayout;
	BitSet flags;

	public static final int FIRST_FOR_ELEMENT = 1;
	public static final int LAST_FOR_ELEMENT = 2;

	public Fragment(InlineBox box, int startPos, int endPos, TextLayout lineLayout) {
		this.box = box;
		this.nodeRef = box.getStyledNode();
		this.lineLayout = lineLayout;
		this.startPos = startPos;
		this.endPos = endPos;
		this.flags = new BitSet();
	}
	
	public BitSet getFlags() {
		return flags;
	}

	public TextLayout getLayout() {
		return lineLayout;
	}
}
