package org.jpropeller.util;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jpropeller.transformer.Transformer;

/**
 * Simple general purpose utility methods
 * These are not specific to jpropeller itself
 */
public class GeneralUtils {

	private final static DecimalFormat TWO_DP_FORMAT = new DecimalFormat("#.##");
	private final static DecimalFormat THREE_DP_FORMAT = new DecimalFormat("#.###");
	
	/**
	 * Enable the Nimbus LaF if available
	 */
	public static void enableNimbus() {
    	try {            
//            UIManager.setLookAndFeel(
//                    "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
    		for (LookAndFeelInfo laf :
    			UIManager.getInstalledLookAndFeels()) {
    			    if ("Nimbus".equals(laf.getName())) {
    			         UIManager.setLookAndFeel(laf.getClassName());
    			}
    		}
        } catch (Exception e) {
        	//Fall back to system look and feel if nimbus not available
        	try{
        		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        	} catch (Exception e2) {
        	}
        }
	}

	/**
	 * Get the default documents folder for the system.
	 * Should be used instead of System.getProperty("user.home"), as a default
	 * location for files.
	 * @return	Default documents folder.
	 */
	public static File getDocumentsFolder() {
		//This is always a good default, and is all we need on Windows, where it
		//will be "My Documents" or possibly "Documents" in the user's home directory.
		File defaultFile = javax.swing.filechooser.FileSystemView.getFileSystemView().getDefaultDirectory();
		
		String osName = System.getProperty("os.name");
		if (osName != null) {
			osName = osName.toLowerCase();
			
			//On OS X we can always use the same path relative to the user home - the
			//file name is always the same, and is only displayed differently in different
			//languages (http://developer.apple.com/library/mac/#documentation/MacOSX/Conceptual/BPInternational/Articles/LocalizingPathnames.html#//apple_ref/doc/uid/20002141-BBCFJBFB)
			if (osName.indexOf("mac os x") >= 0) {
				File osxFile = new File(System.getProperty("user.home") + File.separator + "Documents");
				if (osxFile.exists() && osxFile.isDirectory()) {
					return osxFile;
				}
			}
		}
			
		return defaultFile;
	}

	/**
	 * Produce a filename with a prefix, then a hyphen, then the current time
	 * in standard format, then the suffix.
	 * @param prefix	The file prefix (filename, or path and filename, etc.)
	 * @param suffix	The file suffix
	 * @return			The full file name
	 */
	public static String timeStampedFilename(String prefix, String suffix) {
		DateTime now = new DateTime();
		DateTimeFormatter formatter = DateTimeFormat.forPattern("YYYY-MM-dd HH-mm");
		return prefix + "-" + now.toString(formatter) + suffix;
	}
	
	/**
	 * Enable console logging.
	 * Enable {@link Level#FINEST} logging for specified classes and
	 * {@link Level#WARNING} logging for all classes
	 * @param classes
	 * 		The classes for which to enable finest logging
	 */
	public static void enableConsoleLogging(Class<?>... classes) {
		enableConsoleLogging(Level.WARNING, classes);
	}
	
