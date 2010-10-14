package org.jpropeller.ui.impl;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.RenderingHints;

import javax.swing.Action;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.jpropeller.ui.IconFactory.IconSize;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.view.Views;

/**
 * Button designed to be used as a tab.
 */
public class JDropDownButton extends JToggleButton {

	static {
		UIManager.put("itis.background", GeneralUtils.scaleColor(UIManager.getColor("Button.background"), 1));
		UIManager.put("itis.background.selected", new Color(190, 200, 220));
	}
	
	private Color bgColor;
	private Color outlineColor;
	private Color fgColor;
	private Color selColor;
	private int highlightAlpha;
	
	private int outerRoundRectSize = 10;
	private int innerRoundRectSize = 8;
	private int shadeWidth = 8;
	
	private boolean pressDown = false;

	PaintBox pressed;
	PaintBox selected;
	PaintBox off;
	Paint leftShadowPaint, rightShadowPaint, topShadowPaint, bottomShadowPaint;
	Paint blendPaint;
	Paint dividerUnselected;
	Paint dividerSelected;
	private int dividerSize = 24;
	
	private final static Icon dropdownSelected = Views.getIconFactory().getIcon(IconSize.SMALL, "jpropeller", "dropdown-selected");
	private final static Icon dropdownUnselected = Views.getIconFactory().getIcon(IconSize.SMALL, "jpropeller", "dropdown-unselected");
	
    /**
     * Creates an initially unselected toggle button
     * without setting the text or image.
     */
	public JDropDownButton() {
		super();
		setFocusable(true);
		setContentAreaFilled(false);
		setBorderPainted(false);

		
		Insets insets = (Insets)(getInsets().clone());
		insets.right += dividerSize;
		
		//System.out.println("Insets: " + );

		setBorder(new EmptyBorder(insets));
		
		outlineColor = new Color(100,100,100);
		
		highlightAlpha = 100;
		
		selColor = UIManager.getColor("itis.background.selected");
		
	}
	
	private void updateBoxes(int w, int h) {
		bgColor = getBackground();
		fgColor = getForeground();
		off = new PaintBox(
				new GradientPaint(		//bg
						0, 1, 
						bgColor, 
						0, h-3, 
						GeneralUtils.scaleColor(bgColor, 0.9), 
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
							GeneralUtils.scaleColor(bgColor, 0.9), 
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
							GeneralUtils.scaleColor(selColor, 0.9), 
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
							GeneralUtils.scaleColor(bgColor, 1.2d),
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
							GeneralUtils.scaleColor(selColor, 1.1),
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
				new Color(0,0,0,50),
				0, shadeWidth,
				new Color(0,0,0,0),
				false);

		bottomShadowPaint = new GradientPaint(
				0, h-shadeWidth, 
				new Color(0,0,0,0), 
				0, h,
				new Color(0,0,0,50), 
				false);
		
		rightShadowPaint = new GradientPaint(
				w - shadeWidth, 0, 
				new Color(0,0,0,0), 
				w, 0,
				new Color(0,0,0,50), 
				false);
		
		leftShadowPaint = new GradientPaint(
				0, 0,
				new Color(0,0,0,50),
				shadeWidth, 0,
				new Color(0,0,0,0),  
				false);
		
		blendPaint = new GradientPaint(
				w - shadeWidth, 0, 
				new Color(selColor.getRed(), selColor.getGreen(), selColor.getBlue(), 0), 
				w, 0,
				selColor, 
				false);

		dividerSelected = new Color(0f, 0.2f, 0.4f, 0.5f);
		dividerUnselected = new Color(0f, 0.2f, 0.4f, 0.15f);
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

		int minX = 0; 
		int maxX = w-1;
		int minY = 0;
		int maxY = h-1;
		
		int width = maxX - minX;
		int height = maxY - minY;
		
		//Draw bg
		g2d.setPaint(box.getBg());
		g2d.fillRoundRect(minX, minY, width, height, outerRoundRectSize, outerRoundRectSize);

		//Draw divider area
		if (model.isSelected()) {
			g2d.setPaint(dividerSelected);
		} else {
			g2d.setPaint(dividerUnselected);			
		}
		g2d.fillRoundRect(maxX - dividerSize, minY, dividerSize, height, outerRoundRectSize, outerRoundRectSize);

		//Draw outline
		g2d.setPaint(box.getOutline());
		g2d.drawRoundRect(minX, minY, width, height, outerRoundRectSize,
				outerRoundRectSize);

		//Draw highlight
		g2d.setPaint(box.getHighlight());
		g2d.drawRoundRect(minX+1, minY+1, width-2, height-2, innerRoundRectSize,
				innerRoundRectSize);
		
		//draw arrow
		Icon icon = model.isSelected() ? dropdownSelected : dropdownUnselected;
		icon.paintIcon(this, g2d, maxX - dividerSize + (dividerSize - icon.getIconWidth())/2, minY + (height - icon.getIconHeight())/2);
		
		g2d.dispose();
		super.paintComponent(g);
	}

	
	//Many constructors...
    /**
     * Creates a toggle button where properties are taken from the 
     * Action supplied.
     * @param a 	{@link Action}
     */
	public JDropDownButton(Action a) {
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
	public JDropDownButton(Icon icon, boolean selected) {
		super(icon, selected);
	}

    /**
     * Creates an initially unselected toggle button
     * with the specified image but no text.
     *
     * @param icon  the image that the button should display
     */
	public JDropDownButton(Icon icon) {
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
	public JDropDownButton(String text, boolean selected) {
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
	public JDropDownButton(String text, Icon icon, boolean selected) {
		super(text, icon, selected);
	}

    /**
     * Creates a toggle button that has the specified text and image,
     * and that is initially unselected.
     *
     * @param text the string displayed on the button
     * @param icon  the image that the button should display
     */
	public JDropDownButton(String text, Icon icon) {
		super(text, icon);
	}

    /**
     * Creates an unselected toggle button with the specified text.
     *
     * @param text  the string displayed on the toggle button
     */
	public JDropDownButton(String text) {
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
