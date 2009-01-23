/*
 * Copyright (c) 2000-2005 JGoodies Karsten Lentzsch. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer. 
 *     
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *     
 *  o Neither the name of JGoodies Karsten Lentzsch nor the names of 
 *    its contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */

package org.jpropeller.ui.external;

import java.awt.*;

import javax.swing.*;

/** 
 * A <code>JPanel</code> subclass that has a drop shadow border and 
 * that provides a header with icon, title and tool bar.<p>
 * 
 * This class can be used to replace the <code>JInternalFrame</code>,
 * for use outside of a <code>JDesktopPane</code>. 
 * The <code>SimpleInternalFrame</code> is less flexible but often
 * more usable; it avoids overlapping windows and scales well 
 * up to IDE size.
 * Several customers have reported that they and their clients feel 
 * much better with both the appearance and the UI feel.<p>
 * 
 * The SimpleInternalFrame provides the following bound properties:
 * <i>frameIcon, title, toolBar, content, selected.</i><p>
 * 
 * By default the SimpleInternalFrame is in <i>selected</i> state.
 * If you don't do anything, multiple simple internal frames will
 * be displayed as selected.
 * 
 * @author Karsten Lentzsch
 * @version $Revision: 1.2 $
 * 
 * @see    javax.swing.JInternalFrame
 * @see    javax.swing.JDesktopPane
 */

public class SimpleInternalFrame extends JPanel {
	private static final long serialVersionUID = -8286231616133396583L;
	
	RoundedTitleBorder border;
	
	/**
	 * Create a frame
	 * @param title
	 * 		The title of the frame
	 */
	public SimpleInternalFrame(String title) {
		super(new BorderLayout());
		border = new RoundedTitleBorder(title, getHeaderBackground(), getBackground());
        setBorder(border);
        setFont(getFont().deriveFont(Font.BOLD));
	}
	
	/**
	 * Returns the frame's title text.
	 * 
	 * @return String the current title text
	 */
	public String getTitle() {
		return border.getTitle();
	}

	/**
	 * Sets a new title text.
	 * 
	 * @param newText
	 *            the title text tp be set
	 */
	public void setTitle(String newText) {
		String oldText = getTitle();
		border.setTitle(newText);
		firePropertyChange("title", oldText, newText);
	}

	protected Color getHeaderBackground() {
		// BMW

		Color c = UIManager
		.getColor("SimpleInternalFrame.activeTitleBackground");
		if (c != null)
			return c;
		
		c = UIManager.getColor("InternalFrame.activeTitleGradient");
		if (c == null)
			c = UIManager.getColor("InternalFrame.activeTitleBackground");
		if (c == null) {
			c = UIManager.getColor("InternalFrame.activeTitle");
			// logger.debug("Fell back to InternalFrame.activeTitle " + c);
		}
		if (c == null) {
			// logger.debug("Falling back to light gray");
			c = Color.LIGHT_GRAY;
		}
		// c = c.brighter();

		return c;
	}
	
	/**
	 * Demonstrate the frame
	 * @param args
	 * 		ignored
	 */
    public final static void main(String[] args) {
    	SwingUtilities.invokeLater(new Runnable() {
		
			@Override
			public void run() {
		    	//LooksLaFSetter.setLaF(true);
		    	JFrame frame = new JFrame();
		    	SimpleInternalFrame sif = new SimpleInternalFrame("Title");
		    	JButton button = new JButton("bob");
		    	JPanel panel = new JPanel();
		    	panel.add(button);
		    	sif.add(panel);
		    	frame.setLayout(new FlowLayout());
		    	frame.add(sif);
		    	frame.pack();
		    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    	frame.setVisible(true);
			}
		
		});
    }

}
