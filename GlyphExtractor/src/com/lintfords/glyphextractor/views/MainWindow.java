package com.lintfords.glyphextractor.views;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.lintfords.glyphextractor.data.BitmapFont;
import com.lintfords.glyphextractor.data.BitmapFontOptions;
import com.lintfords.glyphextractor.presenters.IMainPresenter;
import com.lintfords.glyphextractor.presenters.MainWindowPresenter;

public class MainWindow extends JFrame {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 6157664264606288469L;
	private static final int STATUS_TEXT_SHOW_TIME_MS = 2000;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private IMainPresenter mMainWindowPresenter;

	private Timer mStatusTimer;
	private JLabel mLabelStatus;
	private JTextField mTextAreaFontFilename;
	private JTextField mTextAreaOutputFolder;
	private JTextField mTextAreaPointSize;
	private JTextField mTextAreaUnicodeStart;
	private JTextField mTextAreaUnicodeEnd;
	private JTextField mTextAreaOutlineSize;
	private JTextField mTextAreaFillColor;
	private JTextField mTextAreaOutlineColor;
	private JCheckBox mCheckBoxStyleBold;
	private JTextField mTextAreaSpritePadding;
	private JRadioButton mRadioButtonGlyphHex;
	private JRadioButton mRadioButtonGlyphDec;
	private JCheckBox mCheckBoxAntiAliasing;
	private ImagePanel mPreviewImage;
	private JTextField mTextAreaPreviewTextChar;
	private JLabel mLabelPreviewTextCharUnicode;

	private MainWindow mThisMainWindow = this;

