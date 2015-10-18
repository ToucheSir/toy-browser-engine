package com.brianc.layout;

import java.util.List;

import com.brianc.dom.Node;
import com.brianc.style.StyledNode;

public class InlineBox extends LayoutBox implements StyledLayoutBox {
	private StyledNode styledNode;
	private List<LineBox> lines;
	// TODO add list
	
	public InlineBox(StyledNode styledNode) {
		super();
		this.styledNode = styledNode;
	}

	@Override
	public BoxType getType() {
		return BoxType.INLINE_NODE;
	}
	
	@Override
	public StyledNode getStyledNode() {
		return styledNode;
	}
	
	void layout(Dimensions containingBlock) {
		// TODO inline layout as per
		// http://www.w3.org/TR/CSS2/visuren.html#inline-boxes
		// and http://www.w3.org/TR/CSS2/visuren.html#inline-formatting
		Node domNode = styledNode.getNode();
		

		calculateInlineWidth(containingBlock);
		calculateInlinePosition(containingBlock);
		//		layoutInlineChildren();
		calculateInlineHeight();
	}

	private void calculateInlineHeight() {
	}

	private void calculateInlinePosition(Dimensions containingBlock) {
	}

	private void calculateInlineWidth(Dimensions containingBlock) {
	}
}

class LineBox {
	
}
