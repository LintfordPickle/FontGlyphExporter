package com.lintfords.glyphextractor.data;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

public class BitmapFont {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private BitmapFontOptions mFontOptions;
	private Font mFont;

	private boolean mIsLoaded;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public BitmapFontOptions bitmapFontOptions() {
		return mFontOptions;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BitmapFont() {
		mIsLoaded = false;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void LoadFont(BitmapFontOptions pOptions) {
		if (pOptions == null || pOptions.fontFilepath == null) {
			mFont = null;
			mFontOptions = null;
			mIsLoaded = false;
			return;
		}

		mFontOptions = pOptions;

		File lFontFile = new File(pOptions.fontFilepath);
		if (lFontFile.exists() == false) {
			System.out.println("Font file doesn't exist : " + pOptions.fontFilepath);
			return;
		}

		try {
			FileInputStream lFileStream = new FileInputStream(lFontFile);
			mFont = Font.createFont(Font.TRUETYPE_FONT, lFileStream);
			mFont = mFont.deriveFont((float) mFontOptions.pointSize);

			lFileStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (FontFormatException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		if (mFont == null) {
			System.out.println("Couldn't load font : " + mFontOptions.fontFilepath);
			return;
		}

		mIsLoaded = true;
	}

	public void exportGlyphsToFiles() {
		if (!mIsLoaded)
			return;

		System.out.println("Starting export ...");

		int numGlyphsExported = 0;
		for (int i = mFontOptions.unicodeStartCode; i <= mFontOptions.unicodeEndCode; i++) {
			char c = (char) i;

			// TODO: unicode-code as string? or ansi value (int)
			String hexValue = null;
			if (mFontOptions.useHexIdentifier) {
				hexValue = String.format("%4s", Integer.toHexString(c), ' ');
				hexValue = hexValue.replaceAll(" ", "0");
			} else {
				hexValue = String.valueOf((int) c);
			}

			BufferedImage lGlyphImage = createGlyphImage(mFont, c);
			saveGlyphToFile(lGlyphImage, hexValue);

			numGlyphsExported++;
		}

		System.out.println("Exported " + numGlyphsExported + " glyphs to file(s)");
		System.out.println("Finished export");
	}

	private void saveGlyphToFile(BufferedImage pGlyphImage, String pFilename) {
		File fontDirectory = new File(mFontOptions.outputFolder);
		fontDirectory.mkdirs();

		final String fileSeparator = System.getProperty("file.separator");
		File lOutputfile = new File(fontDirectory + fileSeparator + pFilename + ".png");

		try {
			ImageIO.write(pGlyphImage, "png", lOutputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private BufferedImage createGlyphImage(Font pFont, char currentCharacter) {
		BufferedImage lImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2D = lImage.createGraphics();

		graphics2D.setFont(pFont);
		FontMetrics lFontMetrics = graphics2D.getFontMetrics();

		final int lGlyphBorder = mFontOptions.spritePadding;

		final int lOutlineWidth = (int) Math.ceil(mFontOptions.outlineSize);

		int charWidth = lFontMetrics.charWidth(currentCharacter) + lGlyphBorder * 2 + lOutlineWidth*2;
		int charHeight = lFontMetrics.getHeight() + lGlyphBorder * 2 + lOutlineWidth*2;

		// create a glyph vector from your text
		GlyphVector glyphVector = mFont.createGlyphVector(graphics2D.getFontRenderContext(), String.valueOf(currentCharacter));
		// get the shape object
		Shape textShape = glyphVector.getOutline();

		lImage = new BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB);
		graphics2D = lImage.createGraphics();
		graphics2D.setFont(pFont);
		BasicStroke outlineStroke = new BasicStroke(lOutlineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		graphics2D.translate(lGlyphBorder + lOutlineWidth, lGlyphBorder + lFontMetrics.getAscent() + lOutlineWidth);

		// Draw outline
		Color lOutlineColor = hex2Rgba(mFontOptions.outlineColorHex);
		if (lOutlineColor == null) {
			lOutlineColor = Color.black;
		}
		graphics2D.setColor(lOutlineColor);
		graphics2D.setStroke(outlineStroke);
		graphics2D.draw(textShape);

		int testCol = 0xff0000ff;
		
		// fill
		Color lFillColor = hex2Rgba(mFontOptions.fillColorHex);
//		if (lFillColor == null) {
//			lFillColor = Color.black;
//		}
		graphics2D.setPaint(lFillColor);
		graphics2D.fill(textShape);

		graphics2D.dispose();

		return lImage;
	}

	private static Color hex2Rgba(String colorStr) {
		return new Color(Integer.valueOf(colorStr.substring(1, 3), 16), Integer.valueOf(colorStr.substring(3, 5), 16), Integer.valueOf(colorStr.substring(5, 7), 16), Integer.valueOf(colorStr.substring(7, 9), 16));
	}

}
