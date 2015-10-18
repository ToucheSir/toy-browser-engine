package com.brianc.layout;

import java.util.ArrayList;
import java.util.List;

import com.brianc.graphics.Renderer;
import com.brianc.style.Display;
import com.brianc.style.StyledNode;

public abstract class LayoutBox {
	protected Dimensions dimensions;
	protected List<LayoutBox> children;

	public abstract BoxType getType();

	public LayoutBox() {
		this(new Dimensions(), new ArrayList<>());
	}

	public LayoutBox(Dimensions dimensions, List<LayoutBox> children) {
		this.dimensions = dimensions;
		this.children = children;
	}

	public LayoutBox(Dimensions dimensions, List<LayoutBox> children, StyledNode styledNode) {
		this(dimensions, children);
	}

	public List<LayoutBox> getChildren() {
		return children;
	}

	public Dimensions getDimensions() {
		return dimensions;
	}

	public static LayoutBox layoutTree(StyledNode node, Dimensions containingBlock, Renderer imgRenderer) {
		containingBlock.content.height = 0;

		LayoutBox rootBox = buildLayoutTree(node);
		rootBox.layout(containingBlock);
		return rootBox;
	}

	private static LayoutBox fromDisplay(Display display, StyledNode styleNode) {
		switch (display) {
		case BLOCK:
			return new BlockBox(styleNode);
		case INLINE:
			return new InlineBox(styleNode);
		case NONE:
		default:
			throw new IllegalArgumentException(String
					.format("Display mode '%s' must be one of 'INLINE' or 'BLOCK'", display));
		}
	}

	private static LayoutBox buildLayoutTree(StyledNode styleNode) {
		LayoutBox root = fromDisplay(styleNode.display(), styleNode);

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
		switch (getType()) {
		case INLINE_NODE:
		case ANONYMOUS_BLOCK:
			return this;
		case BLOCK_NODE:
			// TODO: do not generate unnecessary anonymous block box if block
			// node
			// only has inline child(ren)
			if (children.stream().allMatch(c -> c.getType() == BoxType.INLINE_NODE)) {
				return this;
			} else {
				children.add(new AnonymousBlockBox());
				return children.get(children.size() - 1);
			}
		default:
			throw new IllegalStateException("layout box does not have an inline container");
		}
	}

	abstract void layout(Dimensions containingBlock); 
/*	{ switch (getType()) {
		case BLOCK_NODE:
			layoutBlock(containingBlock);
			break;
		case INLINE_NODE:
			layoutInline(containingBlock);
		case ANONYMOUS_BLOCK:
		default:
		}
	}*/

	@Override
	public String toString() {
		return stringifyLayoutBox(this);
	}

	private static String stringifyLayoutBox(LayoutBox box, String lastIndent) {
		StringBuilder res = new StringBuilder();
		String indent = lastIndent + "  ";

		res.append(lastIndent).append(box.getType()).append(" {\n");

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
