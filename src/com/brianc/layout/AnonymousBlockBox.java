package com.brianc.layout;

import com.brianc.graphics.Renderer;

public class AnonymousBlockBox extends LayoutBox {
	@Override
	public BoxType getType() {
		return BoxType.ANONYMOUS_BLOCK;
	}

	@Override
	void layout(Dimensions containingBlock, Renderer renderBackend) {
	}
}
