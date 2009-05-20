package org.jpropeller.sheet.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Stroke;

import javax.swing.border.AbstractBorder;

public class UnderlineBorder extends AbstractBorder {
	
    private int left, right, top, bottom;
    private int sideInset = 0;
    private int bottomInset = 2;
    private Stroke stroke = new BasicStroke(2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1, new float[]{6, 4}, 0);
    
    private static Color color = new Color(199, 199, 199);
    
    /**
     * Creates a {@link UnderlineBorder}
     */
    public UnderlineBorder()   {
        this.top = 0; 
        this.right = 0;
        this.bottom = 4;
        this.left = 0;
    }

    /**
     * Does no drawing by default.
     */
    public void paintBorder(Component c, Graphics graphics, int x, int y, int width, int height) {
    	Graphics2D g = (Graphics2D)graphics;
    	
    	Color oc = g.getColor();
    	Stroke os = g.getStroke();
    	
    	int dy = y + height - bottomInset;
    	
    	g.setColor(color);
    	g.setStroke(stroke);
    	g.drawLine(x + sideInset, dy, x + width - sideInset, dy);
    	
    	g.setStroke(os);
    	g.setColor(oc);
    }

    /**
     * Returns the insets of the border.
     * @param c the component for which this border insets value applies
     */
    public Insets getBorderInsets(Component c)       {
        return getBorderInsets();
    }

    /** 
     * Reinitialize the insets parameter with this Border's current Insets. 
     * @param c the component for which this border insets value applies
     * @param insets the object to be reinitialized
     */
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = left;
        insets.top = top;
        insets.right = right;
        insets.bottom = bottom;
        return insets;
    }

    /**
     * Returns the insets of the border.
     */
    public Insets getBorderInsets() {
        return new Insets(top, left, bottom, right);
    }

    /**
     * Returns whether or not the border is opaque.
     * Returns false by default.
     */
    public boolean isBorderOpaque() { return false; }

	
}
