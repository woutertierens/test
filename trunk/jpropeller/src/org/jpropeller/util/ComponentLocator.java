package org.jpropeller.util;

import java.awt.Rectangle;

import javax.swing.JComponent;

public interface ComponentLocator {
	/**
	 * Calls component.setBounds to locate the component, somewhere within bounds.
	 * @param component
	 * @param bounds
	 */
	public void locateComponent(JComponent component,Rectangle bounds);
}
