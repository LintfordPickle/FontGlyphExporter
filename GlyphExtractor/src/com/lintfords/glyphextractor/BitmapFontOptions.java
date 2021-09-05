package com.lintfords.glyphextractor;

import org.apache.commons.cli.CommandLine;

public class BitmapFontOptions {
	public static final String CLI_OPTION_FILEPATH = "f";
	public static final String CLI_OPTION_BITMAPNAME = "n";
	public static final String CLI_OPTION_POINTSIZE = "p";
	public static final String CLI_OPTION_GLYPH_BORDER = "b";
	public static final String CLI_OPTION_RANGE_START = "rs";
	public static final String CLI_OPTION_RANGE_END = "re";

	public String fontFilepath;
	public String bitmapName;
	public float pointSize;
	public int glyphBorderSize;
	public int unicodeStartCode;
	public int unicodeEndCode;

	private static int DEFAULT_POINT_SIZE = 16;
	private static int DEFAULT_UNICODE_START_CODE = 32;
	private static int DEFAULT_UNICODE_END_CODE = 127;

	private BitmapFontOptions() {
		bitmapName = "unnamed";
		unicodeStartCode = 30;
		unicodeEndCode = 127;
		glyphBorderSize = 1;
		pointSize = 16;
	}

	public static BitmapFontOptions fromCmdLine(CommandLine pCli) {
		BitmapFontOptions newBitmapFontOptions = new BitmapFontOptions();
		newBitmapFontOptions.fontFilepath = pCli.getOptionValue(CLI_OPTION_FILEPATH);
		newBitmapFontOptions.bitmapName = pCli.getOptionValue(CLI_OPTION_BITMAPNAME);
		newBitmapFontOptions.pointSize = tryGetFloat(pCli.getOptionValue(CLI_OPTION_POINTSIZE), DEFAULT_POINT_SIZE);
		newBitmapFontOptions.unicodeStartCode = tryGetInt(pCli.getOptionValue(CLI_OPTION_RANGE_START), DEFAULT_UNICODE_START_CODE);
		newBitmapFontOptions.unicodeEndCode = tryGetInt(pCli.getOptionValue(CLI_OPTION_RANGE_END), DEFAULT_UNICODE_END_CODE);
		newBitmapFontOptions.glyphBorderSize = tryGetInt(pCli.getOptionValue(CLI_OPTION_GLYPH_BORDER), 1);

		return newBitmapFontOptions;
	}

	private static float tryGetFloat(String pCliValue, float pDefValue) {
		if (pCliValue == null || pCliValue.length() == 0)
			return pDefValue;

		try {
			final float lParsedFloat = Float.valueOf(pCliValue);
			return lParsedFloat;
		} catch (Exception e) {
			System.out.println("NumberFormatException thrown whilst parsing argument " + pCliValue);
		}

		return pDefValue;
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
