package com.brianc.graphics;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

import com.brianc.css.Color;
import com.brianc.css.ColorValue;
import com.brianc.dom.Node;
import com.brianc.dom.NodeType;
import com.brianc.dom.TextNode;
import com.brianc.layout.Dimensions;
import com.brianc.layout.InlineBox;
import com.brianc.layout.InlineBox.LineBox;
import com.brianc.layout.LayoutBox;
import com.brianc.layout.Rect;
import com.brianc.layout.StyledLayoutBox;

public class Painter {
	private static List<DisplayCommand> buildDisplayList(LayoutBox layoutRoot) {
		List<DisplayCommand> displayList = new ArrayList<>();
		renderLayoutBox(displayList, layoutRoot);

		return displayList;
	}

	private static void renderLayoutBox(List<DisplayCommand> displayList, LayoutBox layoutBox) {
		System.out.println(layoutBox);
		switch (layoutBox.getType()) {
		case BLOCK_NODE:
			renderBackground(displayList, layoutBox);
			renderBorders(displayList, layoutBox);
			for (LayoutBox child : layoutBox.getChildren()) {
				renderLayoutBox(displayList, child);
			}
			break;
		case INLINE_NODE:
			InlineBox box = (InlineBox) layoutBox;
			Optional<Color> colorVal = getColor(layoutBox, "border-color");
			Dimensions dims = layoutBox.getDimensions();
			Rect borderBox = dims.borderBox();
			Deque<LineBox> lines = box.getLines();

			if (colorVal.isPresent()) {
				Color color = colorVal.get();
				LineBox firstLine = lines.getFirst();
				float boxHeight = firstLine.getBoxHeight();
				float borderHeight = firstLine.getLineHeight() + dims.border.top + dims.border.bottom;

				// left border
				displayList.add(new SolidColor(color, new Rect(borderBox.x, borderBox.y + boxHeight - borderHeight,
						dims.border.left, borderHeight)));

				// right border
				displayList.add(new SolidColor(color, new Rect(borderBox.x + borderBox.width
						- dims.border.right, borderBox.y + borderBox.height - 2 * borderHeight
						- dims.border.top, dims.border.right, borderHeight)));

				float lineX = dims.content.x;
				float lineY = dims.content.y;

				for (LineBox line : box.getLines()) {
					// TODO proper line-height calculations
					float lineHeight = line.getLineHeight();
					float lineWidth = line.getFilledWidth();

					// top border
					displayList.add(new SolidColor(color, new Rect(lineX, lineY - dims.border.top,
							lineWidth, dims.border.top)));
					// bottom border
					displayList.add(new SolidColor(color, new Rect(lineX, lineY + lineHeight,
							lineWidth, dims.border.bottom)));

					lineX += lineWidth;
					lineY += lineHeight;
				}
			}
			break;
		default:
			break;
		}
	}

	private static void renderText(List<DisplayCommand> displayList, InlineBox layoutBox) {
		// there needs to be some major refactoring before this is less ugly.
		// TODO eliminate the current wonky lookup for extracting a node
		// TODO include the parent box when rendering text without using this
		// check
		// FIXME text rendering is not contained because inline layout and
		// cascading(?) do not exist yet.
		Node sourceNode = layoutBox.getStyledNode().getNode();

		if (sourceNode.getType() == NodeType.TEXT) {
			Optional<Color> colorVal = getColor(layoutBox, "color");
			Color fontColor = colorVal.orElse(Color.BLACK);

			Dimensions dims = layoutBox.getDimensions();
			Rect paddingBox = dims.paddingBox();
			String text = ((TextNode) sourceNode).getText();
			Deque<LineBox> lines = layoutBox.getLines();

			// displayList.add(new RenderText(text, lines, paddingBox,
			// fontColor));
		}

	}

	private static void renderBorders(List<DisplayCommand> displayList, LayoutBox layoutBox) {
		Optional<Color> colorVal = getColor(layoutBox, "border-color");

		if (colorVal.isPresent()) {
			Color color = colorVal.get();
			Dimensions dims = layoutBox.getDimensions();
			Rect borderBox = dims.borderBox();

			// left border
			displayList.add(new SolidColor(color, new Rect(borderBox.x, borderBox.y,
					dims.border.left, borderBox.height)));
			// right border
			displayList.add(new SolidColor(color, new Rect(borderBox.x + borderBox.width
					- dims.border.right, borderBox.y, dims.border.right, borderBox.height)));
			// top border
			displayList.add(new SolidColor(color, new Rect(borderBox.x, borderBox.y,
					borderBox.width, dims.border.top)));
			// bottom border
			displayList.add(new SolidColor(color, new Rect(borderBox.x, borderBox.y
					+ borderBox.height - dims.border.bottom, borderBox.width, dims.border.bottom)));
		}
	}

	private static void renderBackground(List<DisplayCommand> displayList, LayoutBox layoutBox) {
		getColor(layoutBox, "background").map(
				color -> displayList.add(new SolidColor(color, layoutBox.getDimensions()
						.borderBox())));
	}

	private static Optional<Color> getColor(LayoutBox layoutBox, String name) {
		switch (layoutBox.getType()) {
		case BLOCK_NODE:
		case INLINE_NODE:
			return ((StyledLayoutBox) layoutBox).getStyledNode().value(name)
					.filter(val -> (val instanceof ColorValue))
					.map(val -> ((ColorValue) val).getColor());
		case ANONYMOUS_BLOCK:
		default:
			return Optional.empty();
		}
	}

	public static void paint(LayoutBox layoutRoot, Rect bounds, Renderer renderBackend) {
		List<DisplayCommand> displayList = buildDisplayList(layoutRoot);
		Graphics2D graphicsContext = renderBackend.getGraphicsContext();
		System.out.println(displayList);

		for (DisplayCommand item : displayList) {
			item.paint(graphicsContext);
		}

		graphicsContext.dispose();
	}
}
