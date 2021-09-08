package com.lintfords.glyphextractor.views;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ImagePanel extends JPanel {
	private static final long serialVersionUID = -2052219687118868176L;

	private BufferedImage image;

	public void setBufferedImage(BufferedImage pImage) {
		image = pImage;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Color lOrigColor = g.getColor();
		g.clearRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.blue);
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

		if (image != null) {
			g.setColor(Color.red);
			g.drawRect(0, 0, image.getWidth() * 2, image.getHeight() * 2);
			System.out.println("size: " + image.getWidth() + "x" + image.getHeight());
			g.drawImage(image, 0, 0, image.getWidth() * 2, image.getHeight() * 2, this);
		}

		g.setColor(lOrigColor);
	}
}
