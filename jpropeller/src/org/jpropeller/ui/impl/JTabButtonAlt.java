package org.jpropeller.ui.impl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;

import javax.swing.Action;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.jpropeller.ui.IconFactory.IconSize;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.view.Views;

/**
 * Button designed to be used as a tab.
 */
public class JTabButtonAlt extends JToggleButton {

	static {
		UIManager.put("itis.background", GeneralUtils.scaleColor(UIManager.getColor("Button.background"), 1));
		UIManager.put("itis.background.selected", new Color(190, 200, 220));
	}
	
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
	 * Button that can be separate from other components, all borders rounded..
	 */
	public static final int INDEPENDENT_BUTTON = RIGHT_BORDER | TOP_BORDER | LEFT_BORDER | BOTTOM_BORDER;
	
	
	private int bordersPainted = LEFT_BUTTON;
	
	private Color bgColor;
	private Color outlineColor;
	private Color fgColor;
	private Color selColor;
	private int highlightAlpha;
	
	private int outerRoundRectSize = 10;
	private int pad = 16;
	private int shadeWidth = 8;
	
	private boolean pressDown = false;

	PaintBox pressed;
	PaintBox selected;
	PaintBox off;
	Paint leftShadowPaint, rightShadowPaint, topShadowPaint, bottomShadowPaint;
	Paint blendPaint;
	
	{
		setFocusable(false);
		setContentAreaFilled(false);
		setBorderPainted(false);
		
		outlineColor = new Color(100,100,100);
		
		highlightAlpha = 100;
		
		selColor = UIManager.getColor("itis.background.selected");
	}

    /**
     * Creates an initially unselected toggle button
     * without setting the text or image.
     */
	public JTabButtonAlt() {
		super();
	}
	
	/**
	 * Create a {@link JTabButtonAlt} for use as a 
	 * normal {@link JToggleButton}
	 * @param name		To display in button
	 * @return			new {@link JTabButtonAlt}
	 */
	public static JTabButtonAlt createToggle(String name) {
		JTabButtonAlt button = new JTabButtonAlt(name);
		button.setBorders(INDEPENDENT_BUTTON);
		button.pressDown = true;
		return button;
	}

