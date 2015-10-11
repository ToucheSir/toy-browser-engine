package com.brianc;

import java.util.function.Predicate;

public abstract class AbstractParser {
	private int pos;
	// TODO Stream for efficiency?
	private String input;

	public AbstractParser(String input) {
		pos = 0;
		this.input = input;
	}

	protected char nextChar() {
		return input.charAt(pos);
	}

	protected boolean startsWith(String s) {
		return input.startsWith(s, pos);
	}

	protected boolean eof() {
		return pos >= input.length();
	}

	protected char consumeChar() {
		return input.charAt(pos++);
	}

	protected String consumeChars(int number) {
		int start = pos;
		pos += number;

		return input.substring(start, pos);
	}

	protected String consumeWhile(Predicate<Character> test) {
		int start = pos;

		for (; !eof() && test.test(nextChar()); consumeChar())
			;

		return input.substring(start, pos);
	}

	protected void consumeWhitespace() {
		consumeWhile(Character::isWhitespace);
	}

}
