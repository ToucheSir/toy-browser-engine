package com.brianc.layout;

import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
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
	private Deque<LineBox> lines;
	private Set<Fragment> fragments;

	// TODO add list

	public Set<Fragment> getFragments() {
		return fragments;
	}

	public InlineBox(StyledNode styledNode) {
		super();
		this.styledNode = styledNode;
		lines = new LinkedList<>();
		fragments = new HashSet<>();
	}

	@Override
	public BoxType getType() {
		return BoxType.INLINE_NODE;
	}

	@Override
	public StyledNode getStyledNode() {
		return styledNode;
	}

	public Deque<LineBox> getLines() {
		return lines;
	}

	private void layout(Dimensions containingBlock, InlineBox inlineRoot, Renderer renderBackend, LineBox lastLine) {
		layoutInlineMaxWidth(containingBlock);
		reCalcLines(containingBlock, inlineRoot, lastLine, renderBackend);

		for (LayoutBox child : children) {
			// TODO block children?
			InlineBox childAsInline = (InlineBox) child;
			childAsInline.layout(dimensions, inlineRoot, renderBackend, lastLine);

			lines.addAll(childAsInline.lines);
		}

		calculateInlinePosition(containingBlock);
	}

	private void calculateInlinePosition(Dimensions containingBlock) {
		Value zero = new LengthValue(0, Unit.PX);
		float maxLineWidth = (float) lines.stream().mapToDouble(l -> (double) l.filledWidth).max()
				.orElse(0);
		float totalHeight = (float) lines.stream().mapToDouble(l -> (double) l.maxHeight).sum();

		dimensions.margin.top = styledNode.lookup("margin-top", "margin", zero).toPx();
		dimensions.margin.bottom = styledNode.lookup("margin-bottom", "margin", zero).toPx();

		dimensions.border.top = styledNode.lookup("border-top-width", "border-width", zero).toPx();
		dimensions.border.bottom = styledNode.lookup("border-bottom-width", "border-width", zero).toPx();

		dimensions.padding.top = styledNode.lookup("padding-left", "padding", zero).toPx();
		dimensions.padding.bottom = styledNode.lookup("padding-right", "padding", zero).toPx();

		dimensions.content.width = maxLineWidth;
		dimensions.content.height = totalHeight;

		dimensions.content.x = containingBlock.content.x + dimensions.margin.left
				+ dimensions.padding.left + dimensions.border.left;
		dimensions.content.y = containingBlock.content.y;
	}

	@Override
	void layout(Dimensions containingBlock, Renderer renderBackend) {
		// TODO inline layout as per
		// http://www.w3.org/TR/CSS2/visuren.html#inline-boxes
		// and http://www.w3.org/TR/CSS2/visuren.html#inline-formatting
		if (lines.isEmpty()) {
			lines.add(new LineBox(this));
		}

		layout(containingBlock, this, renderBackend, getLastLine());
	}

	private LineBox getLastLine() {
		return lines.getLast();
	}

	private void reCalcLines(Dimensions dimensions, InlineBox inlineRoot, LineBox lastLine, Renderer renderBackend) {
		Node domNode = styledNode.getNode();

		if (domNode.getType() == NodeType.TEXT) {
			String nodeText = ((TextNode) domNode).getText();
			AttributedCharacterIterator iter = new AttributedString(nodeText).getIterator();
			FontRenderContext renderContext = renderBackend.getGraphicsContext()
					.getFontRenderContext();

			LineBreakMeasurer measurer = new LineBreakMeasurer(iter, renderContext);
			float contentMaxWidth = dimensions.content.width;
			LineBox currentLine = lastLine;

			int pos;
			while ((pos = measurer.getPosition()) < nodeText.length()) {
				float lineRemainingWidth = contentMaxWidth;

				if (currentLine.filledWidth < contentMaxWidth) {
					lineRemainingWidth = contentMaxWidth - currentLine.filledWidth;
				} else {
					currentLine = new LineBox(inlineRoot);
					lines.add(currentLine);
				}

				TextLayout lineLayout = measurer.nextLayout(lineRemainingWidth);

				Fragment textFrag = new Fragment(this, pos, measurer.getPosition(), lineLayout);
				fragments.add(textFrag);

				currentLine.addFragment(textFrag);
			}
		}
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

		float total = (float) Stream
				.of(marginLeft, marginRight, borderLeft, borderRight, paddingLeft, paddingRight,
						width).mapToDouble(v -> v.toPx()).sum();

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
		private float maxHeight;
		private float lineHeight;
		private InlineBox inlineRoot;
		
		public LineBox(InlineBox inlineRoot) {
			this.inlineRoot = inlineRoot;
			fragments = new ArrayList<>();
		}

		public void addFragment(Fragment fragment) {
			fragments.add(fragment);
			filledWidth += fragment.lineLayout.getAdvance();
			TextLayout layout = fragment.getLayout();
			
			// TODO: This assumes a default line-height of 1.0. 
			float fragmentHeight = layout.getAscent() - layout.getDescent();
			maxHeight = Math.max(maxHeight, fragmentHeight);
			
			if (inlineRoot.children.contains(fragment.box)) {
				lineHeight = fragmentHeight;
			}
		}

		public float getFilledWidth() {
			return filledWidth;
		}

		public List<Fragment> getFragments() {
			return fragments;
		}

		public float getBoxHeight() {
			return maxHeight;
		}
		
		public float getLineHeight() {
			return lineHeight;
		}
	}

}
