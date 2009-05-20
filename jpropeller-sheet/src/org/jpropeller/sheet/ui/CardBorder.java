package org.jpropeller.sheet.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.border.AbstractBorder;

public class CardBorder extends AbstractBorder {
	
    private int left, right, top, bottom;

    private static Image image;
    private static Image[][] tiles;
    
    private final static int size = 34;
    private final static int padding = 30;
    
    /**
     * Creates a {@link CardBorder}
     */
    public CardBorder()   {
        this.top = padding; 
        this.right = padding;
        this.bottom = padding;
        this.left = padding;
        
        if (image == null) {
        	loadImages();
        }
    }

	private void loadImages() {
		image = new ImageIcon(this.getClass().getResource("CardBorder.png")).getImage();
		tiles = new Image[3][3];
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				int xp = 0;
				if (x == 1) {
					xp = size;
				} else if (x == 2) {
					xp = image.getWidth(null) - size;
				}
				
				int yp = 0;
				if (y == 1) {
					yp = size;
				} else if (y == 2) {
					yp = image.getHeight(null) - size;
				}
				
				System.out.println("x " + x + ", y " + y + ", xp "+ xp + ", yp " + yp);
				
				tiles[x][y] = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = (Graphics2D)tiles[x][y].getGraphics();
				
				g.drawImage(image, 0, 0, size, size, xp, yp, xp+size, yp+size, null);
			}
		}
	}

    /**
     * Does no drawing by default.
     */
    public void paintBorder(Component c, Graphics graphics, int x, int y, int width, int height) {
    	Graphics2D g = (Graphics2D)graphics;
    	
    	//g.drawImage(image, x, y, null);
    	
    	//corners
    	g.drawImage(tiles[0][0], x, y, null);
    	g.drawImage(tiles[2][0], x + width - size, y, null);
    	g.drawImage(tiles[0][2], x, y + height - size, null);
    	g.drawImage(tiles[2][2], x + width - size, y + height - size, null);
    	
    	//top, bottom, left, right sides
    	g.drawImage(tiles[1][0], x + size, 0, x + width - size, size, 0, 0, size, size, null);
    	g.drawImage(tiles[1][2], x + size, y + height - size, x + width - size, y + height, 0, 0, size, size, null);
    	g.drawImage(tiles[0][1], x, y + size, x + size, y + height - size, 0, 0, size, size, null);
    	g.drawImage(tiles[2][1], x + width - size, y + size, x + width, y + height - size, 0, 0, size, size, null);
    	
    	//center
    	g.drawImage(tiles[1][1], x + size, y + size, x + width - size, y + height - size, 0, 0, size, size, null);

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
