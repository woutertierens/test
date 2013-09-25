package org.jpropeller.ui.impl;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;

import javax.swing.Action;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JToggleButton;
import javax.swing.UIManager;

import org.jpropeller.util.GeneralUtils;

/**
 * Button designed to be used as a tab.
 */
public class JTabButton extends JToggleButton {

	/**
	 * The left border of the button should be rounded.
	 */
	public static final int LEFT_BORDER = 1;
	/**
	 * The right border of the button should be rounded.
	 */
	public static final int RIGHT_BORDER = 2;
	/**
	 * The top border of the button should be rounded.
	 */
	public static final int TOP_BORDER = 4;
	/**
	 * The bottom border of the button should be rounded.
	 */
	public static final int BOTTOM_BORDER = 8;
	
	/**
	 * Button to go at the top of a component.
	 */
	public static final int TOP_BUTTON = LEFT_BORDER | TOP_BORDER | RIGHT_BORDER;
	/**
	 * Button to go at the bottom of a component.
	 */
	public static final int BOTTOM_BUTTON = LEFT_BORDER | BOTTOM_BORDER | RIGHT_BORDER;
	/**
	 * Button to go at the left of a component.
	 */
	public static final int LEFT_BUTTON = LEFT_BORDER | TOP_BORDER | BOTTOM_BORDER;
	/**
	 * Button to go at the right of a component.
	 */
	public static final int RIGHT_BUTTON = RIGHT_BORDER | TOP_BORDER | BOTTOM_BORDER;

	/**
	 * Button to go at the left of a component, acting as a header for another component
	 */
	public static final int LEFT_HEADER_BUTTON = LEFT_BORDER | TOP_BORDER;

	
	/**
	 * Button that can be separate from other components, all borders rounded..
	 */
	public static final int INDEPENDENT_BUTTON = RIGHT_BORDER | TOP_BORDER | LEFT_BORDER | BOTTOM_BORDER;
	
	
	private int bordersPainted = LEFT_BUTTON;
	private int shadowsNotPainted = LEFT_BUTTON;
	
	private Color bgColor;
	private Color outlineColor;
	private Color fgColor;
	private Color selColor;
	private int highlightAlpha;
	
	private int outerRoundRectSize;
	private int innerRoundRectSize;
	private int pad = 16;
	private int shadeWidth = 8;
	
	private boolean pressDown = false;
	
	private Color unselectedForeground;
	private Color selectedForeground;
	
	private double curve = 0.1;

	PaintBox pressed;
	PaintBox selected;
	PaintBox off;
	Paint leftShadowPaint, rightShadowPaint, topShadowPaint, bottomShadowPaint;
	Paint blendPaint;
	private final int shadowAlpha;

	{
		setFocusable(false);
		setContentAreaFilled(false);
		setBorderPainted(false);
		
		outlineColor = new Color(100,100,100);
		
		highlightAlpha = 100;
		
		selColor = UIManager.getColor("itis.background.selected");
		unselectedForeground = UIManager.getColor("itis.foreground.unselected");
		selectedForeground = UIManager.getColor("itis.foreground.selected");
		
		outerRoundRectSize = UIManager.getInt("itis.roundsize");
		innerRoundRectSize = outerRoundRectSize - 2;
		shadowAlpha = UIManager.getInt("itis.tab.shadowalpha");

		curve = UIManager.getBoolean("itis.tab.flat") ? 0 : 0.1;
	}
	
	/**
	 * Set the highlight alpha value
	 * @param highlightAlpha
	 */
	public void setHighlightAlpha(int highlightAlpha) {
		this.highlightAlpha = highlightAlpha;
	}

    /**
     * Creates an initially unselected tab button
     * without setting the text or image, designed to
     * display at left side.
     * @param borders	Initial border settings
     */
	public JTabButton(int borders) {
		super();
		setBorders(borders);
	}

    /**
     * Creates an initially unselected tab button
     * without setting the text or image, designed to
     * display at left side.
     * @param borders	Initial border settings
     * @param noShadows Initial shadow settings - mask showing where shadows are omitted
     */
	public JTabButton(int borders, int noShadows) {
		super();
		setBorders(borders);
		shadowsNotPainted = noShadows;
	}

    /**
     * Creates an initially unselected tab button
     * without setting the text or image
     */
	public JTabButton() {
		super();
	}

