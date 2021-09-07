package com.lintfords.glyphextractor;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.lintfords.glyphextractor.data.BitmapFontOptions;

class BitmapFontOptionsTests {

	private String mFontFilepath;
	private String mBitmapName;
	private int mPointSize;
	private int mUnicodeStartingPoint;
	private int mUnicodeEndingPoint;
	private int mGlyphBorderSize;

	BitmapFontOptions mOptions;

	@BeforeEach
	void setUp() {
		mFontFilepath = "res//fonts//arial.ttf";
		mBitmapName = "test_bitmap";
		mPointSize = 16;
		mUnicodeStartingPoint = 32;
		mUnicodeEndingPoint = 127;
		mGlyphBorderSize = 1;

		mOptions = BitmapFontOptions.fromArguments(mFontFilepath, mBitmapName, mPointSize, mUnicodeStartingPoint, mUnicodeEndingPoint, mGlyphBorderSize);
	}

	@Test
	@DisplayName("The FromArguments method correctly maps the actual arguments to the correct fields")
	void On_FromArguments_Parameters_Return_Correctly() {
		assertTrue(mOptions.fontFilepath.equals(mFontFilepath));
		assertTrue(mOptions.outputFolder.equals(mBitmapName));
		assertTrue(mOptions.pointSize == mPointSize);
		assertTrue(mOptions.unicodeStartCode == mUnicodeStartingPoint);
		assertTrue(mOptions.unicodeEndCode == mUnicodeEndingPoint);
		assertTrue(mOptions.spritePadding == mGlyphBorderSize);
	}

	@Test
	@DisplayName("Validation of the input options should return true")
	void Then_BitmapFontOptions_Should_Validate_To_True() {
		assertTrue(BitmapFontOptions.validateInputOptions(mOptions));
	}

}