	/**
	 * Enable console logging.
	 * Enable {@link Level#FINEST} logging for specified classes and
	 * specified logging for all other classes
	 * @param generalLevel	The {@link Level} for other classes.
	 * @param classes		The classes for which to enable finest logging
	 */
	public static void enableConsoleLogging(Level generalLevel, Class<?>... classes) {
		System.out.println("Enabled console logging...");
		
		//Remove any existing handlers
		Logger rootLogger = Logger.getLogger("");
		Handler[] handlers = rootLogger.getHandlers();
		for (Handler handler : handlers) {
			rootLogger.removeHandler(handler);
		}

		//Add our logger
		ConsoleHandler handler = new ConsoleHandler();
		//handler.setFormatter(new SimpleFormatter());
		handler.setLevel(Level.ALL);
		Logger.getLogger("").addHandler(handler);
		Logger.getLogger("").setLevel(generalLevel);
		
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
	 * Format a number to three decimal places
	 * @param number		To format
	 * @return				Formatted number
	 */
	public static String threeDP(double number) {
		return THREE_DP_FORMAT.format(number);
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
	 * Multiply a {@link Color}s alpha by a factor,
	 * then clip to 0-255 and return as a new {@link Color}
	 * @param c			The input color
	 * @param factor	The alpha scaling factor
	 * @return			The output faded color
	 */
    public static Color transparentColor(Color c, double factor) {
    	return new Color(	c.getRed(), 
    						c.getGreen(),
    						c.getBlue(),
    						clip((int)(c.getAlpha() * factor), 0, 255));
    }
    
	/**
	 * Scale a {@link Color} by the same factor across red, green and blue,
	 * then clip to 0-255 and return as a new {@link Color}
	 * @param c			The input color
	 * @param factor	The factor
	 * @return			The output scaled color
	 */
    public static Color fadeColor(Color c, double factor) {
    	return new Color(	clip((int)(lerp(c.getRed(), 255, factor)), 0, 255), 
    						clip((int)(lerp(c.getGreen(), 255, factor)), 0, 255),
    						clip((int)(lerp(c.getBlue(), 255, factor)), 0, 255));
    }
    
	/**
	 * Blend from one {@link Color} to another
	 * then clip to 0-255 and return as a new {@link Color}
	 * @param first			The first input color
	 * @param second			The second input color
	 * @param factor	The factor - 0 gives first color, 1 gives second, values in between
	 * 					interpolate, values outside 0-1 extrapolate (but are clipped)
	 * @return			The output scaled color
	 */
    public static Color blendColors(Color first, Color second, double factor) {
    	return new Color(	clip((int)(lerp(first.getRed(), second.getRed(), factor)), 0, 255), 
    						clip((int)(lerp(first.getGreen(), second.getGreen(), factor)), 0, 255),
    						clip((int)(lerp(first.getBlue(), second.getBlue(), factor)), 0, 255));
    }
    
    /**
     * Linearly interpolate/extrapolate from one double to another, by a certain
     * scale
     * @param from		The value returned when by == 0 
     * @param to		The value returned when by == 1
     * @param by		The interpolation/extrapolation position
     * @return			The lerped value
     */
    public final static double lerp(double from, double to, double by) {
    	return (from * (1-by)) + to * by;
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


    /**
     * Sleep up to a given time, or less if {@link InterruptedException} is caught
     * @param millis		Time to sleep, in milliseconds
     */
	public static void sleepUpTo(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}

    /**
     * Sleep for at least a given time, ignoring any {@link InterruptedException}
     * @param millis		Time to sleep, in milliseconds
     */
	public static void sleep(long millis) {
		long end = System.currentTimeMillis() + millis;
		long left = 0;
		while ((left = (end - System.currentTimeMillis())) > 0) {
			sleepUpTo(left);
		}
	}


	/**
	 *	Copies src file to dst file.
	 *  If the dst file does not exist, it is created
	 *  @param src	Source file
	 *  @param dst	Destination file 
	 *  @throws	IOException		If src cannot be read, or dst cannot be created/written
	 */
	public static void copyFile(File src, File dst) throws IOException { 
		InputStream in = null; 
		OutputStream out = null;
		
		try {
			in = new FileInputStream(src);
			out = new FileOutputStream(dst);
			byte[] buf = new byte[1024]; 
			int len; 
			while ((len = in.read(buf)) > 0) { 
				out.write(buf, 0, len); 
			} 
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}
    
	/**
	 * Return a value limited to be not less than a min 
	 * and not more than a max 
	 * @param value		The value
	 * @param min		The minimum acceptable value
	 * @param max		The maximum acceptable value
	 * @return			The value, limited to lie in range
	 */
	public static int limit(int value, int min, int max) {
		if (value < min) {
			value = min;
		}
		if (value > max) {
			value = max;
		}
		return value;
	}

	/**
	 * Compare two values, first checking whether either is null
	 * and if so considering null to be equal only to null.
	 * @param a		First value
	 * @param b		Second value
	 * @return		True if objects are equal using {@link Object#equals(Object)},
	 * 				OR both null. Note that the equality method of object a
	 * 				is used, if this matters for debugging.
	 */
	public static boolean equalIncludingNull(Object a, Object b) {
		//If a is null, only equal if b is too
		if (a == null) {
			return (b == null);
		//a is not null, so if b is it is not equal
		} else if (b == null) {
			return false;
		//Neither a nor b is null
		} else {
			return a.equals(b);
		}
	}
	
	/**
	 * Search for the next free file name, following
	 * a linear indexing system
	 * @param firstIndex		The first file index to check
	 * @param fileNamer			A {@link Transformer} from a file index
	 * 							to an actual file.
	 * @return					The first free file index
	 * @throws IOException		If files cannot be checked for existence, etc.
	 */
	public static int findNextFreeFileIndex(int firstIndex, Transformer<Integer, File> fileNamer) throws IOException {
		
		//Scan for a free file
		int index = firstIndex;
		
		//If index is 0, use 1 instead
		if (index == 0) {
			index = 1;
		}
		
		//First, scan exponentially upwards
		while (fileNamer.transform(index).exists()) {
			index*= 2;
			//System.out.println("Scanned up to " + index);
		}
		
		//Now, binary search for a free file
		int step = index/2;
		while (step > 0) {
			if (fileNamer.transform(index).exists()) {
				index += step;
				//System.out.println("Exists - up by " + step + " to " + index);
			} else {
				index -= step;
				//System.out.println("Doesn't exist - stepped down by " + step + " to " + index);
			}
			step /= 2;
		}
		
		//System.out.println("Binary searched to " + index);
		
		//Note we may have moved onto the last used file, so linear scan for last "bit"
		index -= 2;
		while (index < 0 || fileNamer.transform(index).exists()) {
			index++;
		}

		//System.out.println("Linear scanned to " + index);
		
		//Now check that index doesn't exist, and index-1 does (or is negative)
		if (!fileNamer.transform(index).exists() && (index == 0 || fileNamer.transform(index-1).exists())) {
			return index;
		} else {
			throw new RuntimeException("Logical failure in finding free index.");
		}
	}
	
}
