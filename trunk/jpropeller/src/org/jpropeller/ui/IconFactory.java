package org.jpropeller.ui;

import java.awt.Image;

import javax.swing.Icon;

/**
 * Factory creating {@link Icon} and {@link Image} instances
 * according to a size, category and name. This ties in well
 * with standard icon libraries using the standards at
 * http://standards.freedesktop.org/icon-naming-spec/icon-naming-spec-latest.html
 */
public interface IconFactory {

	/**
	 * Available standard icon sizes
	 */
	public enum IconSize {
		/**
		 * Small icon size (e.g. 16x16)
		 */
		SMALL,
		
		/**
		 * Medium icon size (e.g. 22x22)
		 */
		MEDIUM,
		
		/**
		 * Large icon size (e.g. 32x32)
		 */
		LARGE;
	}
	/**
	 * Get an {@link Icon}
	 * 
	 * @param size		The desired size
	 * @param category	The desired icon category, e.g. "actions", "status"
	 * @param name		The desired name
	 * @return			The {@link Icon}
	 */
	public Icon getIcon(IconSize size, String category, String name);

	/**
	 * Get an {@link Image} for an icon
	 * 
	 * @param size		The desired size
	 * @param category	The desired icon category, e.g. "actions", "status"
	 * @param name		The desired name
	 * @return			The {@link Icon}
	 */	
	public Image getImage(IconSize size, String category, String name);
	
}
