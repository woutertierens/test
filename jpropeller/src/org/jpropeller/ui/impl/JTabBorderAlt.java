package org.jpropeller.ui.impl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

/**
 * Border designed to be used round the panel displaying
 * contents selected by a {@link JTabButton}.
 * Blends with {@link JTabButton}s displayed down its left
 * hand side.
 */
public class JTabBorderAlt extends EmptyBorder {

	private Color shadowColor;
	private Color bgColor;
	private int highlightAlpha1;
	private int highlightAlpha2;
	
	private int outerRoundRectSize = 10;
	private int highlightSize = 5;
	private static int shadowOffset = 3;
	private static int border = shadowOffset + 1;

	/**
	 * Default background color
	 */
	//public final static Color DEFAULT_BG = new Color(0.95f, 0.95f, 0.95f);
	public final static Color DEFAULT_BG = new JLabel().getBackground();
		
	PaintBox selected;
	
	{
		highlightAlpha1 = 255;
		highlightAlpha2 = 40;
		bgColor = UIManager.getColor("itis.background.selected");
		shadowColor = new Color(0f,0f,0f, 0.2f);
	}

    /**
     * Creates an initially unselected toggle button
     * without setting the text or image.
     */
	public JTabBorderAlt() {
		super(6,6,6 + border,6 + border);
	}

	private void updateBoxes(int w, int h) {
		selected = new PaintBox(
				bgColor,	//bg
						
				new GradientPaint(				//highlight1
						0, 0, 
						new Color(255,255,255,highlightAlpha1), 
						0, highlightSize, 
						new Color(255,255,255,0), 
						false),
				
				new GradientPaint(				//highlight2
						0, h - border - highlightSize, 
						new Color(0,0,0,0), 
						0, h - border, 
						new Color(0,0,0,highlightAlpha2), 
						false)
				);
	}
	
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
		Graphics2D g2d = (Graphics2D) g.create();
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		updateBoxes(w, h);


		PaintBox box = selected;

		//Draw shadow
		g2d.setPaint(shadowColor);
		g2d.fillRoundRect(shadowOffset, shadowOffset, w-border, h-border, outerRoundRectSize, outerRoundRectSize);
		
		//Draw bg
		g2d.setPaint(box.getBg());
		g2d.fillRoundRect(0, 0, w-border, h-border, outerRoundRectSize, outerRoundRectSize);

//		//Draw outline
//		g2d.setPaint(box.getOutline());
//		g2d.drawRoundRect(0, 0, w - 1, h - 1, outerRoundRectSize,
//				outerRoundRectSize);
		
		//Draw highlight
		g2d.setPaint(box.getHighlight1());
		g2d.drawRoundRect(0, 0, w-border, h-border, outerRoundRectSize, outerRoundRectSize);
		
		g2d.setPaint(box.getHighlight2());
		g2d.drawRoundRect(0, 0, w-border, h-border, outerRoundRectSize, outerRoundRectSize);
		
		//Draw right hand divider
		//g2d.setPaint(box.getHighlight());
		//g2d.drawLine(w-1, 1, w-1, h-3);
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
		
		JPanel panel = new JPanel();
		panel.setBorder(new JTabBorderAlt());
		panel.setOpaque(false);
		panel.add(new JLabel("Example Contents"));

		JPanel mainPanel = new JPanel();
		mainPanel.setBackground(new Color(0.8f, 0.8f, 0.8f));
		mainPanel.add(panel);
		
		frame.add(mainPanel);
		
		frame.setSize(700, 85);
		frame.setVisible(true);
	}
	
	/**
	 * Stores a set of {@link Paint}s to use to draw button
	 */
	private class PaintBox {
		private Paint bg;
		private Paint highlight1;
		private Paint highlight2;
		public PaintBox(Paint bg, Paint highlight1, Paint highlight2) {
			super();
			this.bg = bg;
			this.highlight1 = highlight1;
			this.highlight2 = highlight2;
		}
		public Paint getBg() {
			return bg;
		}
		public Paint getHighlight1() {
			return highlight1;
		}
		public Paint getHighlight2() {
			return highlight2;
		}
	}
}
