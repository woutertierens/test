package org.jpropeller.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;

/**
 *	Simple utilities for string handling, e.g. to strip all whitespace
 */
public class StringUtilities {

	private final static DecimalFormat DEFAULT_DOUBLE_FORMAT = new DecimalFormat("0.####");
	
    /**
     * Static class
     */
    private StringUtilities() {
        super();
    }

    /**
     * Strip all whitespace from string, whitespace is anything matching the REGEX "\\s",
     * including spaces and tabs, etc. 
     * @param s String to strip
     * @return String made from s by removing whitespace
     */
    public static String stripWhiteSpace(String s) {
        return s.replaceAll("\\s", "");
    }
    
    /**
     * Convert an integer to a string. If it is shorter than
     * padLength, then it will have "0" characters prepended
     * until it is padLength
     * @param i The integer
     * @param padLength The minimum length to which to pad
     * @return
     * 		The integer as a padded string
     */
	public static String pad(int i, int padLength) {
		String s = Integer.toString(i);
		while (s.length() < padLength) s = "0" + s;
		return s;
	}

	/**
	 * Iterate over an iterable object, converting each element
	 * of the iteration into a String via {@link Object#toString()},
	 * with a separator between each element. 
	 * @param iterable
	 * 		Object to be iterated
	 * @param separator
	 * 		Separator between each element, and optionally also at the
	 * end of the string
	 * @param finalSeparator
	 * 		True to end the string with a final separator, false to
	 * have separators only between pairs of elements
	 * @return
	 * 		A string with iterated elements converted to strings
	 */
	public static String iterateToString(Iterable<?> iterable, String separator, boolean finalSeparator) {
		StringBuilder s = new StringBuilder();
		Iterator<?> it = iterable.iterator();
		while (it.hasNext()) {
			Object o = it.next();
			s.append(o.toString());
			
			//We need a separator if we insert a final separator,
			//OR we have another value to follow
			if (finalSeparator || it.hasNext()) {
				s.append(separator);
			}
		}
		
		return s.toString();
	}
	
	/**
	 * Iterate over an array, converting each double
	 * of the array into a String via specified {@link NumberFormat},
	 * with a separator between each element. 
	 * @param array
	 * 		Array to be iterated
	 * @param separator
	 * 		Separator between each element, and optionally also at the
	 * end of the string
	 * @param finalSeparator
	 * 		True to end the string with a final separator, false to
	 * have separators only between pairs of elements
	 * @param format
	 * 		The format for the double values
	 * @return
	 * 		A string with iterated elements converted to strings
	 */
	public static String doubleArrayToString(double[] array, String separator, boolean finalSeparator, NumberFormat format) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			double o = array[i];
			s.append(format.format(o));
			
			//We need a separator if we insert a final separator,
			//OR we have another value to follow
			if (finalSeparator || i < array.length-1) {
				s.append(separator);
			}
		}
		
		return s.toString();
	}
	
	/**
	 * Iterate over an array, converting each double
	 * of the array into a String via default double format "0.####",
	 * with a tab between each element. 
	 * @param array
	 * 		Array to be iterated
	 * @return
	 * 		A string with iterated elements converted to strings
	 */
	public static String doubleArrayToString(double[] array) {
		return doubleArrayToString(array, "\t", false, DEFAULT_DOUBLE_FORMAT);
	}

}
