package com.lintfords.glyphextractor;

import com.lintfords.glyphextractor.data.BitmapFont;
import com.lintfords.glyphextractor.data.BitmapFontOptions;

public class ConsoleApp {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ConsoleApp(BitmapFontOptions pBitmapOptions) {
		BitmapFont lBitmapFont = new BitmapFont();

		lBitmapFont.LoadFont(pBitmapOptions);
		lBitmapFont.exportGlyphsToFiles();
	}
}
