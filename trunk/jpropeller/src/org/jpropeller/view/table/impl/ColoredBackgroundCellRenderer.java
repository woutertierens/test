package org.jpropeller.view.table.impl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.table.DefaultTableCellRenderer;

import org.jpropeller.transformer.Transformer;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.view.info.Colored;

/**
 * Renders exactly as for {@link DefaultTableCellRenderer}, but
 * where an object is {@link Colored}, that {@link Color} is
 * overlaid on the cell at 0.2 alpha
 */
public class ColoredBackgroundCellRenderer extends DefaultTableCellRenderer {

	private Color color = null;
	private Transformer<Object, Color> colorer;
	
	/**
	 * Create a renderer using {@link Color} from {@link Colored}
	 * objects only
	 */
	public ColoredBackgroundCellRenderer() {
		this(null);
	}
	
	/**
	 * Create a renderer using a {@link Transformer} to convert objects
	 * to colors
	 * @param colorer		{@link Transformer} from objects to colors
	 */
	public ColoredBackgroundCellRenderer(Transformer<Object, Color> colorer) {
		this.colorer = colorer;
	}
	
	@Override
	public void setValue(Object value) {

		double alpha = 0.2;

		if (colorer == null) {
			if (value instanceof Colored) {
				Colored colored = (Colored) value;
				color = GeneralUtils.transparentColor(colored.color().get(), alpha);
			} else {
				color = null;
			}
		} else {
			color = colorer.transform(value);
			color = GeneralUtils.transparentColor(color, alpha);
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
