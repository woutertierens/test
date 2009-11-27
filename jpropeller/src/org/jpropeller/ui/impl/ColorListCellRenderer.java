package org.jpropeller.ui.impl;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * A simple {@link ListCellRenderer} for {@link Color}s, displays the color as a
 * {@link ColorSwatchIcon} in a label.
 */
public class ColorListCellRenderer extends DefaultListCellRenderer {

	private final ColorSwatchIcon icon = new ColorSwatchIcon();
	
	/**
	 * Create a renderer
	 */
	public ColorListCellRenderer() {
		super();
		setHorizontalAlignment(JLabel.CENTER);
	}

	/**
	 * Create a renderer
	 * @param width
	 * 		Width of color swatch in display 
	 * @param height 
	 * 		Height of color swatch in display
	 * @param outlineColor
	 * 		Outline color of color swatch in display 
	 */
	public ColorListCellRenderer(int width, int height, Color outlineColor) {
		this();
		icon.setWidth(width);
		icon.setHeight(height);
		icon.setOutline(outlineColor);
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected,
				cellHasFocus);
		if (value instanceof Color) {
			Color color = (Color) value;
			icon.setFill(color);
			setIcon(icon);
			setText("");
		} else {
			setIcon(null);
			setText(value.toString());
		}
		return this;
	}
	
}