	/**
	 * Set the round size of the rounded rectangles used to draw.
	 * @param roundSize Rounding size
	 */
	public void setRoundSize(int roundSize) {
		outerRoundRectSize = roundSize;
		innerRoundRectSize = roundSize-2;
	}
	
	/**
	 * Create a {@link JTabButton} for use as a 
	 * normal {@link JToggleButton}
	 * @param name		To display in button
	 * @return			new {@link JTabButton}
	 */
	public static JTabButton createToggle(String name) {
		JTabButton button = new JTabButton(name);
		button.setBorders(INDEPENDENT_BUTTON);
		button.pressDown = true;
		return button;
	}

	private void updateBoxes(int w, int h) {
		bgColor = getBackground();
		fgColor = getForeground();
		off = new PaintBox(
				new GradientPaint(		//bg
						0, 1, 
						bgColor, 
						0, h-3, 
						GeneralUtils.scaleColor(bgColor, 1-curve), 
						true),
				outlineColor,			//outline
				new GradientPaint(		//highlight
						0, 1, 
						new Color(255,255,255,highlightAlpha), 
						0, h-3, 
						new Color(0,0,0,highlightAlpha), 
						true)
				);
		
		if (pressDown) {
			selected = new PaintBox(
					new GradientPaint(		//bg
							0, 1, 
							GeneralUtils.scaleColor(bgColor, 1-curve), 
							0, h-3, 
							bgColor, 
							true),
					outlineColor,			//outline
					new GradientPaint(		//highlight
							0, 1, 
							new Color(0,0,0,highlightAlpha), 
							0, h-3, 
							new Color(255,255,255,highlightAlpha), 
							true)
					);
		} else {
			selected = new PaintBox(
					new GradientPaint(		//bg
							0, 1, 
							selColor, 
							0, h-3, 
							GeneralUtils.scaleColor(selColor, 1-curve), 
							true),
					outlineColor,			//outline
					new GradientPaint(		//highlight
							0, 1, 
							new Color(255,255,255,highlightAlpha), 
							0, h-3, 
							new Color(0,0,0,highlightAlpha), 
							true)
					);
		}
		
		if (pressDown) {
			pressed = new PaintBox(
					new GradientPaint(		//bg
							0, 1, 
							GeneralUtils.scaleColor(bgColor, 1+curve*2),
							0, h-3, 
							GeneralUtils.scaleColor(bgColor, 1d), 
							true),
					outlineColor,			//outline
					new GradientPaint(		//highlight
							0, 1, 
							new Color(255,255,255,highlightAlpha), 
							0, h-3, 
							new Color(0,0,0,highlightAlpha), 
							true)
					);
		} else {
			pressed = new PaintBox(
					new GradientPaint(		//bg
							0, 1, 
							GeneralUtils.scaleColor(selColor, 1 + curve),
							0, h-3, 
							GeneralUtils.scaleColor(selColor, 1), 
							true),
					outlineColor,			//outline
					new GradientPaint(		//highlight
							0, 1, 
							new Color(255,255,255,highlightAlpha), 
							0, h-3, 
							new Color(0,0,0,highlightAlpha), 
							true)
					);
		}

		topShadowPaint = new GradientPaint(
				0, 0,
				new Color(0,0,0,shadowAlpha),
				0, shadeWidth,
				new Color(0,0,0,0),
				false);

		bottomShadowPaint = new GradientPaint(
				0, h-shadeWidth, 
				new Color(0,0,0,0), 
				0, h,
				new Color(0,0,0,shadowAlpha), 
				false);
		
		rightShadowPaint = new GradientPaint(
				w - shadeWidth, 0, 
				new Color(0,0,0,0), 
				w, 0,
				new Color(0,0,0,shadowAlpha), 
				false);
		
		leftShadowPaint = new GradientPaint(
				0, 0,
				new Color(0,0,0,shadowAlpha),
				shadeWidth, 0,
				new Color(0,0,0,0),  
				false);
		
		blendPaint = new GradientPaint(
				w - shadeWidth, 0, 
				new Color(selColor.getRed(), selColor.getGreen(), selColor.getBlue(), 0), 
				w, 0,
				selColor, 
				false);

	}

