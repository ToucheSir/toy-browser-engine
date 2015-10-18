package com.brianc;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import com.brianc.css.CSSParser;
import com.brianc.css.StyleSheet;
import com.brianc.dom.Node;
import com.brianc.graphics.Painter;
import com.brianc.graphics.Renderer;
import com.brianc.html.HTMLParser;
import com.brianc.layout.Dimensions;
import com.brianc.layout.LayoutBox;
import com.brianc.style.StyledNode;

public class Main {

	public static void main(String[] args) {
		String htmlFile = "test.html";
		String styleSheetFile = "test.css";
		String outputFile = "out.png";
		Dimensions viewport = new Dimensions();
		viewport.content.width = 800;
		viewport.content.height = 600;
		
		try {
			String htmlText = new String(Files.readAllBytes(Paths.get(htmlFile)), StandardCharsets.UTF_8);
			String cssText = new String(Files.readAllBytes(Paths.get(styleSheetFile)), StandardCharsets.UTF_8);
			Renderer imgRenderer = new Renderer();
			
			Node rootNode = new HTMLParser(htmlText).parse();
			StyleSheet styleSheet = new CSSParser(cssText).parse();
			StyledNode styleRoot = StyledNode.styleTree(rootNode, styleSheet);
			LayoutBox layoutRoot = LayoutBox.layoutTree(styleRoot, new Dimensions(viewport), imgRenderer);
			
			BufferedImage img = Painter.paint(layoutRoot, viewport.content).getBuffer();
			File outputImageFile = new File(outputFile);
			ImageIO.write(img, "png", outputImageFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
