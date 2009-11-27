package org.jpropeller.ui.impl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.AffineTransform;

import javax.swing.Icon;

/**
 * A simple icon that draws a swatch of flat colour with an
 * outline of another color, with a set width and height,
 * and a blank border around the swatch
 */
public class ColorSwatchIcon implements Icon {

	Paint fill;
	Paint outline = Color.BLACK;
	int width = 20;
	int height = 12;
	int borderWidth = 0;
	int borderHeight = 0;
	
	/**
	 * Create a 20x12 icon with default blue fill,
	 * black outline
	 */
	public ColorSwatchIcon() {
		this(Color.BLUE);
	}
	
	/**
	 * Create a 20x12 icon with default black outline
	 * @param fill		Initial color
	 */
	public ColorSwatchIcon(Paint fill) {
		this.fill = fill;
	}
	
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
		
		Paint originalPaint = g.getPaint();
		
		if (fill != null) {
			AffineTransform oldTransform = g.getTransform();
			
			AffineTransform newTransform = new AffineTransform(oldTransform);
			newTransform.concatenate(AffineTransform.getTranslateInstance(x+borderWidth, y+borderHeight));
			g.setTransform(newTransform);

			g.setPaint(fill);
			g.fillRect(0, 0, width, height);
			
			g.setTransform(oldTransform);
		}
		
		g.setPaint(outline);
		g.drawRect(x + borderWidth, y + borderHeight, width, height);
		
		g.setPaint(originalPaint);
	}

	/**
	 * Get {@link Paint} to fill swatch - if null, swatch is just an outline
	 * Note that the {@link Paint} will be applied to a rectangle with top left at
	 * the origin, (0, 0), with width and height according toe {@link #getWidth()}
	 * and {@link #getHeight()}. Hence for example gradients should be set up
	 * to have correct scale, but start from (0, 0).
	 * @return
	 * 		swatch color
	 */
	public Paint getFill() {
		return fill;
	}

	/**
	 * Set {@link Paint} to fill swatch - if null, swatch is just an outline
	 * Note that the {@link Paint} will be applied to a rectangle with top left at
	 * the origin, (0, 0), with width and height according toe {@link #getWidth()}
	 * and {@link #getHeight()}. Hence for example gradients should be set up
	 * to have correct scale, but start from (0, 0).
	 * @param fill
	 * 		swatch color
	 */
	public void setFill(Paint fill) {
		this.fill = fill;
	}

	/**
	 * Get {@link Paint} to outline swatch
	 * @return
	 * 		outline {@link Paint}
	 */
	public Paint getOutline() {
		return outline;
	}

	/**
	 * Set color to outline swatch
	 * @param outline
	 * 		outline color
	 */
	public void setOutline(Paint outline) {
		this.outline = outline;
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
