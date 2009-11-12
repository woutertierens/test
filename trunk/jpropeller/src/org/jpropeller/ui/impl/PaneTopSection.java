package org.jpropeller.ui.impl;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;

import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import org.jpropeller.util.GeneralUtils;

/**
 * Border designed to be used round the panel displaying
 * contents selected by a {@link JTabButton}.
 * Blends with {@link JTabButton}s displayed down its left
 * hand side.
 */
public class PaneTopSection extends EmptyBorder {

	/**
	 * Default background color
	 */
	public final static Color DEFAULT_BG = new JLabel().getBackground();
	
	private final int lineMargin = 5;
	private final int pad = 6;
	private final int radius = 10;
	private final int shadowSize = 4;
	private final int shadowAlpha = 20;
	private final Color shadowColor = new Color(0,0,0,shadowAlpha);
	private final Color clear = new Color(0,0,0,0);
	private final Color outline = new Color(120,120,120);
	private final Color bg = GeneralUtils.scaleColor(DEFAULT_BG, 0.9f);
	private final Color selectionColor = new Color(0,0,100, 40);
	
	private final GradientPaint shadowTop = new GradientPaint(
			1, 1, 
			shadowColor, 
			1, 1 + shadowSize, 
			clear, 
			false);
	private final GradientPaint shadowLeft = new GradientPaint(
			1, 1, 
			shadowColor, 
			1 + shadowSize, 1, 
			clear, 
			false);
	private final boolean drawLeft;
	private final boolean drawRight;
	private final boolean selected;
	private final boolean shadows = false;
	
    /**
     * Creates an initially unselected toggle button
     * without setting the text or image.
     * @param border		The size of border - should be at least 4
     * @param drawLeft		True to draw right border 
     * @param drawRight 	True to draw left border
     * @param selected		Whether the tab is selected
     */
	public PaneTopSection(int border, boolean drawLeft, boolean drawRight, boolean selected) {
		super(border, border, border, border);
		this.drawLeft = drawLeft;
		this.drawRight = drawRight;
		this.selected = selected;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
		Graphics2D g2d = (Graphics2D) g.create();
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		GradientPaint shadowRight = new GradientPaint(
				x + w-2, y + 1, 
				shadowColor, 
				x + w-shadowSize-2, y + 1, 
				clear, 
				false);

		int leftPad = drawLeft ? 0 : 6;
		int rightPad = drawRight ? 0 : 6;
		
		//Draw bg
		g2d.setPaint(bg);
		g2d.fillRoundRect(x-leftPad, y, w - 1 + leftPad + rightPad, h - 1 + pad, radius, radius);

		
		if (selected) {
			g2d.setPaint(selectionColor);
			Shape oldClip = g2d.getClip();
			if (!drawRight) {
				g2d.clipRect(x, y, w-1, h);
			}
			g2d.fillRoundRect(x + 1 - leftPad, y + 1, w - 2 + leftPad + rightPad, h - 2 + pad, radius, radius);
			g2d.setClip(oldClip);
		}
		
		//Draw shadow
		if (shadows) {
			g2d.setPaint(shadowTop);
			g2d.fillRect(x + 1-leftPad, y + 1, w-2+leftPad + rightPad, h-1);
			if (drawLeft) {
				g2d.setPaint(shadowLeft);
				g2d.fillRect(x + 1-leftPad, y + 1, w-2+leftPad + rightPad, h-1);
			}
			if (drawRight) {
				g2d.setPaint(shadowRight);
				g2d.fillRect(x + 1-leftPad, y + 1, w-2+leftPad + rightPad, h-1);
			}
		}

		//Draw divider line
		if (!drawRight) {
			g2d.setPaint(outline);
			g2d.fillRect(x + w-1, y + 1+lineMargin, w-1, h-1-lineMargin*2);
		}
		
		g2d.setPaint(outline);
		g2d.drawRoundRect(x-leftPad, y, w-1+leftPad+rightPad, h-1+pad, radius, radius);

		g2d.dispose();
	}

}
