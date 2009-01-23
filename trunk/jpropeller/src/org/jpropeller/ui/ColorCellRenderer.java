package org.jpropeller.ui;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * A simple {@link TableCellRenderer} for {@link Color}s, displays the color as a
 * {@link ColorSwatchIcon} in a label.
 * @author shingoki
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
	 * @param width
	 * 		Width of color swatch in display 
	 * @param height 
	 * 		Height of color swatch in display
	 * @param outlineColor
	 * 		Outline color of color swatch in display 
	 */
	public ColorCellRenderer(int width, int height, Color outlineColor) {
		this();
		icon.setWidth(width);
		icon.setHeight(height);
		icon.setOutlineColor(outlineColor);
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof Color) {
			Color color = (Color) value;
			icon.setColor(color);
			setIcon(icon);
		} else {
			setIcon(null);	
		}
	}
}