	private final int COLUMN_1_X = 10;
	private final int COLUMN_2_X = 150;
	private final int COLUMN_3_X = 510;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MainWindow(BitmapFont pBitmapFont) {
		mMainWindowPresenter = new MainWindowPresenter(this, pBitmapFont);

		createWindow();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void createWindow() {
		setTitle("Glyph Exporter");
		setSize(570, 455);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		getContentPane().setLayout(null);

		ImageIcon image = new ImageIcon("res/icon.png");
		setIconImage(image.getImage());

		createControls();
	}

	private void createControls() {
		final int lVerticalSpacing = 30;
		int lCurrentYPos = 20;

		// Status label
		mLabelStatus = new JLabel();
		mLabelStatus.setBounds(0, 390, 570, 25);

		createFontFilepathControls(lCurrentYPos);
		createOutputFolderControls(lCurrentYPos += lVerticalSpacing);
		createPointSizeControl(lCurrentYPos += lVerticalSpacing);
		createUnicodeRangeControls(lCurrentYPos += lVerticalSpacing);
		createStyleControls(lCurrentYPos += lVerticalSpacing);
		createOutlineGlyphsControls(lCurrentYPos += lVerticalSpacing);
		createFillColorControls(lCurrentYPos += lVerticalSpacing);
		createOutlineColorControls(lCurrentYPos += lVerticalSpacing);
		createGlyphNamingControls(lCurrentYPos += lVerticalSpacing);
		createAntiAliasingControls(lCurrentYPos += lVerticalSpacing);
		createSpritePaddingControls(lCurrentYPos += lVerticalSpacing);
		createPreviewImage();

		// Buttons

		lCurrentYPos += lVerticalSpacing * 1.5f;
		// Reset Window
		JButton lButtonReset = new JButton();
		lButtonReset.setBounds(10, lCurrentYPos, 75, 25);
		lButtonReset.setText("Reset");
		lButtonReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetForm();
			}
		});

		// Save Configuration
		JButton lButtonSave = new JButton();
		lButtonSave.setBounds(90, lCurrentYPos, 75, 25);
		lButtonSave.setText("Save");
		lButtonSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Path currentRelativePath = Paths.get("");

				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(currentRelativePath.toAbsolutePath().toString()));
				fileChooser.setFileFilter(new FileNameExtensionFilter("Glyph Export Configuration(.gec)", "gec", "gec"));
				fileChooser.setAcceptAllFileFilterUsed(false);
				int result = fileChooser.showSaveDialog(mThisMainWindow);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					mMainWindowPresenter.saveConfiguration(selectedFile);
				}
			}
		});

		// Load Configuration
		JButton lButtonLoad = new JButton();
		lButtonLoad.setBounds(170, lCurrentYPos, 75, 25);
		lButtonLoad.setText("Load");
		lButtonLoad.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Path currentRelativePath = Paths.get("");

				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new FileNameExtensionFilter("Glyph Export Configuration(.gec)", "gec", "gec"));
				fileChooser.setCurrentDirectory(new File(currentRelativePath.toAbsolutePath().toString()));
				int result = fileChooser.showOpenDialog(mThisMainWindow);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					mMainWindowPresenter.loadConfiguration(selectedFile);
				}
			}
		});

		// Export Glyphs
		JButton lButtonExport = new JButton();
		lButtonExport.setBounds(470, lCurrentYPos, 75, 25);
		lButtonExport.setText("Export");
		lButtonExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mMainWindowPresenter.exportGlyphs();
			}
		});

		getContentPane().add(lButtonReset);
		getContentPane().add(lButtonSave);
		getContentPane().add(lButtonLoad);
		getContentPane().add(lButtonExport);

		getContentPane().add(mLabelStatus);

	}

	private void createPreviewImage() {
		mTextAreaPreviewTextChar = new JTextField();
		mLabelPreviewTextCharUnicode = new JLabel();
		mPreviewImage = new ImagePanel();

		mTextAreaPreviewTextChar.setBounds(425, 170, 25, 25);
		mTextAreaPreviewTextChar.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				updatePreviewCharacter();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				updatePreviewCharacter();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updatePreviewCharacter();
			}
		});
		mTextAreaPreviewTextChar.setText("A");
		mLabelPreviewTextCharUnicode.setBounds(455, 170, 25, 25);
		mPreviewImage.setBounds(425, 200, 120, 120);

		getContentPane().add(mTextAreaPreviewTextChar);
		getContentPane().add(mLabelPreviewTextCharUnicode);
		getContentPane().add(mPreviewImage);
	}

	private void createFontFilepathControls(int pYPosition) {
		JLabel lLabelFontFile = new JLabel();
		lLabelFontFile.setText("Font File");
		lLabelFontFile.setBounds(COLUMN_1_X, pYPosition, 130, 25);

		mTextAreaFontFilename = new JTextField();
		mTextAreaFontFilename.setBounds(COLUMN_2_X, pYPosition, 350, 25);

		JButton lButtonFontFilename = new JButton();
		lButtonFontFilename.setText("...");
		lButtonFontFilename.setBounds(COLUMN_3_X, pYPosition, 25, 25);
		lButtonFontFilename.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Path currentRelativePath = Paths.get("");

				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new FileNameExtensionFilter("Font Files", "ttf", "otf"));
				fileChooser.setCurrentDirectory(new File(currentRelativePath.toAbsolutePath().toString()));
				int result = fileChooser.showOpenDialog(mThisMainWindow);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					mMainWindowPresenter.setFontFilepath(selectedFile);
				}
			}
		});

		getContentPane().add(lLabelFontFile);
		getContentPane().add(mTextAreaFontFilename);
		getContentPane().add(lButtonFontFilename);
	}

	private void createOutputFolderControls(int pYPosition) {
		JLabel lLabelOutputFolder = new JLabel();
		lLabelOutputFolder.setText("Output Folder");
		lLabelOutputFolder.setBounds(COLUMN_1_X, pYPosition, 130, 25);

		mTextAreaOutputFolder = new JTextField();
		mTextAreaOutputFolder.setBounds(COLUMN_2_X, pYPosition, 350, 25);
		mTextAreaOutputFolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateOutputFolder();
			}
		});

		JButton lButtonOutputFolder = new JButton();
		lButtonOutputFolder.setText("...");
		lButtonOutputFolder.setBounds(COLUMN_3_X, pYPosition, 25, 25);
		lButtonOutputFolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Path currentRelativePath = Paths.get("");

				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(currentRelativePath.toAbsolutePath().toString()));
				fileChooser.setAcceptAllFileFilterUsed(false);
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = fileChooser.showOpenDialog(mThisMainWindow);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					mMainWindowPresenter.setOutputFolder(selectedFile);
				}
			}
		});

		getContentPane().add(lLabelOutputFolder);
		getContentPane().add(mTextAreaOutputFolder);
		getContentPane().add(lButtonOutputFolder);
	}

	private void createPointSizeControl(int pYPosition) {
		JLabel lLabelPointSize = new JLabel();
		lLabelPointSize.setText("Point Size");
		lLabelPointSize.setBounds(COLUMN_1_X, pYPosition, 130, 25);

		mTextAreaPointSize = new JTextField();
		mTextAreaPointSize.setBounds(COLUMN_2_X, pYPosition, 100, 25);
		mTextAreaPointSize.setText("16");
		mTextAreaPointSize.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				updatePointSize();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				updatePointSize();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updatePointSize();
			}
		});

		getContentPane().add(lLabelPointSize);
		getContentPane().add(mTextAreaPointSize);
	}

	private void createUnicodeRangeControls(int pYPosition) {
		JLabel lLabelUnicode = new JLabel();
		lLabelUnicode.setText("Unicode Code-Point Range");
		lLabelUnicode.setBounds(COLUMN_1_X, pYPosition, 130, 25);

		mTextAreaUnicodeStart = new JTextField();
		mTextAreaUnicodeStart.setBounds(COLUMN_2_X, pYPosition, 75, 25);
		mTextAreaUnicodeStart.setText("32");
		mTextAreaUnicodeStart.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				updateUnicodeStartPoint();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateUnicodeStartPoint();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateUnicodeStartPoint();
			}
		});

		mTextAreaUnicodeEnd = new JTextField();
		mTextAreaUnicodeEnd.setBounds(COLUMN_2_X + 80, pYPosition, 75, 25);
		mTextAreaUnicodeEnd.setText("127");
		mTextAreaUnicodeEnd.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				updateUnicodeEndPoint();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateUnicodeEndPoint();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateUnicodeEndPoint();
			}
		});

		getContentPane().add(lLabelUnicode);
		getContentPane().add(mTextAreaUnicodeStart);
		getContentPane().add(mTextAreaUnicodeEnd);
	}

	private void createOutlineGlyphsControls(int pYPosition) {
		JLabel lLabelOutlineGlyphs = new JLabel();
		lLabelOutlineGlyphs.setText("Outline Glyphs");
		lLabelOutlineGlyphs.setBounds(COLUMN_1_X, pYPosition, 130, 25);

		mTextAreaOutlineSize = new JTextField();
		mTextAreaOutlineSize.setBounds(COLUMN_2_X, pYPosition, 40, 25);
		mTextAreaOutlineSize.setText("0");
		mTextAreaOutlineSize.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				updateOutlineGlyphs();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateOutlineGlyphs();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateOutlineGlyphs();
			}
		});

		getContentPane().add(lLabelOutlineGlyphs);
		getContentPane().add(mTextAreaOutlineSize);
	}

	private void createFillColorControls(int pYPosition) {
		JLabel lLabelFillColor = new JLabel();
		lLabelFillColor.setText("Fill Color");
		lLabelFillColor.setBounds(COLUMN_1_X, pYPosition, 130, 25);

		mTextAreaFillColor = new JTextField();
		mTextAreaFillColor.setBounds(COLUMN_2_X, pYPosition, 100, 25);
		mTextAreaFillColor.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				updateFillColor();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateFillColor();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateFillColor();
			}
		});
		mTextAreaFillColor.setText("#FFFFFFFF");

		JButton lColorChooserFill = new JButton();
		lColorChooserFill.setBounds(COLUMN_2_X + 110, pYPosition, 75, 25);
		lColorChooserFill.setText("Pick ...");
		lColorChooserFill.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color lChosenColor = JColorChooser.showDialog(null, "", getForeground());
				if (lChosenColor != null) {
					String lRGBAHexValue = String.format("#%02x%02x%02x%02x", lChosenColor.getRed(), lChosenColor.getGreen(), lChosenColor.getBlue(), lChosenColor.getAlpha());
					mTextAreaFillColor.setText(lRGBAHexValue);
				}
			}
		});

		getContentPane().add(lLabelFillColor);
		getContentPane().add(mTextAreaFillColor);
		getContentPane().add(lColorChooserFill);
	}

	private void createStyleControls(int pYPosition) {
		JLabel lLabelStyleBold = new JLabel();
		lLabelStyleBold.setText("Bold");
		lLabelStyleBold.setBounds(COLUMN_1_X, pYPosition, 130, 25);

		mCheckBoxStyleBold = new JCheckBox();
		mCheckBoxStyleBold.setBounds(COLUMN_2_X, pYPosition, 20, 20);
		mCheckBoxStyleBold.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mMainWindowPresenter.setStyleBold(mCheckBoxStyleBold.isSelected());
			}
		});

		getContentPane().add(lLabelStyleBold);
		getContentPane().add(mCheckBoxStyleBold);
	}

	private void createOutlineColorControls(int pYPosition) {
		JLabel lLabelOutlineColor = new JLabel();
		lLabelOutlineColor.setText("Outline Color");
		lLabelOutlineColor.setBounds(COLUMN_1_X, pYPosition, 130, 25);

		mTextAreaOutlineColor = new JTextField();
		mTextAreaOutlineColor.setBounds(COLUMN_2_X, pYPosition, 100, 25);
		mTextAreaOutlineColor.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				updateOutlineColor();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateOutlineColor();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateOutlineColor();
			}
		});
		mTextAreaOutlineColor.setText("#000000FF");

		JButton lColorChooserOutline = new JButton();
		lColorChooserOutline.setBounds(COLUMN_2_X + 110, pYPosition, 75, 25);
		lColorChooserOutline.setText("Pick ...");
		lColorChooserOutline.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color lChosenColor = JColorChooser.showDialog(null, "", getForeground());
				if (lChosenColor != null) {
					String lRGBAHexValue = String.format("#%02x%02x%02x%02x", lChosenColor.getRed(), lChosenColor.getGreen(), lChosenColor.getBlue(), lChosenColor.getAlpha());
					mTextAreaOutlineColor.setText(lRGBAHexValue);
				}
			}
		});

		getContentPane().add(lLabelOutlineColor);
		getContentPane().add(mTextAreaOutlineColor);
		getContentPane().add(lColorChooserOutline);
	}

	private void createGlyphNamingControls(int pYPosition) {
		JLabel lLabelGlyphNaming = new JLabel();
		lLabelGlyphNaming.setText("Glyph Naming");
		lLabelGlyphNaming.setBounds(COLUMN_1_X, pYPosition, 130, 25);

		ButtonGroup lGlyphNamingGroup = new ButtonGroup();
		mRadioButtonGlyphHex = new JRadioButton();
		mRadioButtonGlyphHex.setBounds(COLUMN_2_X, pYPosition, 75, 25);
		mRadioButtonGlyphHex.setText("Hex");
		mRadioButtonGlyphHex.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateGlyphNamingConventions();

			}
		});
		mRadioButtonGlyphDec = new JRadioButton();
		mRadioButtonGlyphDec.setBounds(COLUMN_2_X + 80, pYPosition, 75, 25);
		mRadioButtonGlyphDec.setText("Dec");
		mRadioButtonGlyphDec.setSelected(true);
		mRadioButtonGlyphDec.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateGlyphNamingConventions();

			}
		});

		lGlyphNamingGroup.add(mRadioButtonGlyphHex);
		lGlyphNamingGroup.add(mRadioButtonGlyphDec);

		getContentPane().add(lLabelGlyphNaming);
		getContentPane().add(mRadioButtonGlyphHex);
		getContentPane().add(mRadioButtonGlyphDec);
	}

	private void createAntiAliasingControls(int pYPosition) {
		JLabel lLabelAntiAliasing = new JLabel();
		lLabelAntiAliasing.setText("Anti-Aliasing");
		lLabelAntiAliasing.setBounds(COLUMN_1_X, pYPosition, 130, 25);

		mCheckBoxAntiAliasing = new JCheckBox();
		mCheckBoxAntiAliasing.setBounds(COLUMN_2_X, pYPosition, 20, 20);
		mCheckBoxAntiAliasing.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mMainWindowPresenter.setAntiAliasing(mCheckBoxAntiAliasing.isSelected());
			}
		});

		getContentPane().add(lLabelAntiAliasing);
		getContentPane().add(mCheckBoxAntiAliasing);
	}

	private void createSpritePaddingControls(int pYPosition) {
		JLabel lLabelSpritePadding = new JLabel();
		lLabelSpritePadding.setText("Sprite Padding");
		lLabelSpritePadding.setBounds(COLUMN_1_X, pYPosition, 130, 25);

		mTextAreaSpritePadding = new JTextField();
		mTextAreaSpritePadding.setBounds(COLUMN_2_X, pYPosition, 100, 25);
		mTextAreaSpritePadding.setText("1");
		mTextAreaSpritePadding.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				updateSpritePadding();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateSpritePadding();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateSpritePadding();
			}
		});

		getContentPane().add(lLabelSpritePadding);
		getContentPane().add(mTextAreaSpritePadding);
	}

	// --------------------------------------

	public void setStatusText(String mStatusText) {
		if (mStatusTimer != null) {
			mStatusTimer.cancel();
		}

		mLabelStatus.setText(mStatusText);

		mStatusTimer = new Timer();
		mStatusTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				mLabelStatus.setText("");

			}
		}, STATUS_TEXT_SHOW_TIME_MS);
	}

	public void setFontFilename(String pFontFilepath) {
		mTextAreaFontFilename.setText(pFontFilepath);
	}

	public void setStyleBold(boolean pBoldEnabled) {
		mCheckBoxStyleBold.setSelected(pBoldEnabled);
	}

	public void setOutputFolder(String pOutputFolder) {
		mTextAreaOutputFolder.setText(pOutputFolder);
	}

	public void setFillColor(String pFillColor) {
		mTextAreaFillColor.setText(pFillColor);
	}

	public void setOutlineColor(String pOutlineColor) {
		mTextAreaOutlineColor.setText(pOutlineColor);
	}

	public void setUnicodeRangePoints(int pStartPoint, int pEndPoint) {
		mTextAreaUnicodeStart.setText(String.valueOf(pStartPoint));
		mTextAreaUnicodeEnd.setText(String.valueOf(pEndPoint));
	}

	public void setOutlineGlyphs(String pOutlineGlyphs) {
		mTextAreaOutlineSize.setText(pOutlineGlyphs);
	}

	public void setGlyphNamingConvention(boolean pUseHex) {
		if (pUseHex) {
			mRadioButtonGlyphHex.setSelected(true);
		} else {
			mRadioButtonGlyphDec.setSelected(true);
		}
	}

	public void setAntiAliasing(boolean pAntiAliasingEnabled) {
		mCheckBoxAntiAliasing.setSelected(pAntiAliasingEnabled);
	}

	public void setSpritePadding(String pSpritePadding) {
		mTextAreaSpritePadding.setText(pSpritePadding);
	}

	public void setImagePreview(BufferedImage pImage) {
		mPreviewImage.setBufferedImage(pImage);
		invalidate();
	}

	// --------------------------------------

	private void resetForm() {
		Runnable doUpdatePreviewCharacter = new Runnable() {
			@Override
			public void run() {
				mMainWindowPresenter.resetOptions();

				BitmapFontOptions lOptions = mMainWindowPresenter.getBitmapFontOptions();

				mTextAreaFontFilename.setText(lOptions.fontFilepath);
				mTextAreaOutputFolder.setText(lOptions.outputFolder);
				mTextAreaPointSize.setText(String.valueOf(lOptions.pointSize));
				mTextAreaUnicodeStart.setText(String.valueOf(lOptions.unicodeStartCode));
				mTextAreaUnicodeEnd.setText(String.valueOf(lOptions.unicodeEndCode));
				mTextAreaFillColor.setText(lOptions.fillColorHex);
				mTextAreaOutlineColor.setText(lOptions.outlineColorHex);
				mCheckBoxAntiAliasing.setSelected(lOptions.useAntiAliasing);
				mTextAreaSpritePadding.setText(String.valueOf(lOptions.spritePadding));
				setImagePreview(null);
			}
		};
		SwingUtilities.invokeLater(doUpdatePreviewCharacter);
	}

	private void updatePreviewCharacter() {
		Runnable doUpdatePreviewCharacter = new Runnable() {
			@Override
			public void run() {
				String lPreviewCharacter = mTextAreaPreviewTextChar.getText();
				if (lPreviewCharacter.length() == 0) {
					mMainWindowPresenter.setPreviewCharacter(' ');
					mLabelPreviewTextCharUnicode.setText("");
				} else if (lPreviewCharacter.length() > 1) {
					lPreviewCharacter = lPreviewCharacter.substring(0, 1);
					mTextAreaPreviewTextChar.setText(lPreviewCharacter);
				} else if (lPreviewCharacter != null && lPreviewCharacter.length() == 1) {
					char lPreviewChar = (char) lPreviewCharacter.charAt(0);
					int lPreviewCharValue = (int) lPreviewChar;
					mMainWindowPresenter.setPreviewCharacter(lPreviewChar);
					mLabelPreviewTextCharUnicode.setText(String.valueOf(lPreviewCharValue));
				}
			}
		};
		SwingUtilities.invokeLater(doUpdatePreviewCharacter);
	}

	private void updateOutputFolder() {
		Runnable doUpdateSpritePadding = new Runnable() {
			@Override
			public void run() {
				try {
					File lOutputDirectory = new File(mTextAreaOutputFolder.getText());
					mMainWindowPresenter.setOutputFolder(lOutputDirectory);
				} catch (NumberFormatException ex) {
					mTextAreaSpritePadding.setText("");
					setStatusText("Invalid Output directory");
				}

			}
		};
		SwingUtilities.invokeLater(doUpdateSpritePadding);
	}

	private void updateGlyphNamingConventions() {
		boolean useHex = mRadioButtonGlyphHex.isSelected();
		mMainWindowPresenter.setGlyphNamingConventionUseHex(useHex);
	}

	private void updateSpritePadding() {
		Runnable doUpdateSpritePadding = new Runnable() {
			@Override
			public void run() {
				try {
					int lNewSpritePadding = Integer.parseInt(mTextAreaSpritePadding.getText());
					mMainWindowPresenter.setSpritePadding(lNewSpritePadding);
				} catch (NumberFormatException ex) {
					mTextAreaSpritePadding.setText("");
					setStatusText("Invalid number entered as sprite pading");
				}

			}
		};
		SwingUtilities.invokeLater(doUpdateSpritePadding);
	}

	private void updateFillColor() {
		Runnable doUpdateFillColor = new Runnable() {
			@Override
			public void run() {
				mMainWindowPresenter.setFillColor(mTextAreaFillColor.getText());
			}
		};
		SwingUtilities.invokeLater(doUpdateFillColor);
	}

	private void updateOutlineColor() {
		Runnable doUpdateOutlineColor = new Runnable() {
			@Override
			public void run() {
				mMainWindowPresenter.setOutlineColor(mTextAreaOutlineColor.getText());
			}
		};
		SwingUtilities.invokeLater(doUpdateOutlineColor);
	}

	private void updatePointSize() {
		Runnable doUpdatePointSize = new Runnable() {
			@Override
			public void run() {
				try {
					int lNewPointSize = Integer.parseInt(mTextAreaPointSize.getText());
					mMainWindowPresenter.setPointSize(lNewPointSize);
				} catch (NumberFormatException ex) {
					mTextAreaPointSize.setText("");
					setStatusText("Invalid number entered as point size");
				}

			}
		};
		SwingUtilities.invokeLater(doUpdatePointSize);
	}

	private void updateUnicodeStartPoint() {
		Runnable doUpdatePointSize = new Runnable() {
			@Override
			public void run() {
				try {
					int lNewUnicodeStart = Integer.parseInt(mTextAreaUnicodeStart.getText());
					mMainWindowPresenter.setUnicodeStartPoint(lNewUnicodeStart);
				} catch (NumberFormatException ex) {
					mTextAreaUnicodeStart.setText("");
					setStatusText("Invalid number entered as unicode start code");
				}

			}
		};
		SwingUtilities.invokeLater(doUpdatePointSize);
	}

	private void updateUnicodeEndPoint() {
		Runnable doUpdatePointSize = new Runnable() {
			@Override
			public void run() {
				try {
					int lNewUnicodeEnd = Integer.parseInt(mTextAreaUnicodeEnd.getText());
					mMainWindowPresenter.setUnicodeEndPoint(lNewUnicodeEnd);
				} catch (NumberFormatException ex) {
					mTextAreaUnicodeEnd.setText("");
					setStatusText("Invalid number entered as unicode end code");
				}

			}
		};
		SwingUtilities.invokeLater(doUpdatePointSize);
	}

	private void updateOutlineGlyphs() {
		Runnable doUpdateOutlineGlyphs = new Runnable() {
			@Override
			public void run() {
				try {
					int lNewOutlineGlyphWidth = Integer.parseInt(mTextAreaOutlineSize.getText());
					mMainWindowPresenter.setOutlineGlyphs(lNewOutlineGlyphWidth);
				} catch (NumberFormatException ex) {
					mTextAreaOutlineSize.setText("");
					setStatusText("Invalid number entered as outline glyph width");
				}

			}
		};
		SwingUtilities.invokeLater(doUpdateOutlineGlyphs);
	}

	public void setPointSizeValue(String pNewPointSize) {
		mTextAreaPointSize.setText(pNewPointSize);
	}

}