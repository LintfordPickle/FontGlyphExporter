package com.lintfords.glyphextractor;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
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

	private boolean mOptionsLoaded;
	private boolean mIsLoaded;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BitmapFont(BitmapFontOptions pInputOptions) {
		mFontOptions = pInputOptions;
		mIsLoaded = false;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void LoadFont() {
		File lFontFile = new File(mFontOptions.fontFilepath);
		if (lFontFile.exists() == false) {
			System.out.println("Font file doesn't exist : " + mFontOptions.fontFilepath);
			return;
		}

		try {
			FileInputStream lFileStream = new FileInputStream(lFontFile);
			mFont = Font.createFont(Font.TRUETYPE_FONT, lFileStream);
			mFont = mFont.deriveFont(mFontOptions.pointSize);

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

		for (int i = mFontOptions.unicodeStartCode; i <= mFontOptions.unicodeEndCode; i++) {
			char c = (char) i;

			// TODO: unicode-code as string? or ansi value (int)
			String hexValue = String.format("%4s", Integer.toHexString(c), ' ');
			hexValue = hexValue.replaceAll(" ", "0");

			BufferedImage lGlyphImage = createGlyphImage(mFont, c);
			saveGlyphToFile(lGlyphImage, hexValue);
		}
	}

	private void saveGlyphToFile(BufferedImage pGlyphImage, String pFilename) {
		File fontDirectory = new File(mFontOptions.bitmapName);
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
		Graphics2D graphics = lImage.createGraphics();

		graphics.setFont(pFont);
		FontMetrics lFontMetrics = graphics.getFontMetrics();

		final int lGlyphBorder = mFontOptions.glyphBorderSize;

		int charWidth = lFontMetrics.charWidth(currentCharacter) + lGlyphBorder * 2;
		int charHeight = lFontMetrics.getHeight() + lGlyphBorder * 2;

		lImage = new BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB);
		graphics = lImage.createGraphics();
		graphics.setFont(pFont);
		graphics.setPaint(Color.white);
		graphics.drawString(String.valueOf(currentCharacter), 0 + lGlyphBorder, lFontMetrics.getAscent() + lGlyphBorder);
		graphics.dispose();

		return lImage;
	}

}
