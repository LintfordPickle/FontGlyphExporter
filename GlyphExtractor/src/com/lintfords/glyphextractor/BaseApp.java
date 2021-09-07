package com.lintfords.glyphextractor;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.lintfords.glyphextractor.data.BitmapFontOptions;

public class BaseApp {

	// --------------------------------------
	// Entry Point
	// --------------------------------------

	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			startGui();
		} else {
			startCli(args);
		}
	}

	// --------------------------------------

	private static void startGui() {
		GuiApp lApp = new GuiApp();
		lApp.createWindow();
	}

	private static void startCli(String[] args) {
		printAppHeader();

		Options lCliOptions = new Options();
		lCliOptions.addOption(BitmapFontOptions.CLI_OPTION_FILEPATH, true, "path to the font file");
		lCliOptions.addOption(BitmapFontOptions.CLI_OPTION_BITMAPNAME, true, "name of bitmap");
		lCliOptions.addOption(BitmapFontOptions.CLI_OPTION_POINTSIZE, true, "point size");
		lCliOptions.addOption(BitmapFontOptions.CLI_OPTION_GLYPH_BORDER, true, "glyph border size");
		lCliOptions.addOption(BitmapFontOptions.CLI_OPTION_RANGE_START, true, "character start range");
		lCliOptions.addOption(BitmapFontOptions.CLI_OPTION_RANGE_END, true, "charcter end range");

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(lCliOptions, args);
		} catch (ParseException e) {
			e.printStackTrace();

			printAppUsage(lCliOptions);

			return;
		}

		BitmapFontOptions lBitmapOptions = BitmapFontOptions.fromCmdLine(cmd);
		lBitmapOptions.outlineSize = 2;

		if (BitmapFontOptions.validateInputOptions(lBitmapOptions) == false) {
			printAppUsage(lCliOptions);
			return;
		}

		new ConsoleApp(lBitmapOptions);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private static void printAppHeader() {
		System.out.println(AppConstants.AppName + " " + AppConstants.Version);
		System.out.println("Developed by " + AppConstants.Author + ", " + AppConstants.Date);
	}

	private static void printAppUsage(Options opts) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("GlyphExporter", opts);
	}

}
