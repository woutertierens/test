package org.jpropeller.ui.impl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;

/**
 * A simple icon that draws a swatch of flat colour with an
 * outline of another color, with a set width and height,
 * and a blank border around the swatch
 */
public class ColorSwatchIcon implements Icon {

	Color color = Color.BLUE;
	Color outlineColor = Color.BLACK;
	int width = 20;
	int height = 12;
	int borderWidth = 0;
	int borderHeight = 0;
	
	@Override
	public int getIconHeight() {
		return height + borderHeight * 2;
	}

	@Override
	public int getIconWidth() {
		return width + borderWidth * 2;
	}

	@Override
	public void paintIcon(Component c, Graphics graphics, int x, int y) {
		Graphics2D g = (Graphics2D) graphics;
		
		Color originalColor = g.getColor();
		
		if (color != null) {
			g.setColor(color);
			g.fillRect(x + borderWidth, y + borderHeight, width, height);
		}
		
		g.setColor(outlineColor);
		g.drawRect(x + borderWidth, y + borderHeight, width, height);
		
		g.setColor(originalColor);
	}

	/**
	 * Get color to fill swatch - if null, swatch is just an outline
	 * @return
	 * 		swatch color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Set color to fill swatch - if null, swatch is just an outline
	 * @param color
	 * 		swatch color
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Get color to outline swatch
	 * @return
	 * 		outline color
	 */
	public Color getOutlineColor() {
		return outlineColor;
	}

	/**
	 * Set color to outline swatch
	 * @param outlineColor
	 * 		outline color
	 */
	public void setOutlineColor(Color outlineColor) {
		this.outlineColor = outlineColor;
	}

	/**
	 * Get total width of swatch
	 * @return
	 * 		width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Set total width of swatch
	 * @param width
	 * 		width
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Get total height of swatch
	 * @return
	 * 		height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Set total height of swatch
	 * @param height
	 * 		height
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * Get width in pixels of border - there are borders this width
	 * on either side of the swatch
	 * @return
	 * 		border width
	 */
	public int getBorderWidth() {
		return borderWidth;
	}

	/**
	 * Set width in pixels of border - there are borders this width
	 * on either side of the swatch
	 * @param borderWidth
	 */
	public void setBorderWidth(int borderWidth) {
		this.borderWidth = borderWidth;
	}

	/**
	 * Get height in pixels of border - there are borders this height
	 * on either side of the swatch
	 * @return
	 * 		border height
	 */
	public int getBorderHeight() {
		return borderHeight;
	}

	/**
	 * Set height in pixels of border - there are borders this height
	 * on either side of the swatch
	 * @param borderHeight
	 */
	public void setBorderHeight(int borderHeight) {
		this.borderHeight = borderHeight;
	}

}
