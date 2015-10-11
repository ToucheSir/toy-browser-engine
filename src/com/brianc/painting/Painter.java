package com.brianc.painting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.brianc.css.Color;
import com.brianc.css.ColorValue;
import com.brianc.layout.BoxType;
import com.brianc.layout.Dimensions;
import com.brianc.layout.LayoutBox;
import com.brianc.layout.Rect;

public class Painter {
	private static List<DisplayCommand> buildDisplayList(LayoutBox layoutRoot) {
		List<DisplayCommand> displayList = new ArrayList<>();
		renderLayoutBox(displayList, layoutRoot);

		return displayList;
	}

	private static void renderLayoutBox(List<DisplayCommand> displayList, LayoutBox layoutBox) {
		renderBackground(displayList, layoutBox);
		renderBorders(displayList, layoutBox);

		for (LayoutBox child : layoutBox.getChildren()) {
			renderLayoutBox(displayList, child);
		}
	}

	private static void renderBorders(List<DisplayCommand> displayList, LayoutBox layoutBox) {
		Optional<Color> colorVal = getColor(layoutBox, "border-color");

		if (colorVal.isPresent()) {
			Color color = colorVal.get();
			Dimensions dims = layoutBox.getDimensions();
			Rect borderBox = dims.borderBox();

			// left border
			displayList.add(new SolidColor(color,
					new Rect(borderBox.x, borderBox.y, dims.border.left, borderBox.height)));
			// right border
			displayList.add(new SolidColor(color,
					new Rect(borderBox.x + borderBox.width - dims.border.right, borderBox.y,
							dims.border.right, borderBox.height)));
			// top border
			displayList.add(new SolidColor(color,
					new Rect(borderBox.x, borderBox.y, borderBox.width, dims.border.top)));
			// bottom border
			displayList
					.add(new SolidColor(color,
							new Rect(borderBox.x,
									borderBox.y + borderBox.height - dims.border.bottom,
									borderBox.width, dims.border.bottom)));
		}
	}

	private static void renderBackground(List<DisplayCommand> displayList, LayoutBox layoutBox) {
		getColor(layoutBox, "background").map(color -> displayList
				.add(new SolidColor(color, layoutBox.getDimensions().borderBox())));
	}

	private static Optional<Color> getColor(LayoutBox layoutBox, String name) {
		BoxType boxType = layoutBox.getType();
		switch (boxType.getType()) {
		case BLOCK_NODE:
		case INLINE_NODE:
			return boxType.getStyle().value(name).filter(val -> (val instanceof ColorValue))
					.map(val -> ((ColorValue) val).getColor());
		case ANONYMOUS_BLOCK:
		default:
			return Optional.empty();
		}
	}
	
	public static Canvas paint(LayoutBox layoutRoot, Rect bounds) {
		List<DisplayCommand> displayList = buildDisplayList(layoutRoot);
		Canvas canvas = new Canvas((int)bounds.width, (int)bounds.height);
		System.out.println(displayList);
		
		for (DisplayCommand item : displayList) {
			canvas.paintItem(item);
		}
		
		canvas.endPaint();
		return canvas;
	}
}