	private void updateBoxes(int w, int h) {
		bgColor = getBackground();
		fgColor = getForeground();
		off = new PaintBox(
				bgColor,
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
					bgColor,
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
		
		if (pressDown) {
			pressed = new PaintBox(
					GeneralUtils.scaleColor(bgColor, 1.2d),
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
					GeneralUtils.scaleColor(selColor, 1.1),
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

	}

	/**
	 * Sets which edges of the button are painted.
	 * @param borderMask	A bitmask of {@link #BOTTOM_BORDER}, {@link #TOP_BORDER}, 
	 * 						{@link #RIGHT_BORDER} and {@link #LEFT_BORDER}.
	 */
	public void setBorders(int borderMask) {
		this.bordersPainted = borderMask;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		
		int highlightAlpha1 = 255;
		int highlightAlpha2 = 40;
		int highlightSize = 5;
		Color shadowColor = new Color(0f,0f,0f, 0.2f);
		int shadowOffset = 3;
		int border = shadowOffset + 1;

		
		Graphics2D g2d = (Graphics2D) g.create();
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		updateBoxes(getWidth(), getHeight());

		int h = getHeight();
		int w = getWidth();
		ButtonModel model = getModel();

		GradientPaint highlight1 = new GradientPaint(				//highlight1
				0, 0, 
				new Color(255,255,255,highlightAlpha1), 
				0, highlightSize, 
				new Color(255,255,255,0), 
				false);
		
		GradientPaint highlight2 = new GradientPaint(				//highlight2
				0, h - border - highlightSize, 
				new Color(0,0,0,0), 
				0, h - border, 
				new Color(0,0,0,highlightAlpha2), 
				false);

		
		
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
		int minY = (bordersPainted & TOP_BORDER) == 0 ? -pad : 0;

		int maxX = (bordersPainted & RIGHT_BORDER) == 0 ? w-1-border+pad : w-1-border;
		int maxY = (bordersPainted & BOTTOM_BORDER) == 0 ? h-1-border+pad : h-1-border;
		
		int width = maxX - minX+1;
		int height = maxY - minY+1;
		
//		System.out.println(minX + "," + minY + " @ " + width + "x" + height);

		//Draw shadow
		g2d.setPaint(shadowColor);
		g2d.fillRoundRect(minX+shadowOffset , minY+shadowOffset, width, height, outerRoundRectSize, outerRoundRectSize);

		//Draw bg
		g2d.setPaint(box.getBg());
		g2d.fillRoundRect(minX, minY, width, height, outerRoundRectSize, outerRoundRectSize);

		//Draw outline
//		g2d.setPaint(box.getOutline());
//		g2d.drawRoundRect(minX, minY, width, height, outerRoundRectSize,
//				outerRoundRectSize);
		
		//Draw highlights
		g2d.setPaint(highlight1);
		g2d.drawRoundRect(minX, minY, width, height, outerRoundRectSize, outerRoundRectSize);
		g2d.setPaint(highlight2);
		g2d.drawRoundRect(minX, minY, width, height, outerRoundRectSize, outerRoundRectSize);
		
		//Draw shadow if not pressed or selected
		if (!model.isSelected() && !model.isPressed()) {
			if((bordersPainted & RIGHT_BORDER) == 0) {
				g2d.setPaint(rightShadowPaint);
				g2d.fillRect(0, 0, w, h);	
			}
			if((bordersPainted & TOP_BORDER) == 0) {
				g2d.setPaint(topShadowPaint);
				g2d.fillRect(0, 0, w, h);	
			}			
			if((bordersPainted & LEFT_BORDER) == 0) {
				g2d.setPaint(leftShadowPaint);
				g2d.fillRect(0, 0, w, h);	
			}
			if((bordersPainted & BOTTOM_BORDER) == 0) {
				g2d.setPaint(bottomShadowPaint);
				g2d.fillRect(0, 0, w, h);	
			}
			
		}
		
		g2d.dispose();
		super.paintComponent(g);
	}


	
	/**
	 * Demonstrate the button
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
		frame.setLayout(new FlowLayout());
		frame.add(JTabButtonAlt.getButtonsPanel());
		frame.getContentPane().setBackground(Color.WHITE);
		frame.setBackground(Color.WHITE);
		frame.setSize(700, 85);
		frame.setVisible(true);
	}
	
	/**
	 * Make a panel showing some buttons
	 * 
	 * @return {@link JPanel} with buttons
	 */
	public static JPanel getButtonsPanel() {
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);

		JTabButtonAlt standardButton = new JTabButtonAlt("Standard Button");
		standardButton.setPreferredSize(new Dimension(130, 28));

		JTabButtonAlt rollOverButton = new JTabButtonAlt("RollOver Button");
		rollOverButton.setPreferredSize(new Dimension(130, 28));

		JTabButtonAlt disabledButton = new JTabButtonAlt("Disable Button");
		disabledButton.setPreferredSize(new Dimension(130, 28));
		disabledButton.setEnabled(false);

		JTabButtonAlt pressedButton = new JTabButtonAlt("Pressed Button");
		pressedButton.setPreferredSize(new Dimension(130, 28));

		JTabButtonAlt labelButton = new JTabButtonAlt("");
		labelButton.add(new JLabel("<html>line1<br />line2</html>", Views.getIconFactory()
				.getIcon(IconSize.SMALL, "actions", "list-add"),
				SwingConstants.LEFT));
		// labelButton.setPreferredSize(new Dimension(130, 28));

		panel.add(standardButton);
		panel.add(rollOverButton);
		panel.add(disabledButton);
		panel.add(pressedButton);
		panel.add(labelButton);
		return panel;

	}
	
	//Many constructors...
    /**
     * Creates a toggle button where properties are taken from the 
     * Action supplied.
     * @param a 	{@link Action}
     */
	public JTabButtonAlt(Action a) {
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
	public JTabButtonAlt(Icon icon, boolean selected) {
		super(icon, selected);
	}

    /**
     * Creates an initially unselected toggle button
     * with the specified image but no text.
     *
     * @param icon  the image that the button should display
     */
	public JTabButtonAlt(Icon icon) {
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
	public JTabButtonAlt(String text, boolean selected) {
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
	public JTabButtonAlt(String text, Icon icon, boolean selected) {
		super(text, icon, selected);
	}

    /**
     * Creates a toggle button that has the specified text and image,
     * and that is initially unselected.
     *
     * @param text the string displayed on the button
     * @param icon  the image that the button should display
     */
	public JTabButtonAlt(String text, Icon icon) {
		super(text, icon);
	}

    /**
     * Creates an unselected toggle button with the specified text.
     *
     * @param text  the string displayed on the toggle button
     */
	public JTabButtonAlt(String text) {
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
