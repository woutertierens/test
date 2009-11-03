package org.jpropeller.view.table.impl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.table.DefaultTableCellRenderer;

import org.jpropeller.util.GeneralUtils;
import org.jpropeller.view.info.Colored;

/**
 * Renders exactly as for {@link DefaultTableCellRenderer}, but
 * where an object is {@link Colored}, that {@link Color} is
 * overlaid on the cell at 0.2 alpha
 */
public class ColoredBackgroundCellRenderer extends DefaultTableCellRenderer {

	private Color color = null;
	
	@Override
	public void setValue(Object value) {

		if (value instanceof Colored) {
			Colored colored = (Colored) value;
			double alpha = 0.2;
			color = GeneralUtils.transparentColor(colored.color().get(), alpha);
		} else {
			color = null;
		}
		
		super.setValue(value);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (color != null) {
			Graphics2D g2 = (Graphics2D)g;
			g2.setColor(color);
			g2.fillRect(0, 0, getWidth(), getHeight());
		}
	}
}
