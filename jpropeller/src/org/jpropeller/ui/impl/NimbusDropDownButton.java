package org.jpropeller.ui.impl;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.jpropeller.ui.IconFactory.IconSize;
import org.jpropeller.view.Views;

/**
 * Button designed to be used as a drop down combo box replacement.
 * Note that this uses the nimbus LaF when available, otherwise
 * just looks like a standard toggle button
 */
public class NimbusDropDownButton extends JToggleButton {

	private final int dividerSize = 20;

	private final PainTer disabled = new PainTer(UIManager.get("ComboBox[Disabled].backgroundPainter"));
	private final PainTer enabled = new PainTer(UIManager.get("ComboBox[Enabled].backgroundPainter"));	
	private final PainTer focusedMouseOver = new PainTer(UIManager.get("ComboBox[Focused+MouseOver].backgroundPainter"));
	private final PainTer focusedPressed = new PainTer(UIManager.get("ComboBox[Focused+Pressed].backgroundPainter"));
	private final PainTer focused = new PainTer(UIManager.get("ComboBox[Focused].backgroundPainter"));	
	private final PainTer mouseOver = new PainTer(UIManager.get("ComboBox[MouseOver].backgroundPainter"));
//	private final PainTer pressed = new PainTer(UIManager.get("ComboBox[Pressed].backgroundPainter"));

	//FIXME use proper arrow painters
//	PainTer arrowEnabled = new PainTer(UIManager.get("ComboBox:\"ComboBox.arrowButton\"[Enabled].foregroundPainter"));
//	PainTer arrowDisabled = new PainTer(UIManager.get("ComboBox:\"ComboBox.arrowButton\"[Disabled].foregroundPainter"));

	//FIXME find out what this is for
	//PainTer enabledSelected = new PainTer(UIManager.get("ComboBox[Enabled+Selected].backgroundPainter"));	

	private final static Icon dropdownSelected = Views.getIconFactory().getIcon(IconSize.SMALL, "jpropeller", "dropdown-selected");
	private final static Icon dropdownUnselected = Views.getIconFactory().getIcon(IconSize.SMALL, "jpropeller", "dropdown-unselected");
	private final static Icon dropdownDisabled = Views.getIconFactory().getIcon(IconSize.SMALL, "jpropeller", "dropdown-disabled");
	
	private boolean isMouseOver = false;
	private boolean isMouseDown = false;
	
	{
		setFocusable(true);
		setContentAreaFilled(false);
		setBorderPainted(false);

		Insets insets = (Insets)(getInsets().clone());
		insets.right += dividerSize;
		
		setBorder(new EmptyBorder(insets));
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				isMouseOver = true;
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				isMouseOver = false;
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					isMouseDown = true;
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					isMouseDown = false;
				}
			}
		});
	}

	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();

		PainTer painter = disabled;
		//Painter<?> arrowPainter = arrowDisabled;
		if (isEnabled()) {
			//arrowPainter = arrowEnabled;
			if (isFocusOwner() && isFocusPainted()) {
				if (isSelected() || (isMouseOver && isMouseDown)) {
					painter = focusedPressed;
				} else if (isMouseOver){
					painter = focusedMouseOver;
				} else {
					painter = focused;
				}				
			} else {
				if (isSelected() || (isMouseOver && isMouseDown)) {
					//TODO we lose focus when showing a popup (because it is technically a different component),
					//but we are still essentially focussed from the
					//user point of view. Can we deal with this better? Is there a way for the
					//popup to be displayed without the control having focus? (seems unlikely)
					painter = focusedPressed;
				} else if (isMouseOver){
					painter = mouseOver;
				} else {
					painter = enabled;
				}
			}
		}
		
		painter.paint(g2d, getWidth(), getHeight());
		
		/*
		arrowPainter.paint(g2d, null, getWidth(), getHeight());
		*/
		
		//draw arrow
		Icon icon = model.isEnabled() ? (isSelected() || (isMouseOver && isMouseDown) ? dropdownSelected : dropdownUnselected) : dropdownDisabled;
		icon.paintIcon(this, g2d, getWidth() - dividerSize + (dividerSize - icon.getIconWidth())/2, (getHeight() - icon.getIconHeight())/2 + 1);
		
		g2d.dispose();
		
		super.paintComponent(g);
	}

	/**
	 * True if nimbus is available, false otherwise.
	 * @return Nimbus availability
	 */
	public final static boolean isNimbusAvailable() {
		return UIManager.get("ComboBox:\"ComboBox.arrowButton\"[Enabled].foregroundPainter") != null;
	}
	
	//Many constructors...
	
    /**
     * Creates a toggle button
     */
	public NimbusDropDownButton() {
		super();
	}
	
    /**
     * Creates a toggle button where properties are taken from the 
     * Action supplied.
     * @param a 	{@link Action}
     */
	public NimbusDropDownButton(Action a) {
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
	public NimbusDropDownButton(Icon icon, boolean selected) {
		super(icon, selected);
	}

    /**
     * Creates an initially unselected toggle button
     * with the specified image but no text.
     *
     * @param icon  the image that the button should display
     */
	public NimbusDropDownButton(Icon icon) {
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
	public NimbusDropDownButton(String text, boolean selected) {
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
	public NimbusDropDownButton(String text, Icon icon, boolean selected) {
		super(text, icon, selected);
	}

    /**
     * Creates a toggle button that has the specified text and image,
     * and that is initially unselected.
     *
     * @param text the string displayed on the button
     * @param icon  the image that the button should display
     */
	public NimbusDropDownButton(String text, Icon icon) {
		super(text, icon);
	}

    /**
     * Creates an unselected toggle button with the specified text.
     *
     * @param text  the string displayed on the toggle button
     */
	public NimbusDropDownButton(String text) {
		super(text);
	}
}
