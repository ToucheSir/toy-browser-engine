package com.brianc.layout;

public class AnonymousBlock implements BoxType {

	@Override
	public Type getType() {
		return Type.ANONYMOUS_BLOCK;
	}
	
	@Override
	public String toString() {
		return getType().toString();
	}
}
