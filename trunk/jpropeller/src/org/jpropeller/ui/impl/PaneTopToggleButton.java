package org.jpropeller.ui.impl;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JToggleButton;

import org.jpropeller.util.GeneralUtils;

/**
 * Border designed to be used round the panel displaying
 * contents selected by a {@link JTabButton}.
 * Blends with {@link JTabButton}s displayed down its left
 * hand side.
 */
public class PaneTopToggleButton extends JToggleButton {

	/**
	 * Default background color
	 */
	public final static Color DEFAULT_BG = new JLabel().getBackground();
	
	private final int lineMargin = 5;
	private int pad = 6;
	private final int radius = 10;
	private final int shadowSize = 4;
	private final int shadowAlpha = 40;
	private final Color shadowColor = new Color(0,0,0,shadowAlpha);
	private final Color halfShadowColor = new Color(0,0,0,shadowAlpha/2);
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
	private boolean drawLeft = true;
	private boolean drawRight = true;
	private boolean shadows = true;
	
	{
		setFocusable(false);
		setContentAreaFilled(false);
		setBorderPainted(false);
	}

	
	/**
	 * True if left border should be drawn (if it is the leftmost tab)
	 * @return	draw left
	 */
	public boolean getDrawLeft() {
		return drawLeft;
	}

	/**
	 * True if left border should be drawn (if it is the leftmost tab)
	 * @param drawLeft draw left
	 */
	public void setDrawLeft(boolean drawLeft) {
		this.drawLeft = drawLeft;
	}

	/**
	 * True to draw bottom, false to miss it out
	 * @param drawBottom	Draw bottom
	 */
	public void setDrawBottom(boolean drawBottom) {
		pad = drawBottom ? 0 : 6;
	}
	
	/**
	 * True if right border should be drawn (if it is the rightmost tab)
	 * @return	draw right
	 */
	public boolean getDrawRight() {
		return drawRight;
	}

	/**
	 * True if right border should be drawn (if it is the rightmost tab)
	 * @param drawRight draw right
	 */
	public void setDrawRight(boolean drawRight) {
		this.drawRight = drawRight;
	}

	@Override
	public void paintComponent(Graphics g) {
		
		int x = 0;
		int y = 0;
		int w = getWidth();
		int h = getHeight();
		
		Graphics2D g2d = (Graphics2D) g.create();
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		GradientPaint shadowRight = new GradientPaint(
				x + w-2, y + 1, 
				shadowColor, 
				x + w-shadowSize-2, y + 1, 
				clear, 
				false);

		GradientPaint shadowBottom = new GradientPaint(
				1, h-2, 
				halfShadowColor, 
				1, h-shadowSize/2-2, 
				clear, 
				false);

		int leftPad = drawLeft ? 0 : 6;
		int rightPad = drawRight ? 0 : 6;
		
		//Draw bg
		g2d.setPaint(bg);
		g2d.fillRoundRect(x-leftPad, y, w - 1 + leftPad + rightPad, h - 1 + pad, radius, radius);

		
		if (isSelected()) {
			g2d.setPaint(selectionColor);
			Shape oldClip = g2d.getClip();
			if (!drawRight) {
				g2d.clipRect(x, y, w-1, h);
			}
			g2d.fillRoundRect(x + 1 - leftPad, y + 1, w - 2 + leftPad + rightPad, h - 2 + pad, radius-1, radius-1);
			g2d.setClip(oldClip);
		}
		
		//Draw shadow
		if (shadows) {
			g2d.setPaint(shadowTop);
			g2d.fillRoundRect(x + 1-leftPad, y + 1, w-2+leftPad + rightPad, h-1+pad, radius, radius);
			if (drawLeft) {
				g2d.setPaint(shadowLeft);
				g2d.fillRoundRect(x + 1-leftPad, y + 1, w-2+leftPad + rightPad, h-1+pad, radius, radius);
			}
			if (drawRight) {
				g2d.setPaint(shadowRight);
				g2d.fillRoundRect(x + 1-leftPad, y + 1, w-2+leftPad + rightPad, h-1+pad, radius, radius);
			}
			if (pad == 0) {
				g2d.setPaint(shadowBottom);
				g2d.fillRoundRect(x + 1-leftPad, y + 1, w-2+leftPad + rightPad, h-1+pad, radius, radius);
			}
		}
		
		if (!drawRight) {
			g2d.setPaint(outline);
			g2d.fillRect(x + w-1, y + 1+lineMargin, 1, h-1-lineMargin*2);
		}
		
		g2d.setPaint(outline);
		g2d.drawRoundRect(x-leftPad, y, w-1+leftPad+rightPad, h-1+pad, radius, radius);

		g2d.dispose();
		
		//FIXME make this better
		setForeground(isSelected() ? Color.blue : Color.black);
		
		super.paintComponent(g);
	}

	//Constructors

    /**
     * Creates a toggle button where properties are taken from the 
     * Action supplied.
     * @param a 	{@link Action}
     */
	public PaneTopToggleButton(Action a) {
		super(a);
	}

    /**
     * Creates a toggle button with the specified image 
     * and selection state, but no text.
     *
     * @param icon  the image that the button should display
     * @param selected  if true, the button is initially selected;
     *                  otherwise, the button is initially unselected
     */
	public PaneTopToggleButton(Icon icon, boolean selected) {
		super(icon, selected);
	}

    /**
     * Creates an initially unselected toggle button
     * with the specified image but no text.
     *
     * @param icon  the image that the button should display
     */
	public PaneTopToggleButton(Icon icon) {
		super(icon);
	}

    /**
     * Creates a toggle button with the specified text
     * and selection state.
     *
     * @param text  the string displayed on the toggle button
     * @param selected  if true, the button is initially selected;
     *                  otherwise, the button is initially unselected
     */
	public PaneTopToggleButton(String text, boolean selected) {
		super(text, selected);
	}

    /**
     * Creates a toggle button with the specified text, image, and
     * selection state.
     *
     * @param text the text of the toggle button
     * @param icon  the image that the button should display
     * @param selected  if true, the button is initially selected;
     *                  otherwise, the button is initially unselected
     */
	public PaneTopToggleButton(String text, Icon icon, boolean selected) {
		super(text, icon, selected);
	}

    /**
     * Creates a toggle button that has the specified text and image,
     * and that is initially unselected.
     *
     * @param text the string displayed on the button
     * @param icon  the image that the button should display
     */
	public PaneTopToggleButton(String text, Icon icon) {
		super(text, icon);
	}

    /**
     * Creates an unselected toggle button with the specified text.
     *
     * @param text  the string displayed on the toggle button
     */
	public PaneTopToggleButton(String text) {
		super(text);
	}
}
