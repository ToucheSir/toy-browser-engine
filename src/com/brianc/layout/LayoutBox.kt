package com.brianc.layout

import java.util.ArrayList

import com.brianc.graphics.ImageRenderer
import com.brianc.graphics.Renderer
import com.brianc.style.Display
import com.brianc.style.StyledNode


interface LayoutBox {
    val children: MutableList<LayoutBox>
    val dimensions: Dimensions

    // TODO: do not generate unnecessary anonymous block box if block node only has inline child(ren)
    val inlineContainer: LayoutBox
        get() = this

    fun layout(containingBlock: Dimensions, renderBackend: Renderer)
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

    companion object {

        fun layoutTree(node: StyledNode, containingBlock: Dimensions, renderBackend: Renderer): LayoutBox {
            containingBlock.content.height = 0f

            val rootBox = buildLayoutTree(node)
            rootBox.layout(containingBlock, renderBackend)
            return rootBox
        }

        private fun fromDisplay(display: Display, styleNode: StyledNode): LayoutBox {
            when (display) {
                Display.BLOCK -> return BlockBox(styledNode = styleNode)
                Display.INLINE -> return InlineBox(styledNode = styleNode)
                Display.NONE -> throw IllegalArgumentException(String.format("Display mode '%s' must be one of 'INLINE' or 'BLOCK'", display))
            }
        }

        private fun buildLayoutTree(styleNode: StyledNode): LayoutBox {
            val root = fromDisplay(styleNode.display(), styleNode)

            for (child in styleNode.children) {
                when (child.display()) {
                    Display.BLOCK -> root.children.add(buildLayoutTree(child))
                    Display.INLINE -> root.inlineContainer.children.add(buildLayoutTree(child))
                    Display.NONE -> {
                    }
                }
            }

            return root
        }

        fun stringifyLayoutBox(box: LayoutBox, lastIndent: String = ""): String {
            val res = StringBuilder()
            val indent = lastIndent + "  "

            res.append(lastIndent).append(box::class.simpleName).append(" {\n")

            for (child in box.children) {
                res.append(stringifyLayoutBox(child, indent)).append("\n")
            }

            res.append(lastIndent).append("}")
            return res.toString()
        }
    }

}
