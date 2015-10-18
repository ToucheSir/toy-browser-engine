package com.brianc.layout;

public class AnonymousBlockBox extends LayoutBox {
	@Override
	public BoxType getType() {
		return BoxType.ANONYMOUS_BLOCK;
	}

	@Override
	public void layout(Dimensions containingBlock) {
	}
}
