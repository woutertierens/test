package org.jpropeller.ui.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Arc2D;

import javax.swing.Icon;

/**
 * An {@link Icon} containing a pie. The pie displays
 * a value from 0 to 1 by filling in dots from 0 to 360 degrees
 * of pie-slice, clockwise from north.
 */
public class Piecon implements Icon {

	private final int size;
	private final Paint fill;
	private final Paint outline;
	private final int arcAngle;
	private final int border = 2;
	private final int dotRadius;

	
	private final static Paint defaultFill = new Color(70, 153, 70);
	private final static Paint defaultOutline = new Color(200, 200, 200);
	
	/**
	 * Create a {@link Piecon} with default fill, stroke and outline
	 * @param size		The icon width and height
	 * @param border	The size of the border round the icon
	 * @param dotRadius	The diameter of the dots
	 * @param value		The value, from 0 to 1
	 */
	public Piecon(int size, int border, int dotRadius, double value) {
		this(size, border, dotRadius, value, defaultFill, defaultOutline);
	}
	
	/**
	 * Create a {@link Piecon}
	 * @param size		The icon width and height
	 * @param border	The size of the border round the icon
	 * @param dotRadius	The diameter of the dots
	 * @param value		The value, from 0 to 1
	 * @param fill		Fill paint 
	 * @param outline 	Outline paint
	 */
	public Piecon(int size, int border, int dotRadius, double value, Paint fill, Paint outline) {
		super();
		this.size = size;
		this.arcAngle = - (int)(value * 360);
		this.fill = fill;
		this.outline = outline;
		this.dotRadius = dotRadius;
	}

	@Override
	public int getIconHeight() {
		return size;
	}

	@Override
	public int getIconWidth() {
		return size;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2 = (Graphics2D)g;
		Object oldAA = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int circleDiameter = size - 2 * (dotRadius + border);
		
		g2.setStroke(new BasicStroke(dotRadius * 2 + 2));
		g2.setPaint(outline);
		g2.drawOval(x + border + dotRadius, y + border + dotRadius, circleDiameter, circleDiameter);

		Arc2D.Double arc = new Arc2D.Double(x, y, size, size,
        		90, arcAngle, Arc2D.PIE);

		Shape clip = g.getClip();
		g2.setPaint(fill);
		g2.setClip(arc);
		g2.setStroke(new BasicStroke(dotRadius * 2));
		g2.drawOval(x + border + dotRadius, y + border + dotRadius, circleDiameter, circleDiameter);
		g2.setClip(clip);
		
		/*
		g2.setPaint(fill);
		for (int i = increments-1; i >=0; i--) {
			if (i < count) {
				double angle = - Math.PI/2 + (Math.PI * 2d / ((double)increments)) * i;
				int dotX = x + (int)(Math.cos(angle) * radius) + offset;
				int dotY = y + (int)(Math.sin(angle) * radius) + offset;
				g2.fillOval(dotX, dotY, dotRadius * 2, dotRadius * 2);
			}
		}*/
		
		/*
		Double arc = new Arc2D.Double(x + border, y + border, width-2*border, height-2*border,
        		90, arcAngle, Arc2D.PIE);

		g2.setPaint(fill);
		g2.fill(arc);

		g2.setStroke(stroke);
		g2.setPaint(outline);
		g2.draw(arc);
		*/
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAA);
	}

}
