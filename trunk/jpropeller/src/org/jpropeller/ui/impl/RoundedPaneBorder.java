package org.jpropeller.ui.impl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * Border designed to be used round the panel displaying
 * contents selected by a {@link JTabButton}.
 * Blends with {@link JTabButton}s displayed down its left
 * hand side.
 */
public class RoundedPaneBorder extends EmptyBorder {

	/**
	 * Default background color
	 */
	public final static Color DEFAULT_BG = new JLabel().getBackground();
	
    /**
     * Creates an initially unselected toggle button
     * without setting the text or image.
     */
	public RoundedPaneBorder() {
		super(4,4,4,4);
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
		Graphics2D g2d = (Graphics2D) g.create();
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		int radius = 10;
		int shadowSize = 4;
		int shadowAlpha = 20;
		Color shadowColor = new Color(0,0,0,shadowAlpha);
		Color halfShadowColor = new Color(0,0,0,shadowAlpha/2);
		Color clear = new Color(0,0,0,0);

		GradientPaint shadow1 = new GradientPaint(
				1, 1, 
				shadowColor, 
				1, 1 + shadowSize, 
				clear, 
				false);
		GradientPaint shadow2 = new GradientPaint(
				1, 1, 
				shadowColor, 
				1 + shadowSize, 1, 
				clear, 
				false);
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

//		GradientPaint highlight1 = new GradientPaint(
//				0, 0, 
//				new Color(0,0,0,highlightAlpha1), 
//				0, highlightSize, 
//				new Color(0,0,0,0), 
//				false);
//		
//		GradientPaint highlight2 = new GradientPaint(
//				0, h - highlightSize-1, 
//				new Color(255,255,255,0), 
//				0, h-1, 
//				new Color(255,255,255,highlightAlpha2), 
//				false);

		
//		GradientPaint highlight = new GradientPaint(		//highlight
//				0, 1, 
//				new Color(0,0,0,highlightAlpha1), 
//				0, h-3, 
//				new Color(255,255,255,highlightAlpha2), 
//				false);
		Color highlight = new Color(0,0,0,120);

		//Draw bg
		g2d.setPaint(DEFAULT_BG);
		g2d.fillRoundRect(0, 0, w-1, h-1, radius, radius);

		g2d.setPaint(shadowColor);
		g2d.fillRoundRect(1, 1, w-2, 30, radius, radius);

		//Draw shadow
		g2d.setPaint(shadow1);
		g2d.fillRect(1, 1, w-2, h-2);
		g2d.setPaint(shadow2);
		g2d.fillRect(1, 1, w-2, h-2);
		g2d.setPaint(shadow3);
		g2d.fillRect(1, 1, w-2, h-2);
		g2d.setPaint(shadow4);
		g2d.fillRect(1, 1, w-2, h-2);

		//Draw highlight
//		g2d.setPaint(highlight1);
//		g2d.drawRect(0, 0, w-1, h-1);
//		
//		g2d.setPaint(highlight2);
//		g2d.drawRect(0, 0, w-1, h-1);

		g2d.setPaint(highlight);
		g2d.drawRoundRect(0, 0, w-1, h-1, radius, radius);

		g2d.dispose();
	}


	
	/**
	 * Demonstrate the border
	 * 
	 * @param args
	 *            Ignored
	 */
	public static void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				showDemo();
			}
		});
	}

	/**
	 * Show demonstration
	 */
	public static void showDemo() {
		JFrame frame = new JFrame("Custom Buttons Demo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		;
		
		JPanel mainPanel = new JPanel();
		mainPanel.add(buildPanel("<html>First Option<br /><br /><br /></html>"));
		mainPanel.add(buildPanel("<html>Second Option<br /><br /><br /></html>"));
		mainPanel.add(buildPanel("<html>Third Option<br /><br /><br /></html>"));
		
		frame.add(mainPanel);
		
		frame.setSize(700, 85);
		frame.setVisible(true);
	}

	private static JPanel buildPanel(String s) {
		JPanel panel = new JPanel();
		panel.setBorder(new RoundedPaneBorder());
		panel.add(new JLabel(s));
		return panel;
	}
}
