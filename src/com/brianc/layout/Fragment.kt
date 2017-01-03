package com.brianc.layout

import java.awt.font.TextLayout
import java.util.BitSet

import com.brianc.style.StyledNode

class Fragment(internal var box: InlineBox, internal var startPos: Int, internal var endPos: Int, lineLayout: TextLayout) {
    internal var nodeRef: StyledNode
    var layout: TextLayout
        internal set
    var flags: BitSet
        internal set

    init {
        this.nodeRef = box.styledNode
        this.layout = lineLayout
        this.flags = BitSet()
    }

    companion object {

        val FIRST_FOR_ELEMENT = 1
        val LAST_FOR_ELEMENT = 2
    }
}
