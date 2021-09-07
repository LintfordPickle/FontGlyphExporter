package com.lintfords.glyphextractor;

import com.lintfords.glyphextractor.data.BitmapFont;
import com.lintfords.glyphextractor.data.BitmapFontOptions;
import com.lintfords.glyphextractor.views.MainWindow;

public class GuiApp {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public GuiApp() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void createWindow() {
		BitmapFont lBitmapFont = new BitmapFont();
		MainWindow lNewMainWindow = new MainWindow(lBitmapFont);
		lNewMainWindow.setVisible(true);
	}

	public void exportBitmapFont(BitmapFontOptions pOptions) {
		BitmapFont lBitmapFont = new BitmapFont();

		lBitmapFont.LoadFont(pOptions);
		lBitmapFont.exportGlyphsToFiles();
	}
}
