package com.brianc.layout;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.brianc.css.KeywordValue;
import com.brianc.css.LengthValue;
import com.brianc.css.Unit;
import com.brianc.css.Value;
import com.brianc.style.StyledNode;

public class LayoutBox {
	private Dimensions dimensions;
	private BoxType boxType;
	private List<LayoutBox> children;

	public LayoutBox(BoxType boxType) {
		this(new Dimensions(), boxType, new ArrayList<>());
	}

	public LayoutBox(Dimensions dimensions, BoxType boxType, List<LayoutBox> children) {
		this.dimensions = dimensions;
		this.boxType = boxType;
		this.children = children;
	}
	
	public List<LayoutBox> getChildren() {
		return children;
	}
	
	public Dimensions getDimensions() {
		return dimensions;
	}
	
	public BoxType getType() {
		return boxType;
	}

	public static LayoutBox layoutTree(StyledNode node, Dimensions containingBlock) {
		containingBlock.content.height = 0;
		
		LayoutBox rootBox = buildLayoutTree(node);
		rootBox.layout(containingBlock);
		return rootBox;
	}
	
	private static LayoutBox buildLayoutTree(StyledNode styleNode) {
		LayoutBox root = new LayoutBox(BoxType.Type.fromDisplay(styleNode.display(), styleNode));

		for (StyledNode child : styleNode.children) {
			switch (child.display()) {
			case BLOCK:
				root.children.add(buildLayoutTree(child));
				break;
			case INLINE:
				root.getInlineContainer().children.add(buildLayoutTree(child));
				break;
			case NONE:
			default:
				break;
			}
		}

		return root;
	}

	private LayoutBox getInlineContainer() {
		switch (boxType.getType()) {
		case INLINE_NODE:
		case ANONYMOUS_BLOCK:
			return this;
		case BLOCK_NODE:
			// TODO: do not generate unecessary anonymous block box if block node
			// only has inline child(ren)
			if (children.isEmpty()) {
				children.add(new LayoutBox(new AnonymousBlock()));
			}
			
			return children.get(children.size() - 1);
		default:
			throw new IllegalStateException("layout box does not have an inline container");
		}
	}

	private void layout(Dimensions containingBlock) {
		switch (boxType.getType()) {
		case BLOCK_NODE:
			layoutBlock(containingBlock);
			break;
		case INLINE_NODE:
			layoutInline(containingBlock);
		case ANONYMOUS_BLOCK:
		default:
		}
	}

	private void layoutInline(Dimensions containingBlock) {
		// TODO inline layout as per http://www.w3.org/TR/CSS2/visuren.html#inline-boxes
		// and http://www.w3.org/TR/CSS2/visuren.html#inline-formatting
		
	}

	void layoutBlock(Dimensions containingBlock) {
		calculateBlockWidth(containingBlock);
		calculateBlockPosition(containingBlock);
		layoutBlockChildren();
		calculateBlockHeight();
	}

	private void calculateBlockHeight() {
		Optional<Value> heightValue = getStyleNode().value("height");
		if (heightValue.isPresent() && heightValue.get() instanceof LengthValue) {
			dimensions.content.height = heightValue.get().toPx();
		}
	}

	private void layoutBlockChildren() {
		for (LayoutBox child : children) {
			child.layout(dimensions);
			
			dimensions.content.height = dimensions.content.height + child.dimensions.marginBox().height;
		}
	}

	private void calculateBlockPosition(Dimensions containingBlock) {
		StyledNode style = getStyleNode();
		Value zero = new LengthValue(0, Unit.PX);

		dimensions.margin.top = style.lookup("margin-top", "margin", zero).toPx();
		dimensions.margin.bottom = style.lookup("margin-bottom", "margin", zero).toPx();

		dimensions.margin.top = style.lookup("border-left", "border", zero).toPx();
		dimensions.margin.bottom = style.lookup("border-right", "border", zero).toPx();

		dimensions.padding.top = style.lookup("padding-left", "padding", zero).toPx();
		dimensions.padding.bottom = style.lookup("padding-right", "padding", zero).toPx();

		dimensions.content.x = containingBlock.content.x + dimensions.margin.left
				+ dimensions.border.left + dimensions.padding.left;

		dimensions.content.y = containingBlock.content.height + containingBlock.content.y
				+ dimensions.margin.top + dimensions.border.top + dimensions.padding.left;

	}

