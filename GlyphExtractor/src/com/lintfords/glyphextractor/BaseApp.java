package com.lintfords.glyphextractor;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class BaseApp {

	public static void main(String[] args) {
		Options options = new Options();
		options.addOption(BitmapFontOptions.CLI_OPTION_FILEPATH, true, "path to the font file");
		options.addOption(BitmapFontOptions.CLI_OPTION_BITMAPNAME, true, "name of bitmap");
		options.addOption(BitmapFontOptions.CLI_OPTION_POINTSIZE, true, "point size");
		options.addOption(BitmapFontOptions.CLI_OPTION_GLYPH_BORDER, true, "glyph border size");
		options.addOption(BitmapFontOptions.CLI_OPTION_RANGE_START, true, "character start range");
		options.addOption(BitmapFontOptions.CLI_OPTION_RANGE_END, true, "charcter end range");

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			e.printStackTrace();

			printAppUsage(options);

			return;
		}

		new BaseApp(cmd);
	}

	private static void printAppUsage(Options opts) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("GlyphExporter", opts);
	}

	public BaseApp(CommandLine cli) {
		BitmapFontOptions options = BitmapFontOptions.fromCmdLine(cli);

		BitmapFont lBitmapFont = new BitmapFont(options);
		lBitmapFont.LoadFont();
		lBitmapFont.exportGlyphsToFiles();
	}
}
