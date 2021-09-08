package com.lintfords.glyphextractor.presenters;

import java.io.File;

import com.lintfords.glyphextractor.data.BitmapFontOptions;

public interface IMainPresenter {
	BitmapFontOptions getBitmapFontOptions();

	void setPreviewCharacter(int pNewPreviewChar);

	void setFontFilepath(File pNewFontFile);

	void setOutputFolder(File pNewOutputFolder);

	void exportGlyphs();

	void saveConfiguration(File pConfigurationFile);

	void loadConfiguration(File pConfigurationFile);

	void setPointSize(int pNewPointSize);

	void setSpritePadding(int pNewSpritePadding);

	void setUnicodeStartPoint(int pNewUnicodeStartPoint);

	void setUnicodeEndPoint(int pNewUnicodeEndPoint);

	void setOutlineGlyphs(int pOutlineWidth);

	void setFillColor(String pNewFillColorHex);

	void resetOptions();

	void setAntiAliasing(boolean pNewValue);

	void setGlyphNamingConventionUseHex(boolean pUseHex);

	void setOutlineColor(String pNewOutlineColorHex);

	String enforceConfigurationFileExtension(String pFilename);
}
