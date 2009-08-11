/*
 * Copyright 2008 Sun Microsystems, Inc.  All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.jpropeller.ui.external;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.border.Border;

/**
 *	An attractive rounded border
 */
public class RoundedBorder implements Border {    
    private int cornerRadius;
	private Color colorOverride;
    
    /**
     * Create a default border with corner radius 10
     */
    public RoundedBorder() {
        this(10, null);
    }
    
    /**
     * Create a border
     * @param cornerRadius
     * 		The radius of the rounded corners
     * @param color The override color.
     */
    public RoundedBorder(int cornerRadius, Color color) {
        this.cornerRadius = cornerRadius;
        this.colorOverride = color;
    }

    /**
     * Create a border
     * @param cornerRadius
     * 		The radius of the rounded corners
     */
    public RoundedBorder(int cornerRadius) {
		this(cornerRadius, null);
	}

	public Insets getBorderInsets(Component c) {
        return getBorderInsets(c, new Insets(0,0,0,0));
    }

    /**
     * Adjust a provided {@link Insets} instance to match
     * this border's inserts, and return it
     * @param c the component for which this border insets value applies
     * @param insets The insets to adjust and return
     * @return
     * 		the adjust insets
     */
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.top = insets.bottom = cornerRadius/2; 
        insets.left = insets.right = 1;
        return insets;
    }

    public boolean isBorderOpaque() {
        return false;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
        Color color;
        if(colorOverride == null) {
        	 color = Utilities.deriveColorHSB(c.getBackground(), 0, 0, -.3f);
        } else {
        	color = colorOverride;
        }
        
        g2.setColor(Utilities.deriveColorAlpha(color, 40));
        g2.drawRoundRect(x, y + 2, width - 1, height - 3, cornerRadius, cornerRadius);
        g2.setColor(Utilities.deriveColorAlpha(color, 90));        
        g2.drawRoundRect(x, y + 1, width - 1, height - 2, cornerRadius, cornerRadius); 
        g2.setColor(Utilities.deriveColorAlpha(color, 255));        
        g2.drawRoundRect(x, y, width - 1, height - 1, cornerRadius, cornerRadius);

        g2.dispose();            
    }
}
