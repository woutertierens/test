package org.jpropeller.util;

import java.util.LinkedList;
import java.util.List;
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

	/**
	 * Get a {@link Logger} for a class
	 * @param clazz
	 * 		The class
	 * @return
	 * 		The logger
	 */
	public static Logger logger(Class<?> clazz) {
		return Logger.getLogger(clazz.getCanonicalName());
	}
	
	/**
	 * Make a list with a single entry
	 * @param <J>
	 * 		The type of entry
	 * @param input
	 * 		The entry
	 * @return
	 * 		A list with the single entry
	 */
	public static <J> List<J> makeList(J input) {
		List<J> list = new LinkedList<J>();
		list.add(input);
		return list;
	}
	
	/**
	 * Make a list from a vararg of inputs
	 * @param <J>
	 * 		The type of entry
	 * @param inputs
	 * 		The entries
	 * @return
	 * 		A list with the entries
	 */
	public static <J> List<J> makeList(J... inputs) {
		List<J> list = new LinkedList<J>();
		for (J input : inputs){
			list.add(input);
		}
		return list;
	}
	
}
