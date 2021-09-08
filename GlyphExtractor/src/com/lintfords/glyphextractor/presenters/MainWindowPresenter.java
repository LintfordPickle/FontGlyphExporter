package com.lintfords.glyphextractor.presenters;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lintfords.glyphextractor.data.BitmapFont;
import com.lintfords.glyphextractor.data.BitmapFontOptions;
import com.lintfords.glyphextractor.views.MainWindow;

public class MainWindowPresenter implements IMainPresenter {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final String CONFIGURATION_EXTENSION = ".GEC";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private MainWindow mMainWindow;
	private BitmapFontOptions mBitmapFontOptions;
	private BitmapFont mBitmapFont;
	private int mPreviewChar;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MainWindowPresenter(MainWindow pMainWindow, BitmapFont pBitmapFont) {
		mMainWindow = pMainWindow;

		mBitmapFont = pBitmapFont;
		mBitmapFontOptions = new BitmapFontOptions();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	// OPTIONS ------------------------------

	@Override
	public void setPreviewCharacter(int pNewPreviewChar) {
		if (mPreviewChar != pNewPreviewChar) {
			mPreviewChar = pNewPreviewChar;

			validateAndPreview();
		}
	}

	@Override
	public void setFontFilepath(File pNewFontFile) {
		if (pNewFontFile.exists() && pNewFontFile.isFile()) {
			if (mBitmapFontOptions.fontFilepath != pNewFontFile.getAbsolutePath()) {
				mBitmapFontOptions.fontFilepath = pNewFontFile.getAbsolutePath();
				mMainWindow.setFontFilename(pNewFontFile.getAbsolutePath());
				mMainWindow.setStatusText("Font Filename Set");

				validateAndPreview();
			}
		}
	}

	@Override
	public void setOutputFolder(File pNewOutputFolder) {
		if (mBitmapFontOptions.outputFolder != pNewOutputFolder.getAbsolutePath()) {
			mBitmapFontOptions.outputFolder = pNewOutputFolder.getAbsolutePath();
			mMainWindow.setOutputFolder(pNewOutputFolder.getAbsolutePath());
			mMainWindow.setStatusText("Output Folder Set");
		}
	}

	@Override
	public void setPointSize(int pNewPointSize) {
		if (mBitmapFontOptions.pointSize != pNewPointSize) {
			mBitmapFontOptions.pointSize = pNewPointSize;
			mMainWindow.setStatusText("Point Size Set : " + pNewPointSize);

			validateAndPreview();
		}
	}

	@Override
	public void setGlyphNamingConventionUseHex(boolean pUseHex) {
		if (mBitmapFontOptions.useHexIdentifier != pUseHex) {
			mBitmapFontOptions.useHexIdentifier = pUseHex;
			mMainWindow.setStatusText(pUseHex ? "Using hex glyph names" : "Using decimal glyph names");
		}
	}

	@Override
	public void setAntiAliasing(boolean pNewValue) {
		if (mBitmapFontOptions.useAntiAliasing != pNewValue) {
			mBitmapFontOptions.useAntiAliasing = pNewValue;
			mMainWindow.setStatusText("Anti-Aliasing Set : " + pNewValue);

			validateAndPreview();
		}
	}

	@Override
	public void setSpritePadding(int pNewSpritePadding) {
		if (mBitmapFontOptions.spritePadding != pNewSpritePadding) {
			mBitmapFontOptions.spritePadding = pNewSpritePadding;
			mMainWindow.setStatusText("Sprite Padding set : " + pNewSpritePadding);

			validateAndPreview();
		}
	}

	public void setUnicodeStartPoint(int pNewUnicodeStartPoint) {
		if (mBitmapFontOptions.unicodeStartCode != pNewUnicodeStartPoint) {
			mBitmapFontOptions.unicodeStartCode = pNewUnicodeStartPoint;
			mMainWindow.setStatusText("Unicode Start Codepoint Set : " + pNewUnicodeStartPoint + " (" + (char) pNewUnicodeStartPoint + ")");
		}
	}

	public void setUnicodeEndPoint(int pNewUnicodeEndPoint) {
		if (mBitmapFontOptions.unicodeEndCode != pNewUnicodeEndPoint) {
			mBitmapFontOptions.unicodeEndCode = pNewUnicodeEndPoint;
			mMainWindow.setStatusText("Unicode End Codepoint Set : " + pNewUnicodeEndPoint + " (" + (char) pNewUnicodeEndPoint + ")");
		}
	}

	@Override
	public void setOutlineGlyphs(int pOutlineWidth) {
		if (pOutlineWidth < 0)
			pOutlineWidth = 0;
		if (pOutlineWidth > 100)
			pOutlineWidth = 100;

		if (mBitmapFontOptions.outlineSize != pOutlineWidth) {
			mBitmapFontOptions.outlineSize = pOutlineWidth;
			mMainWindow.setStatusText("Outline glyph width updated to " + (pOutlineWidth > 0 ? pOutlineWidth : "off"));

			validateAndPreview();
		}
	}

	@Override
	public void setFillColor(String pNewFillColorHex) {
		if (pNewFillColorHex == null || pNewFillColorHex.length() < 9) {
			// Expected format is #RRGGBBAA
			return;
		}

		// Check validity of the fill color string received
		Color lFillColor = hex2Rgb(pNewFillColorHex);
		if (lFillColor != null) {
			if (mBitmapFontOptions.fillColorHex != pNewFillColorHex) {
				mBitmapFontOptions.fillColorHex = pNewFillColorHex;
				mMainWindow.setStatusText("Updated fill color : " + pNewFillColorHex);

				validateAndPreview();
			}
		} else {
			mBitmapFontOptions.fillColorHex = "#000000FF";
			mMainWindow.setStatusText("Error setting fill color. Invalid hex code (try #000000FF)");

			validateAndPreview();
		}
	}

	@Override
	public void setOutlineColor(String pNewOutlineColorHex) {
		if (pNewOutlineColorHex == null || pNewOutlineColorHex.length() < 9) {
			// Expected format is #RRGGBBAA
			return;
		}

		// Check validity of the fill color string received
		Color lOutlineColor = hex2Rgb(pNewOutlineColorHex);
		if (lOutlineColor != null) {
			if (mBitmapFontOptions.outlineColorHex != pNewOutlineColorHex) {
				mBitmapFontOptions.outlineColorHex = pNewOutlineColorHex;
				mMainWindow.setStatusText("Updated fill color : " + pNewOutlineColorHex);

				validateAndPreview();
			}
		} else {
			mBitmapFontOptions.outlineColorHex = "#FFFFFFFF";
			mMainWindow.setStatusText("Error setting fill color. Invalid hex code (try #000000FF)");

			validateAndPreview();
		}
	}

	// TODO: Refactor (used twice)
	private static Color hex2Rgb(String colorStr) {
		try {
			return new Color(Integer.valueOf(colorStr.substring(1, 3), 16), Integer.valueOf(colorStr.substring(3, 5), 16), Integer.valueOf(colorStr.substring(5, 7), 16), Integer.valueOf(colorStr.substring(7, 9), 16));
		} catch (NumberFormatException ex) {
			return Color.magenta;
		}
	}

	public BitmapFontOptions getBitmapFontOptions() {
		return mBitmapFontOptions;
	}

	public void resetOptions() {
		mBitmapFontOptions.reset();
	}

	private void validateAndPreview() {
		if (BitmapFontOptions.validateInputOptions(mBitmapFontOptions)) {
			mBitmapFont.LoadFont(mBitmapFontOptions);
			BufferedImage lGlyphImage = mBitmapFont.createGlyphImage((char) mPreviewChar);

			mMainWindow.setImagePreview(lGlyphImage);
		}
	}

	// --------------------------------------

	@Override
	public String enforceConfigurationFileExtension(String pFilename) {
		if (pFilename == null || pFilename.length() < 4)
			return pFilename;

		if (pFilename.substring(pFilename.length() - 4, pFilename.length()).toUpperCase().equals(CONFIGURATION_EXTENSION) == false)
			return pFilename + ".gec";

		return pFilename;
	}

	@Override
	public void exportGlyphs() {
		if (prepareOutputFolder() == false)
			return;

		mBitmapFont.LoadFont(mBitmapFontOptions);
		mBitmapFont.exportGlyphsToFiles();
	}

	private boolean prepareOutputFolder() {
		String lOutputFolderLocation = mBitmapFontOptions.outputFolder;
		File lOutputFolder = new File(lOutputFolderLocation);

		if (lOutputFolder.exists() == false) {
			lOutputFolder.mkdirs();

			if (lOutputFolder.exists() == false)
				return false;
		}

		return lOutputFolder.isDirectory();
	}

	@Override
	public void saveConfiguration(File pConfigurationFile) {
		if (pConfigurationFile == null)
			return;

		String lFilePath = enforceConfigurationFileExtension(pConfigurationFile.getAbsolutePath());

		Gson lGson = new Gson();
		String lFontOptions = lGson.toJson(mBitmapFontOptions);

		try {
			// write converted json data to a file named "file.json"
			FileWriter writer = new FileWriter(lFilePath);
			writer.write(lFontOptions);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void loadConfiguration(File pConfigurationFile) {
		if (pConfigurationFile == null || pConfigurationFile.exists() == false)
			return;

		final Gson lGson = new GsonBuilder().create();
		FileReader lFileContents;
		BitmapFontOptions lLoadedOptionsFile = null;
		try {
			lFileContents = new FileReader(pConfigurationFile);
			lLoadedOptionsFile = lGson.fromJson(lFileContents, BitmapFontOptions.class);

			if (lLoadedOptionsFile == null) {
				System.out.println("dsf");
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		if (lLoadedOptionsFile == null) {
			mMainWindow.setStatusText("Error loading BitmapFontOptions files: deserialization");
			return;
		}

		// Validate loaded settings
		if (BitmapFontOptions.validateInputOptions(lLoadedOptionsFile) == false) {
			mMainWindow.setStatusText("Error loading BitmapFontOptions files: invalid settings");
			return;
		}

		mBitmapFontOptions = lLoadedOptionsFile;

		// Set the fields
		mMainWindow.setFontFilename(lLoadedOptionsFile.fontFilepath);
		mMainWindow.setOutputFolder(lLoadedOptionsFile.outputFolder);
		mMainWindow.setPointSizeValue(String.valueOf(lLoadedOptionsFile.pointSize));
		mMainWindow.setUnicodeRangePoints(lLoadedOptionsFile.unicodeStartCode, lLoadedOptionsFile.unicodeEndCode);
		mMainWindow.setOutlineGlyphs(String.valueOf(lLoadedOptionsFile.outlineSize));
		mMainWindow.setFillColor(lLoadedOptionsFile.fillColorHex);
		mMainWindow.setOutlineColor(lLoadedOptionsFile.outlineColorHex);
		mMainWindow.setGlyphNamingConvention(lLoadedOptionsFile.useHexIdentifier);
		mMainWindow.setAntiAliasing(lLoadedOptionsFile.useAntiAliasing);
		mMainWindow.setSpritePadding(String.valueOf(lLoadedOptionsFile.spritePadding));
	}
}
