package org.jpropeller.view.table.impl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.table.DefaultTableCellRenderer;

import org.jpropeller.view.info.Colored;

/**
 * Renders exactly as for {@link DefaultTableCellRenderer}, but
 * where an object is {@link Colored}, that {@link Color} is
 * overlaid on the cell at 0.2 alpha
 */
public class ColoredBooleanCellRenderer extends DefaultTableCellRenderer {

	//TODO optimise drawing speed - get rid of the transparency, solid red for true, use image for the dots
	
	private static final int border = 4;
	private static final int extraBorder = 3;

	private Color c = null;
	private Color cl = null;
	private Color cd = null;
	
//	private final static Color f = Color.white;
//	private final static Color fl = Color.white;
//	private final static Color fd = new Color(200, 200, 200);

	private final static Color f = null;
	private final static Color fl = new Color(255, 255, 255, 40);
	private final static Color fd = new Color(0, 0, 0, 40);
	
	private final static Color t =  new Color(255, 100, 100);
	private final static Color tl = new Color(255, 130, 130);
	private final static Color td = new Color(200, 50, 50);
	
	/**
	 * Create a renderer
	 */
	public ColoredBooleanCellRenderer() {
	}
		
	@Override
	public void setValue(Object value) {
		super.setValue(value);

		if (value instanceof Boolean) {
			Boolean b = (Boolean) value;
			c = b ? t : f;
			cl = b ? tl : fl;
			cd = b ? td : fd;
		}
		

		setIcon(null);
		setText(null);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
//		g2.setColor(new Color(255, 255, 255, 200));
//		g2.fillRect(0, 0, getWidth(), getHeight());

		if (c != null) {
			g2.setColor(c);
			g2.fillRect(0, border, getWidth(), getHeight()-2*border);
		}
		
		if (cd != null) {
			g2.setColor(cd);
			g2.drawLine(getWidth()-1, border+extraBorder, getWidth()-1, getHeight() - border-extraBorder);
		}
		
		if (cl != null) {
			g2.setColor(cl);
			g2.drawLine(getWidth()-2, border+extraBorder, getWidth()-2, getHeight() - border-extraBorder);
		}
	}
}
