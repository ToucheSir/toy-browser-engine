package com.brianc

import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

import javax.imageio.ImageIO

import com.brianc.css.CSSParser
import com.brianc.graphics.ImageRenderer
import com.brianc.graphics.Painter
import com.brianc.html.HTMLParser
import com.brianc.layout.Dimensions
import com.brianc.layout.LayoutBox
import com.brianc.style.StyledNode

fun main(args: Array<String>) {
    val htmlFile = "test.html"
    val styleSheetFile = "test.css"
    val outputFile = "out.png"
    val viewport = Dimensions()
    viewport.content.width = 800f
    viewport.content.height = 600f

    try {
        val htmlText = String(Files.readAllBytes(Paths.get(htmlFile)), StandardCharsets.UTF_8)
        val cssText = String(Files.readAllBytes(Paths.get(styleSheetFile)), StandardCharsets.UTF_8)
        val renderBackend = ImageRenderer(viewport.content)

        val rootNode = HTMLParser(htmlText).parse()
        val styleSheet = CSSParser(cssText).parse()
        val styleRoot = StyledNode.styleTree(rootNode, styleSheet)
        val layoutRoot = LayoutBox.layoutTree(styleRoot, Dimensions(viewport), renderBackend)
        println(LayoutBox.stringifyLayoutBox(layoutRoot))
        Painter.paint(layoutRoot, viewport.content, renderBackend)

        val outputImageFile = File(outputFile)
        ImageIO.write(renderBackend.buffer, "png", outputImageFile)
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
