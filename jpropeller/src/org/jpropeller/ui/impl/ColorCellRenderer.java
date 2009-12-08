package org.jpropeller.ui.impl;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * A simple {@link TableCellRenderer} for {@link Color}s, displays the color as a
 * {@link ColorSwatchIcon} in a label.
 */
public class ColorCellRenderer extends DefaultTableCellRenderer {

	private final ColorSwatchIcon icon = new ColorSwatchIcon();
	
	/**
	 * Create a renderer
	 */
	public ColorCellRenderer() {
		super();
		setHorizontalAlignment(JLabel.CENTER);
	}

	/**
	 * Create a renderer
	 * @param opaque Whether the renderer should render a background or not.
	 * @param width
	 * 		Width of color swatch in display 
	 * @param height 
	 * 		Height of color swatch in display
	 * @param outlineColor
	 * 		Outline color of color swatch in display 
	 */
	public ColorCellRenderer(boolean opaque, int width, int height, Color outlineColor) {
		this();
		setOpaque(opaque);
		icon.setWidth(width);
		icon.setHeight(height);
		icon.setOutline(outlineColor);
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof Color) {
			Color color = (Color) value;
			icon.setFill(color);
			setIcon(icon);
		} else {
			setIcon(null);	
		}
	}
}