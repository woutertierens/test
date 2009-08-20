package org.jpropeller.util;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
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

	private final static DecimalFormat TWO_DP_FORMAT = new DecimalFormat("#.##");
	
	/**
	 * Enable the Nimbus LaF if available
	 */
	public static void enableNimbus() {
    	try {            
            UIManager.setLookAndFeel(
                    "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
        	//Fall back to system look and feel if nimbus not available
        	try{
        		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        	} catch (Exception e2) {
        	}
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
	 * @param <J>			The type of entry
	 * @param element		The entry
	 * @return				A list with the single entry
	 */
	public static <J> List<J> makeList(J element) {
		List<J> list = new LinkedList<J>();
		list.add(element);
		return list;
	}
	
	/**
	 * Make a list from a vararg of elements
	 * 
	 * @param <J>		The type of entry
	 * @param elements	The entries
	 * @return			A list with the entries
	 */
	public static <J> List<J> makeList(J... elements) {
		List<J> list = new ArrayList<J>(elements.length);
		for (J input : elements){
			list.add(input);
		}
		return list;
	}

	/**
	 * Make a list from a first element, and a vararg of elements
	 * 
	 * @param <J>					The type of entry
	 * @param firstElement			The first element
	 * @param additionalElements	Any additional elements
	 * 		
	 * @return						A list with the entries
	 */
	public static <J> List<J> makeList(J firstElement, J... additionalElements) {
		List<J> list = new ArrayList<J>(additionalElements.length + 1);
		list.add(firstElement);
		for (J input : additionalElements){
			list.add(input);
		}
		return list;
	}

	/**
	 * Format a number to two decimal places
	 * @param number		To format
	 * @return				Formatted number
	 */
	public static String twoDP(double number) {
		return TWO_DP_FORMAT.format(number);
	}
	
	/**
	 * Make an iterable into a string, which contains
	 * the {@link Object#toString()} results of
	 * each iterated object, in iterated order, with
	 * the separator ", " between each pair.
	 * 
	 * @param iterable		To iterate
	 * @return				String list of iteration
	 */
	public static String iterateToString(Iterable<?> iterable) {
		return iterateToString(iterable, ", ");
	}
	
	/**
	 * Make an iterable into a string, which contains
	 * the {@link Object#toString()} results of
	 * each iterated object, in iterated order, with
	 * the given separator between each pair.
	 * 
	 * @param iterable		To iterate
	 * @param separator		The string to place between each pair
	 * @return				String list of iteration
	 */
	public static String iterateToString(Iterable<?> iterable, String separator) {
		boolean first = true;
		StringBuilder s = new StringBuilder();
		for (Object o : iterable) {
			if (!first) s.append(separator);
			s.append(o.toString());
			first = false;
		}
		return s.toString();
	}
	
	/**
	 * Compare two int values
	 * @param thisVal			The value to be treated as "this"
	 * @param anotherVal		The value to be compared to thisVal
	 * @return					1 if thisVal is greater, 0 if it is equal, or -1 of it is less than
	 */
	public static int compare(int thisVal, int anotherVal) {
		return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
	}

	/**
	 * Scale a {@link Color} by the same factor across red, green and blue,
	 * then clip to 0-255 and return as a new {@link Color}
	 * @param c			The input color
	 * @param factor	The factor
	 * @return			The output scaled color
	 */
    public static Color scaleColor(Color c, double factor) {
    	return new Color(	clip((int)(c.getRed() * factor), 0, 255), 
    						clip((int)(c.getGreen() * factor), 0, 255),
    						clip((int)(c.getBlue() * factor), 0, 255));
    }
    
    /**
     * Return a double value clipped to lie from min to max, inclusive
     * @param value		The value
     * @param min		The minimum (inclusive)
     * @param max		The maximum (inclusive)
     * @return			The clipped value
     */
    public static double clip(double value, double min, double max) {
    	if (value < min) return min;
    	else if (value > max) return max;
    	else return value;
    }
    
    /**
     * Return an int value clipped to lie from min to max, inclusive
     * @param value		The value
     * @param min		The minimum (inclusive)
     * @param max		The maximum (inclusive)
     * @return			The clipped value
     */
    public static int clip(int value, int min, int max) {
    	if (value < min) return min;
    	else if (value > max) return max;
    	else return value;
    }

    /**
     * Convert an integer value to a string, then pad that string
     * to at least minLength and return the result.
     * @param value			The integer value
     * @param minLength		The minimum length of the returned string.
     * @return				The value as a string, padded with 0's until
     * 						it is at least minLength. Note that if the 
     * 						value gives a string longer than minLength,
     * 						it will be returned directly.
     */
    public static String padInteger(int value, int minLength) {
    	String s = Integer.toString(value);
    	while (s.length() < minLength) {
    		s = "0" + s;
    	}
    	return s;
    }
    
}
