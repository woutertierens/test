package org.jpropeller.util;

import java.io.IOException;
import java.io.InputStream;
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
	 * No maximimum line length or count
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
		return iterateToString(iterable, separator, finalSeparator, -1, -1);
	}

	/**
	 * Iterate over an iterable object, converting each element
	 * of the iteration into a String via {@link Object#toString()},
	 * with a separator between each element.
	 * Additionally, "\n" will be inserted whenever the length of a line
	 * of output is greater than maxLineLength. Note that the linebreak
	 * occurs after the item that increases the line length over this limit,
	 * so the longest line may still be longer than maxLineLength.
	 * The last line will not have a "\n" appended.
	 * @param iterable			Object to be iterated
	 * @param separator			Separator between each element, and optionally also 
	 * 							at the end of the string
	 * @param finalSeparator	True to end the string with a final separator, false to
	 * 							have separators only between pairs of elements
	 * @param maxLineLength		The maximum length in characters of a line before "\n" 
	 * 							is appended to it.
	 * @param maxLineCount		The maximum number of lines before following lines are
	 * 							skipped and replaced by "..." 
	 * @return
	 * 		A string with iterated elements converted to strings
	 */
	public static String iterateToString(Iterable<?> iterable, String separator, boolean finalSeparator, int maxLineLength, int maxLineCount) {
		StringBuilder s = new StringBuilder();
		Iterator<?> it = iterable.iterator();
		int lastBreak = 0;
		int lineCount = 0;
		while (it.hasNext()) {
			Object o = it.next();
			s.append(o.toString());
			
			//We need a separator if we insert a final separator,
			//OR we have another value to follow
			if (finalSeparator || it.hasNext()) {
				s.append(separator);
			}
			int length = s.length();
			if (it.hasNext() && maxLineLength >= 0 && (length - lastBreak) > maxLineLength) {
				s.append("\n");
				lastBreak = s.length();
				lineCount++;
				if (maxLineCount >= 0 && lineCount >= maxLineCount) {
					s.append("...");
					return s.toString();
				}
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
	
	/**
	 * Generate a new name, guaranteed to be different from the
	 * provided name.
	 * 
	 * Where the provided name ends with " x", where x is a 
	 * valid string representation of an integer, the returned 
	 * name will be the same, but with " x" replaced by " y", where 
	 * y is the standard string representation of the integer x+1.
	 * 
	 * Where the provided name does NOT end with " x", the returned
	 * name will be the provided name with " 2" appended to it.
	 * 
	 * Hence this naming is compatible (for example) with attempting 
	 * to alter duplicate strings so they have a "copy number" appended.
	 * To do this, repeatedly call {@link #incrementName(String)} on a
	 * candidate name, until the returned string is unique. The candidate
	 * name should be set to the returned string on each call, to "count"
	 * the string through copy numbers until it is unique. This is slightly
	 * inefficient, but acceptable for fairly small sets of strings.
	 * 
	 * @param name		The input name
	 * @return			The (different) output name.
	 */
	public final static String incrementName(String name) {
		
		int lastSpace = name.indexOf(" ");
		if (lastSpace > -1) {
			String numberString = name.substring(lastSpace + 1);
			try {
				int number = Integer.parseInt(numberString);
				return name.substring(0, lastSpace) + " " + Integer.toString(number+1);
			} catch (NumberFormatException nfe) {
				//Just carry on through to default rename
			}
		}
		
		return name + " 2";
	}
	
	/**
	 * Generate a new name, guaranteed to be different from the
	 * provided name.
	 * 
	 * Where the provided name ends with "(x)", where x is a 
	 * valid string representation of an integer >= 2, the returned 
	 * name will be the same, but with "(x)" replaced by "(y)", where 
	 * y is the standard string representation of the integer x+1.
	 * 
	 * Where the provided name does NOT end with "(x)", the returned
	 * name will be the provided name with " (2)" appended to it.
	 * 
	 * Hence this naming is compatible (for example) with attempting 
	 * to alter duplicate strings so they have a "copy number" appended.
	 * To do this, repeatedly call {@link #incrementName(String)} on a
	 * candidate name, until the returned string is unique. The candidate
	 * name should be set to the returned string on each call, to "count"
	 * the string through copy numbers until it is unique. This is slightly
	 * inefficient, but acceptable for fairly small sets of strings.
	 * 
	 * @param name		The input name
	 * @return			The (different) output name.
	 */
	public final static String incrementNameWithBrackets(String name) {
		//See if we already have a number - if we do, increment it
		//We need a closing bracket at end
		if (name.endsWith(")")) {
			//Now we need an opening bracket - it must be before
			//the last character of the string since ")" is at the end
			int openIndex = name.lastIndexOf("(");
			if (openIndex > -1) {
				
				//Pull out the number (if it is one)
				String numberString = name.substring(openIndex+1, name.length()-1);
				try {
					//If we do have a number in the string, then increment it 
					//and return the result
					int number = Integer.parseInt(numberString);
					if (number >= 2) {
						number++;
						return name.substring(0, openIndex) + "(" + number + ")"; 
					}
				} catch (NumberFormatException nfe) {
					//Nothing to do - just fall through to default name
				}
			}
		}
		
		//Default is just to add (2) - we start from 2 so that
		//we will have "name", "name (2)", "name (3)" etc.
		return name + " (2)";
	}
	
	/**
	 * Get the whole contents of a stream as a string, using default encoding
	 * @param stream		The stream to read
	 * @return				The string
	 * @throws IOException	If stream cannot be read
	 */
	public final static String stringFromStream(InputStream stream) throws IOException {
		int i = 0;
		StringBuilder builder = new StringBuilder();
		while ((i = stream.read()) >= 0) {
			builder.append((char)i);				
		}
		return builder.toString();
	}

}
