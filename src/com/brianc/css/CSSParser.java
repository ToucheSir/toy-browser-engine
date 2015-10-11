package com.brianc.css;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import com.brianc.AbstractParser;

public class CSSParser extends AbstractParser {
	public CSSParser(String input) {
		super(input);
	}

	public StyleSheet parse() {
		return new StyleSheet(parseRules());
	}

	private SimpleSelector parseSimpleSelector() {
		SimpleSelector selector = new SimpleSelector();

		while (!eof()) {
			char ch = nextChar();
			if (ch == '#') {
				consumeChar();
				selector.id = Optional.of(parseIdentifier());
			} else if (ch == '.') {
				consumeChar();
				selector.classes.add(parseIdentifier());
			} else if (ch == '*') {
				consumeChar();
			} else if (validIdentifierChar(ch)) {
				selector.tagName = Optional.of(parseIdentifier());
			} else {
				break;
			}
		}

		return selector;
	}

	private List<Rule> parseRules() {
		List<Rule> rules = new ArrayList<>();

		for (;;) {
			consumeWhitespace();
			if (eof())
				break;
			rules.add(parseRule());
		}

		return rules;
	}

	private Rule parseRule() {
		return new Rule(parseSelectors(), parseDeclarations());
	}

	private List<Declaration> parseDeclarations() {
		assertInput(consumeChar() == '{');
		List<Declaration> declarations = new ArrayList<>();
		for (;;) {
			consumeWhitespace();
			if (nextChar() == '}') {
				consumeChar();
				break;
			}

			declarations.add(parseDeclaration());
		}

		return declarations;
	}

	private Declaration parseDeclaration() {
		String propertyName = parseIdentifier();
		consumeWhitespace();

		assertInput(consumeChar() == ':');
		consumeWhitespace();

		Value value = parseValue();
		consumeWhitespace();

		assertInput(consumeChar() == ';');

		return new Declaration(propertyName, value);
	}

	private Value parseValue() {
		char ch = nextChar();
		if (Character.isDigit(ch)) {
			return parseLength();
		} else if (ch == '#') {
			return parseColor();
		}

		return new KeywordValue(parseIdentifier());
	}

	private Value parseColor() {
		assertInput(consumeChar() == '#');

		return new ColorValue(
				new Color(parseHexPair(), parseHexPair(), parseHexPair(), (short) 255));
	}

	private short parseHexPair() {
		return Short.parseShort(consumeChars(2), 16);
	}

	private Value parseLength() {
		return new LengthValue(parseFloat(), parseUnit());
	}

	private Unit parseUnit() {
		switch (parseIdentifier().toLowerCase()) {
		case "px":
			return Unit.PX;
		default:
			throw new IllegalArgumentException("unrecognized unit");
		}
	}

	private float parseFloat() {
		return Float.parseFloat(consumeWhile(c -> Character.isDigit(c) || c == '.'));
	}

	private List<Selector> parseSelectors() {
		Set<Selector> selectors = new TreeSet<>((a, b) -> a.specificity()
				.compareTo(b.specificity()));

		for (;;) {
			selectors.add(parseSimpleSelector());
			consumeWhitespace();

			char ch = nextChar();
			if (ch == ',') {
				consumeChar();
				consumeWhitespace();
			} else if (ch == '{') {
				break;
			} else {
				throw new IllegalStateException("Unexpected character " + ch + " in selector list");
			}
		}

		return new ArrayList<>(selectors);
	}

	private static boolean validIdentifierChar(char ch) {
		return Character.isLetterOrDigit(ch) || ch == '-' || ch == '_';
	}

	private String parseIdentifier() {
		return consumeWhile(CSSParser::validIdentifierChar);
	}

	private void assertInput(boolean cond) {
		if (!cond) {
			throw new IllegalStateException();
		}
	}

	public static void main(String[] args) {
		StyleSheet s = new CSSParser("div.note { margin-bottom: 20px; padding: 10px; }").parse();
		System.out.println(s);
	}
}
