package com.brianc.layout;

import java.util.Optional;
import java.util.stream.Stream;

import com.brianc.css.KeywordValue;
import com.brianc.css.LengthValue;
import com.brianc.css.Unit;
import com.brianc.css.Value;
import com.brianc.graphics.Renderer;
import com.brianc.style.StyledNode;

public class BlockBox extends LayoutBox implements StyledLayoutBox {
	private final StyledNode styledNode;
	
	public BlockBox(final StyledNode styledNode) {
		super();
		this.styledNode = styledNode;
	}

	@Override
	public BoxType getType() {
		return BoxType.BLOCK_NODE;
	}

	@Override
	void layout(Dimensions containingBlock, Renderer renderBackend) {
		calculateBlockWidth(containingBlock);
		calculateBlockPosition(containingBlock);
		layoutBlockChildren(renderBackend);
		calculateBlockHeight();
	}

	private void calculateBlockHeight() {
		Optional<Value> heightValue = styledNode.value("height");
		if (heightValue.isPresent() && heightValue.get() instanceof LengthValue) {
			dimensions.content.height = heightValue.get().toPx();
		}
	}

	private void layoutBlockChildren(Renderer renderBackend) {
		for (LayoutBox child : children) {
			child.layout(dimensions, renderBackend);

			dimensions.content.height += child.dimensions.marginBox().height;
		}
	}

	private void calculateBlockPosition(Dimensions containingBlock) {
		Value zero = new LengthValue(0, Unit.PX);

		dimensions.margin.top = styledNode.lookup("margin-top", "margin", zero).toPx();
		dimensions.margin.bottom = styledNode.lookup("margin-bottom", "margin", zero).toPx();

		dimensions.margin.top = styledNode.lookup("border-left", "border", zero).toPx();
		dimensions.margin.bottom = styledNode.lookup("border-right", "border", zero).toPx();

		dimensions.padding.top = styledNode.lookup("padding-left", "padding", zero).toPx();
		dimensions.padding.bottom = styledNode.lookup("padding-right", "padding", zero).toPx();

		dimensions.content.x = containingBlock.content.x + dimensions.margin.left
				+ dimensions.border.left + dimensions.padding.left;

		dimensions.content.y = containingBlock.content.height + containingBlock.content.y
				+ dimensions.margin.top + dimensions.border.top + dimensions.padding.left;

	}

	private void calculateBlockWidth(Dimensions containingBlock) {
		Value auto = new KeywordValue("auto");
		Value width = styledNode.value("width").orElse(auto);
		Value zero = new LengthValue(0, Unit.PX);

		Value marginLeft = styledNode.lookup("margin-left", "margin", zero);
		Value marginRight = styledNode.lookup("margin-right", "margin", zero);

		Value borderLeft = styledNode.lookup("border-left-width", "border-width", zero);
		Value borderRight = styledNode.lookup("border-right-width", "border-width", zero);

		Value paddingLeft = styledNode.lookup("padding-left", "padding", zero);
		Value paddingRight = styledNode.lookup("padding-right", "padding", zero);

		float total = (float) Stream.of(marginLeft, marginRight, borderLeft, borderRight,
				paddingLeft, paddingRight, width).mapToDouble(v -> v.toPx()).sum();

		if (!width.equals(auto) && total > containingBlock.content.width) {
			if (marginLeft.equals(auto)) {
				marginLeft = zero;
			}

			if (marginRight.equals(auto)) {
				marginRight = zero;
			}
		}

		float underflow = containingBlock.content.width - total;

		boolean widthIsAuto = width.equals(auto);
		boolean leftIsAuto = marginLeft.equals(auto);
		boolean rightIsAuto = marginRight.equals(auto);

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
			if (marginLeft.equals(auto)) {
				marginLeft = zero;
			}
			
			if (marginRight.equals(auto)) {
				marginRight = zero;
			}

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

	@Override
	public StyledNode getStyledNode() {
		return styledNode;
	}
}
