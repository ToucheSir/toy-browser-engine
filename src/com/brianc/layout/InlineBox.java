package com.brianc.layout;

import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import com.brianc.css.KeywordValue;
import com.brianc.css.LengthValue;
import com.brianc.css.Unit;
import com.brianc.css.Value;
import com.brianc.dom.Node;
import com.brianc.dom.NodeType;
import com.brianc.dom.TextNode;
import com.brianc.graphics.Renderer;
import com.brianc.style.StyledNode;

public class InlineBox extends LayoutBox implements StyledLayoutBox {
	private StyledNode styledNode;
	private List<LineBox> lines;
	private List<Fragment> fragments;
	// TODO add list
	
	public InlineBox(StyledNode styledNode) {
		super();
		this.styledNode = styledNode;
		lines = new ArrayList<>();
		fragments = new ArrayList<>();
	}

	@Override
	public BoxType getType() {
		return BoxType.INLINE_NODE;
	}
	
	@Override
	public StyledNode getStyledNode() {
		return styledNode;
	}
	
	public List<LineBox> getLines() {
		return lines;
	}
	
	private void layout(Dimensions containingBlock, Renderer renderBackend, LineBox lastLine) {
		layoutInlineMaxWidth(containingBlock);
		reCalcLines(containingBlock, lastLine, renderBackend);

		for (LayoutBox child : children) {
			// TODO block children?
			InlineBox childAsInline = (InlineBox)child;
			childAsInline.layout(dimensions, renderBackend, lastLine);
			
			lines.addAll(childAsInline.lines);
		}
	}
	
	@Override
	void layout(Dimensions containingBlock, Renderer renderBackend) {
		// TODO inline layout as per
		// http://www.w3.org/TR/CSS2/visuren.html#inline-boxes
		// and http://www.w3.org/TR/CSS2/visuren.html#inline-formatting
		if (lines.isEmpty()) {
			lines.add(new LineBox());
		}
		
		layout(containingBlock, renderBackend, getLastLine());
	}
	
	private LineBox getLastLine() {
		return lines.get(lines.size() - 1);
	}
	
	private void reCalcLines(Dimensions dimensions, LineBox lastLine, Renderer renderBackend) {
		Node domNode = styledNode.getNode();
		
		if (domNode.getType() == NodeType.TEXT) {
			String nodeText = ((TextNode)domNode).getText();
			AttributedCharacterIterator iter = new AttributedString(nodeText).getIterator();
			FontRenderContext renderContext = renderBackend.getGraphicsContext().getFontRenderContext();

			LineBreakMeasurer measurer = new LineBreakMeasurer(iter, renderContext);
			float contentMaxWidth = dimensions.content.width;
			LineBox currentLine = lastLine;
			
			int pos;
			while ((pos = measurer.getPosition()) < nodeText.length()) {
				float lineRemainingWidth = contentMaxWidth;
				
				if (currentLine.filledWidth < contentMaxWidth) {
					lineRemainingWidth = contentMaxWidth - currentLine.filledWidth;
				} else {
					currentLine = new LineBox();
					lines.add(currentLine);
				}

				TextLayout lineLayout = measurer.nextLayout(lineRemainingWidth);

				Fragment textFrag = new Fragment(this, pos, measurer.getPosition(), lineLayout);
				fragments.add(textFrag);

				currentLine.addFragment(textFrag);
			}
		}
	}
	
	private void layoutText(Dimensions containingBlock, TextNode domNode, Renderer renderBackend) {

	}
	
	private void layoutInlineMaxWidth(Dimensions containingBlock) {
		Value auto = new KeywordValue("auto");
		Value width = auto;
		Value zero = new LengthValue(0, Unit.PX);

		Value marginLeft = styledNode.lookup("margin-left", "margin", zero);
		Value marginRight = styledNode.lookup("margin-right", "margin", zero);

		Value borderLeft = styledNode.lookup("border-left-width", "border-width", zero);
		Value borderRight = styledNode.lookup("border-right-width", "border-width", zero);

		Value paddingLeft = styledNode.lookup("padding-left", "padding", zero);
		Value paddingRight = styledNode.lookup("padding-right", "padding", zero);

		float total = (float) Stream.of(marginLeft, marginRight, borderLeft, borderRight,
				paddingLeft, paddingRight, width).mapToDouble(v -> v.toPx()).sum();
	

		float containingWidth = containingBlock.content.width;
		if (total > containingWidth) {
			if (marginLeft.equals(auto)) {
				marginLeft = zero;
			}

			if (marginRight.equals(auto)) {
				marginRight = zero;
			}
		}

		boolean leftIsAuto = marginLeft.equals(auto);
		boolean rightIsAuto = marginRight.equals(auto);

		if (leftIsAuto) {
			marginLeft = zero;
		}
		
		if (rightIsAuto) {
			marginRight = zero;
		}

		dimensions.padding.left = paddingLeft.toPx();
		dimensions.padding.right = paddingRight.toPx();

		dimensions.border.left = borderLeft.toPx();
		dimensions.border.right = borderRight.toPx();

		dimensions.margin.left = marginLeft.toPx();
		dimensions.margin.right = marginRight.toPx();
		
		if (containingWidth > total) {
			dimensions.content.width = containingWidth - total;
		} else {
			dimensions.content.width = 0;
		}
	}


	public class LineBox {
		private List<Fragment> fragments;
		private float filledWidth;
		
		public LineBox() {
			fragments = new ArrayList<>();
		}

		public void addFragment(Fragment fragment) {
			fragments.add(fragment);
			filledWidth += fragment.lineLayout.getAdvance();
		}

		public List<Fragment> getFragments() {
			return fragments;
		}

	}
	
}
