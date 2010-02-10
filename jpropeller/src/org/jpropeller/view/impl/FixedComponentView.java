/*
 *  $Id: org.eclipse.jdt.ui.prefs,v 1.1 2008/03/24 11:20:15 shingoki Exp $
 *
 *  Copyright (c) 2008 shingoki
 *
 *  This file is part of jpropeller, see http://jpropeller.sourceforge.net
 *
 *    jpropeller is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    jpropeller is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with jpropeller; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jpropeller.view.impl;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.jpropeller.view.CompletionException;
import org.jpropeller.view.JView;

/**
 * A {@link JView} that displays the same fixed component
 * at all times. Has no actual model.
 * In order to be compliant with {@link JView} contract, the
 * component should not "update" in any way - if updates are
 * required, implement a new {@link JView}.
 * This is useful for example for displaying a "No valid selection"
 * label for null/unknown type data.
 */
public class FixedComponentView implements JView  {
	
	private final JComponent component;

	/**
	 * Create a {@link FixedComponentView}
	 * @param component		The component to display.
	 */
	public FixedComponentView(final JComponent component) {
		this.component = component;
	}

	/**
	 * Create a {@link FixedComponentView} with a large, transparent,
	 * centered label displaying a message.
	 * @param message		The message string to display
	 */
	public FixedComponentView(final String message) {
		this(makeLabel(message));
	}
	
	private static JLabel makeLabel(final String message) {
		JLabel lbl = new JLabel(message);
		lbl.setOpaque(false);
		Font font = lbl.getFont();
		lbl.setForeground(Color.DARK_GRAY);
		lbl.setFont(new Font(font.getFontName(), Font.ITALIC, 24));
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		return lbl;
	}
	
	@Override
	public Format format() {
		return Format.LARGE;
	}

	@Override
	public JComponent getComponent() {
		return component;
	}

	@Override
	public boolean selfNaming() {
		return false;
	}

	@Override
	public void cancel() {
		
	}

	@Override
	public void commit() throws CompletionException {
	}

	@Override
	public boolean isEditing() {
		return false;
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public void update() {
	}
	
}