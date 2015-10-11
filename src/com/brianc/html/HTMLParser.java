package com.brianc.html;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.brianc.AbstractParser;
import com.brianc.dom.Node;

public class HTMLParser extends AbstractParser {
	public HTMLParser(String input) {
		super(input);
	}

	public Node parse() {
		List<Node> nodes = parseNodes();

		if (nodes.size() == 1) {
			return nodes.get(0);
		}

		return Node.createElement("html", new HashMap<>(), nodes);
	}

	private String parseTagName() {
		return consumeWhile(Character::isLetterOrDigit);
	}

	private Node parseNode() {
		switch (nextChar()) {
		case '<':
			return parseElement();
		default:
			return parseText();
		}
	}

	private Node parseText() {
		return Node.createText(consumeWhile(c -> c != '<'));
	}

	private Node parseElement() {
		assertInput(consumeChar() == '<');
		String tagName = parseTagName();
		Map<String, String> attrs = parseAttributes();
		assertInput(consumeChar() == '>');

		List<Node> children = parseNodes();

		assertInput(consumeChar() == '<');
		assertInput(consumeChar() == '/');
		assertInput(parseTagName().equals(tagName));
		assertInput(consumeChar() == '>');

		return Node.createElement(tagName, attrs, children);
	}

	private List<Node> parseNodes() {
		List<Node> nodes = new ArrayList<>();

		for (;;) {
			consumeWhitespace();

			if (eof() || startsWith("</")) {
				break;
			}

			nodes.add(parseNode());
		}

		return nodes;
	}

	private Map<String, String> parseAttributes() {
		Map<String, String> attributes = new HashMap<>();

		for (;;) {
			consumeWhitespace();

			if (nextChar() == '>') {
				break;
			}

			Entry<String, String> attribute = parseAttribute();
			attributes.put(attribute.getKey(), attribute.getValue());
		}

		return attributes;
	}

	private Entry<String, String> parseAttribute() {
		String name = parseTagName();
		assertInput(consumeChar() == '=');
		String value = parseAttributeValue();

		return new AbstractMap.SimpleEntry<>(name, value);
	}

	private String parseAttributeValue() {
		char openQuote = consumeChar();
		assertInput(openQuote == '"' || openQuote == '\'');
		String value = consumeWhile(c -> c != openQuote);
		assertInput(consumeChar() == openQuote);

		return value;
	}

	private void assertInput(boolean cond) {
		if (!cond) {
			throw new IllegalStateException();
		}
	}

	public static void main(String[] args) {
		String toParse = "<html><body><h1>Title</h1><div id=\"main\" class=\"test\"><p>Hello <em>world</em>!</p></div></body></html>";
		Node root = new HTMLParser(toParse).parse();

		System.out.println(root);
	}
}
