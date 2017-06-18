package com.brianc.dom

class CommentNode(children: List<Node>, val text: String) : Node(children, NodeType.COMMENT)