	private void calculateBlockWidth(Dimensions containingBlock) {
		StyledNode style = getStyleNode();
		Value auto = new KeywordValue("auto");
		Value width = style.value("width").orElse(auto);
		Value zero = new LengthValue(0, Unit.PX);

		Value marginLeft = style.lookup("margin-left", "margin", zero);
		Value marginRight = style.lookup("margin-right", "margin", zero);

		Value borderLeft = style.lookup("border-left-width", "border-width", zero);
		Value borderRight = style.lookup("border-right-width", "border-width", zero);

		Value paddingLeft = style.lookup("padding-left", "padding", zero);
		Value paddingRight = style.lookup("padding-right", "padding", zero);

		float total = (float) Stream.of(marginLeft, marginRight, borderLeft,
				borderRight, paddingLeft, paddingRight, width).mapToDouble(v -> v.toPx()).sum();

		if (width != auto && total > containingBlock.content.width) {
			if (marginLeft == auto) {
				marginLeft = zero;
			}

			if (marginRight == auto) {
				marginRight = zero;
			}
		}

		float underflow = containingBlock.content.width - total;

		boolean widthIsAuto = width == auto;
		boolean leftIsAuto = marginLeft == auto;
		boolean rightIsAuto = marginRight == auto;

		if (!widthIsAuto) {
			if (!leftIsAuto && rightIsAuto) {
				marginRight = new LengthValue(underflow, Unit.PX);
			} else if (leftIsAuto && !rightIsAuto) {
				marginLeft = new LengthValue(underflow, Unit.PX);
			} else if (!leftIsAuto && !rightIsAuto) {
				marginRight = new LengthValue(marginRight.toPx() + underflow, Unit.PX);
			} else {
				marginLeft = new LengthValue(underflow / 2, Unit.PX);
				marginRight = new LengthValue(underflow / 2, Unit.PX);
			}
		} else {
			if (marginLeft == auto)
				marginLeft = zero;
			if (marginRight == auto)
				marginRight = zero;

			if (underflow >= 0) {
				width = new LengthValue(underflow, Unit.PX);
			} else {
				width = zero;
				marginRight = new LengthValue(marginRight.toPx() + underflow, Unit.PX);
			}
		}

		dimensions.content.width = width.toPx();

		dimensions.padding.left = paddingLeft.toPx();
		dimensions.padding.right = paddingRight.toPx();

		dimensions.border.left = borderLeft.toPx();
		dimensions.border.right = borderRight.toPx();

		dimensions.margin.left = marginLeft.toPx();
		dimensions.margin.right = marginRight.toPx();
	}

	private StyledNode getStyleNode() {
		switch (boxType.getType()) {
		case BLOCK_NODE:
			return ((BlockNode)boxType).node;
		case INLINE_NODE:
			return ((InlineNode)boxType).node;
		case ANONYMOUS_BLOCK:
		default:
			throw new IllegalAccessError("Anonymous block box has no style node");
		}
	}
	
	@Override
	public String toString() {
		return stringifyLayoutBox(this);
	}

	private static String stringifyLayoutBox(LayoutBox box, String lastIndent) {
		StringBuilder res = new StringBuilder();
		String indent = lastIndent + "  ";

		res.append(lastIndent).append(box.boxType).append(" {\n");

		for (LayoutBox child : box.children) {
			res.append(stringifyLayoutBox(child, indent)).append("\n");
		}
		
		res.append(lastIndent).append("}");
		return res.toString();
	}
	
	private static String stringifyLayoutBox(LayoutBox box) {
		return stringifyLayoutBox(box, "");
	}

}
