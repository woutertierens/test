package org.jpropeller.ui.impl;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;

import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.jpropeller.util.ViewUtils;

/**
 * Border designed to be used round the panel displaying
 * contents selected by a {@link JTabButton}.
 * Blends with {@link JTabButton}s displayed down its left
 * hand side.
 */
public class JTabBorder extends EmptyBorder {

	private Color outlineColor;
	private Color selColor;
	private int highlightAlpha;
	
	private int outerRoundRectSize;
	private int innerRoundRectSize;

	PaintBox selected;
	
	{
		outlineColor = new Color(100,100,100);
		highlightAlpha = 100;
		selColor = ViewUtils.outerColor();
		outerRoundRectSize = UIManager.getInt("itis.roundsize");
		innerRoundRectSize = outerRoundRectSize - 2;
	}

    /**
     * Creates an initially unselected toggle button
     * without setting the text or image.
     */
	public JTabBorder() {
		super(6,6,6,6);
	}

    /**
     * Creates an initially unselected toggle button
     * without setting the text or image.
     * @param inset The inset on all sides.
     */
	public JTabBorder(int inset) {
		super(inset,inset,inset,inset);
	}

	/**
	 * Set the highlight alpha value
	 * @param highlightAlpha
	 */
	public void setHighlightAlpha(int highlightAlpha) {
		this.highlightAlpha = highlightAlpha;
	}

	private void updateBoxes(int w, int h) {
		selected = new PaintBox(
				selColor,
				outlineColor,			//outline
				new GradientPaint(		//highlight
						0, 1, 
						new Color(255,255,255,highlightAlpha), 
						0, h-3, 
						new Color(0,0,0,highlightAlpha), 
						true)
				);
	}
	
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
		Graphics2D g2d = (Graphics2D) g.create();
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		updateBoxes(w, h);


		PaintBox box = selected;

		//Draw bg
		g2d.setPaint(box.getBg());
		g2d.fillRoundRect(0, 0, w-1, h-1, outerRoundRectSize, outerRoundRectSize);

		//Draw outline
		g2d.setPaint(box.getOutline());
		g2d.drawRoundRect(0, 0, w - 1, h - 1, outerRoundRectSize,
				outerRoundRectSize);
		
		//Draw highlight
		g2d.setPaint(box.getHighlight());
		g2d.drawRoundRect(1, 1, w - 3, h - 3, innerRoundRectSize,
				innerRoundRectSize);
		
		//Draw right hand divider
		//g2d.setPaint(box.getHighlight());
		//g2d.drawLine(w-1, 1, w-1, h-3);
		g2d.dispose();
	}

	/**
	 * Stores a set of {@link Paint}s to use to draw button
	 */
	private class PaintBox {
		private Paint bg;
		private Paint outline;
		private Paint highlight;
		public PaintBox(Paint bg, Paint outline, Paint highlight) {
			super();
			this.bg = bg;
			this.outline = outline;
			this.highlight = highlight;
		}
		public Paint getBg() {
			return bg;
		}
		public Paint getOutline() {
			return outline;
		}
		public Paint getHighlight() {
			return highlight;
		}
	}
}
