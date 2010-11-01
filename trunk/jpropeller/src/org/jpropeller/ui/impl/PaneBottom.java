package org.jpropeller.ui.impl;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

/**
 * Border designed to be used round the panel displaying
 * contents selected by a {@link JTabButton}.
 * Blends with {@link JTabButton}s displayed down its left
 * hand side.
 */
public class PaneBottom extends EmptyBorder {

	/**
	 * Default background color
	 */
	public final static Color DEFAULT_BG = new JLabel().getBackground();
	
	private final int pad;
	private final int radius;
	private final int shadowSize = 4;
	private final int shadowAlpha = UIManager.getInt("itis.tab.shadowalpha");
	private final Color shadowColor = new Color(0,0,0,shadowAlpha);
	private final Color halfShadowColor = new Color(0,0,0,shadowAlpha/2);
	private final Color clear = new Color(0,0,0,0);
	private final GradientPaint shadow2 = new GradientPaint(
			1, 1, 
			shadowColor, 
			1 + shadowSize, 1, 
			clear, 
			false);
	private final GradientPaint shadow1 = new GradientPaint(
			1, 1, 
			shadowColor, 
			1, 1 + shadowSize, 
			clear, 
			false);

	private final Color outline = UIManager.getColor("itis.tab.outline");
;
	private final Color bg = DEFAULT_BG;
	private final boolean shadows = true;
	private final boolean showTop;

    /**
     * Creates a border with no extra padding
     */
	public PaneBottom() {
		this(0);
	}
	
    /**
     * Creates a border
     * @param extraPadding		The number of extra pixels of padding
     */
	public PaneBottom(int extraPadding) {
		this(extraPadding, false);
	}

    /**
     * Creates a border
     * @param extraPadding		The number of extra pixels of padding
     * @param showTop			True to show top border, false otherwise
     */
	public PaneBottom(int extraPadding, boolean showTop) {
		super((showTop ? 4 : 2)+extraPadding,4+extraPadding,4+extraPadding,4+extraPadding);
		radius = UIManager.getInt("itis.roundsize");
		pad = showTop ? 0 : 6;
		this.showTop = showTop;
	}
	
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
		Graphics2D g2d = (Graphics2D) g.create();
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);


		GradientPaint shadow3 = new GradientPaint(
				w-2, 1, 
				shadowColor, 
				w-shadowSize-2, 1, 
				clear, 
				false);
		GradientPaint shadow4 = new GradientPaint(
				1, h-2, 
				halfShadowColor, 
				1, h-shadowSize/2-2, 
				clear, 
				false);

		//Draw bg
		g2d.setPaint(bg);
		g2d.fillRoundRect(0, -pad, w-1, h-1+pad, radius, radius);

		//Draw shadow
		if (shadows) {
			if (showTop) {
				g2d.setPaint(shadow1);
				g2d.fillRoundRect(1, -pad, w-2, h-1+pad, radius, radius);
			}
			g2d.setPaint(shadow2);
			g2d.fillRoundRect(1, -pad, w-2, h-1+pad, radius, radius);
			g2d.setPaint(shadow3);
			g2d.fillRoundRect(1, -pad, w-2, h-1+pad, radius, radius);
			g2d.setPaint(shadow4);
			g2d.fillRoundRect(1, -pad, w-2, h-1+pad, radius, radius);
		}
		
		g2d.setPaint(outline);
		g2d.drawRoundRect(0, -pad, w-1, h-1+pad, radius, radius);

		g2d.dispose();
	}
}