	/**
	 * Sets which edges of the button are painted.
	 * @param borderMask	A bitmask of {@link #BOTTOM_BORDER}, {@link #TOP_BORDER}, 
	 * 						{@link #RIGHT_BORDER} and {@link #LEFT_BORDER}.
	 */
	public void setBorders(int borderMask) {
		this.bordersPainted = borderMask;
		this.shadowsNotPainted = borderMask;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		updateBoxes(getWidth(), getHeight());

		int h = getHeight();
		int w = getWidth();
		ButtonModel model = getModel();

		//Gray text if not enabled
		setForeground(model.isEnabled() ? fgColor : Color.GRAY);

		PaintBox box;
		//Pressed down
		if (model.isPressed()) {
			box = pressed;
		//Selected (toggled on)
		} else if (model.isSelected()) {
			box = selected;
		//Not pressed, not selected (toggled off)
		} else {
			box = off;
		}

		int minX = (bordersPainted & LEFT_BORDER) == 0 ? -pad : 0; 
		int maxX = (bordersPainted & RIGHT_BORDER) == 0 ? w-1+pad : w-1;
		int minY = (bordersPainted & TOP_BORDER) == 0 ? -pad : 0;
		int maxY = (bordersPainted & BOTTOM_BORDER) == 0 ? h-1+pad : h-1;
		
		int width = maxX - minX;
		int height = maxY - minY;
		
		//Draw bg
		g2d.setPaint(box.getBg());
		g2d.fillRoundRect(minX, minY, width, height, outerRoundRectSize, outerRoundRectSize);

		//Draw outline
		g2d.setPaint(box.getOutline());
		g2d.drawRoundRect(minX, minY, width, height, outerRoundRectSize,
				outerRoundRectSize);
		
		//Draw highlight
		g2d.setPaint(box.getHighlight());
		g2d.drawRoundRect(minX+1, minY+1, width-2, height-2, innerRoundRectSize,
				innerRoundRectSize);
		
		//Draw shadow if not pressed or selected
		if (!model.isSelected() && !model.isPressed()) {
			if((shadowsNotPainted & RIGHT_BORDER) == 0) {
				g2d.setPaint(rightShadowPaint);
				g2d.fillRect(0, 0, w, h);	
			}
			if((shadowsNotPainted & TOP_BORDER) == 0) {
				g2d.setPaint(topShadowPaint);
				g2d.fillRect(0, 0, w, h);	
			}			
			if((shadowsNotPainted & LEFT_BORDER) == 0) {
				g2d.setPaint(leftShadowPaint);
				g2d.fillRect(0, 0, w, h);	
			}
			if((shadowsNotPainted & BOTTOM_BORDER) == 0) {
				g2d.setPaint(bottomShadowPaint);
				g2d.fillRect(0, 0, w, h);	
			}
			
		}
		
		if (model.isSelected() || model.isPressed()) {
			super.setForeground(selectedForeground);
		} else {
			super.setForeground(unselectedForeground);
		}
		
		g2d.dispose();
		super.paintComponent(g);
	}

	//Many constructors...
    /**
     * Creates a toggle button where properties are taken from the 
     * Action supplied.
     * @param a 	{@link Action}
     */
	public JTabButton(Action a) {
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
	public JTabButton(Icon icon, boolean selected) {
		super(icon, selected);
	}

    /**
     * Creates a toggle button with the specified image, text
     * and initial borders.
     *
     * @param text	The text that the buttons should display
     * @param icon  the image that the button should display
     * @param borders	Initial borders
     */
	public JTabButton(String text, Icon icon, int borders) {
		super(text, icon);
		setBorders(borders);
	}
	
    /**
     * Creates a toggle button with the specified image, text
     * and initial borders and shadows.
     *
     * @param text	The text that the buttons should display
     * @param icon  the image that the button should display
     * @param borders	Initial borders
     * @param noShadows Initial shadow settings - mask showing where shadows are omitted
     */
	public JTabButton(String text, Icon icon, int borders, int noShadows) {
		super(text, icon);
		setBorders(borders);
		this.shadowsNotPainted = noShadows;
	}
	
    /**
     * Creates an initially unselected toggle button
     * with the specified image but no text.
     *
     * @param icon  the image that the button should display
     */
	public JTabButton(Icon icon) {
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
	public JTabButton(String text, boolean selected) {
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
	public JTabButton(String text, Icon icon, boolean selected) {
		super(text, icon, selected);
	}

    /**
     * Creates a toggle button that has the specified text and image,
     * and that is initially unselected.
     *
     * @param text the string displayed on the button
     * @param icon  the image that the button should display
     */
	public JTabButton(String text, Icon icon) {
		super(text, icon);
	}

    /**
     * Creates an unselected toggle button with the specified text.
     *
     * @param text  the string displayed on the toggle button
     */
	public JTabButton(String text) {
		super(text);
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
