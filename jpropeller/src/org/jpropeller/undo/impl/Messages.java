package org.jpropeller.undo.impl;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Messages for this package
 */
public class Messages {
	private static final String BUNDLE_NAME = "org.jpropeller.undo.impl.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	/**
	 * Get a string resource
	 * @param key
	 * 		The resource key
	 * @return
	 * 		The string resource
	 */
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
	
	/**
	 * Get integer resource for a key
	 * @param key
	 * 		resource key
	 * @return
	 * 		resource integer
	 */
	public static int getInt(String key) {
		try {
			String s = RESOURCE_BUNDLE.getString(key);
			try {
				return Integer.parseInt(s);
			} catch (NumberFormatException nfe) {
				throw new RuntimeException("Invalid resource for key '" + key + "', expected an integer but got '" + s + "'");
			}
		} catch (MissingResourceException e) {
			throw new RuntimeException("Missing resource for key '" + key + "'");
		}
	}

}
