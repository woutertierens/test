package org.jpropeller.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;

/**
 * Simple general purpose utility methods
 * These are not specific to jpropeller itself
 */
public class GeneralUtils {

	/**
	 * Enable the Nimbus LaF if available
	 */
	public static void enableNimbus() {
    	try {            
            UIManager.setLookAndFeel(
                    "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
        }
	}
	
	/**
	 * Enable console logging.
	 * Enable {@link Level#FINEST} logging for specified classes and
	 * {@link Level#WARNING} logging for all classes
	 * @param classes
	 * 		The classes for which to enable finest logging
	 */
	public static void enableConsoleLogging(Class<?>... classes) {
		ConsoleHandler handler = new ConsoleHandler();
		//handler.setFormatter(new SimpleFormatter());
		handler.setLevel(Level.ALL);
		Logger.getLogger("").addHandler(handler);
		Logger.getLogger("").setLevel(Level.WARNING);
		for (Class<?> c : classes) {
			Logger.getLogger(c.getCanonicalName()).setLevel(Level.FINEST);
		}
	}

}
