package com.lintfords.glyphextractor.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.apache.commons.cli.CommandLine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BitmapFontOptions {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final int UNICODE_MIN_START = 32; // SPACE
	private static final int UNICODE_MAX_END = 127; // TILDE (no ANSI)

	public static final String CLI_OPTION_FILEPATH = "f";
	public static final String CLI_OPTION_BITMAPNAME = "n";
	public static final String CLI_OPTION_POINTSIZE = "p";
	public static final String CLI_OPTION_GLYPH_BORDER = "b";
	public static final String CLI_OPTION_RANGE_START = "rs";
	public static final String CLI_OPTION_RANGE_END = "re";

	// --------------------------------------
	// Variables
	// --------------------------------------

	public String fillColorHex;
	public String outlineColorHex;
	public String fontFilepath;
	public String outputFolder;
	public int pointSize;
	public int spritePadding;
	public int outlineSize;
	public int unicodeStartCode;
	public int unicodeEndCode;
	public boolean useHexIdentifier;
	public boolean useAntiAliasing;

	private static int DEFAULT_POINT_SIZE = 16;
	private static int DEFAULT_UNICODE_START_CODE = 32;
	private static int DEFAULT_UNICODE_END_CODE = 127;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BitmapFontOptions() {
		outputFolder = "unnamed";
		unicodeStartCode = 30;
		unicodeEndCode = 127;
		spritePadding = 1;
		pointSize = 16;
		fillColorHex = "#FFFFFFFF";
		outlineColorHex = "#000000FF";
		useHexIdentifier = true;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void reset() {
		fillColorHex = "#FFFFFFFF";
		outlineColorHex = "#000000FF";
		fontFilepath = "";
		outputFolder = "";
		pointSize = 16;
		spritePadding = 1;
		outlineSize = 2;
		unicodeStartCode = 32;
		unicodeEndCode = 127;
		useHexIdentifier = false;
		useAntiAliasing = true;
	}

	public static BitmapFontOptions fromConfigrationFile(String pFilepath) {
		File lConfigurationFile = new File(pFilepath);
		if (lConfigurationFile.exists() == false) {
			return null;
		}

		final Gson lGson = new GsonBuilder().create();
		FileReader lFileContents;
		BitmapFontOptions lLoadedOptionsFile = null;
		try {
			lFileContents = new FileReader(lConfigurationFile);
			lLoadedOptionsFile = lGson.fromJson(lFileContents, BitmapFontOptions.class);

			if (lLoadedOptionsFile == null) {
				System.out.println("Failed to load configuration file");
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		if (lLoadedOptionsFile == null) {
			return null;
		}

		return lLoadedOptionsFile;
	}

	public static BitmapFontOptions fromArguments(String pFilepath, String pBitmapName, int pPointSize, int pUnicodeStartPoint, int pUnicodeEndPoint, int pGlyphBorderSize) {
		BitmapFontOptions newBitmapFontOptions = new BitmapFontOptions();
		newBitmapFontOptions.fontFilepath = pFilepath;
		newBitmapFontOptions.outputFolder = pBitmapName;
		newBitmapFontOptions.pointSize = pPointSize;
		newBitmapFontOptions.unicodeStartCode = pUnicodeStartPoint;
		newBitmapFontOptions.unicodeEndCode = pUnicodeEndPoint;
		newBitmapFontOptions.spritePadding = pGlyphBorderSize;

		return newBitmapFontOptions;
	}

	public static BitmapFontOptions fromCmdLine(CommandLine pCli) {
		BitmapFontOptions newBitmapFontOptions = new BitmapFontOptions();
		newBitmapFontOptions.fontFilepath = pCli.getOptionValue(CLI_OPTION_FILEPATH);
		newBitmapFontOptions.outputFolder = pCli.getOptionValue(CLI_OPTION_BITMAPNAME);
		newBitmapFontOptions.pointSize = tryGetInt(pCli.getOptionValue(CLI_OPTION_POINTSIZE), DEFAULT_POINT_SIZE);
		newBitmapFontOptions.unicodeStartCode = tryGetInt(pCli.getOptionValue(CLI_OPTION_RANGE_START), DEFAULT_UNICODE_START_CODE);
		newBitmapFontOptions.unicodeEndCode = tryGetInt(pCli.getOptionValue(CLI_OPTION_RANGE_END), DEFAULT_UNICODE_END_CODE);
		newBitmapFontOptions.spritePadding = tryGetInt(pCli.getOptionValue(CLI_OPTION_GLYPH_BORDER), 1);

		return newBitmapFontOptions;
	}

	public static boolean validateInputOptions(BitmapFontOptions pInputOptions) {
		if (pInputOptions.fontFilepath == null || pInputOptions.fontFilepath.length() == 0) {
			System.out.println("fontFilepath is null or empty");
			return false;
		}

		if (pInputOptions.unicodeStartCode < UNICODE_MIN_START) {
			pInputOptions.unicodeStartCode = UNICODE_MIN_START;
		}

		if (pInputOptions.unicodeEndCode > UNICODE_MAX_END) {
			pInputOptions.unicodeEndCode = UNICODE_MAX_END;
		}

		return true;
	}

	private static int tryGetInt(String pCliValue, int pDefValue) {
		if (pCliValue == null || pCliValue.length() == 0)
			return pDefValue;

		try {
			final int lParsedInt = Integer.valueOf(pCliValue);
			return lParsedInt;
		} catch (Exception e) {
			System.out.println("NumberFormatException thrown whilst parsing argument " + pCliValue);
		}

		return pDefValue;
	}
}
