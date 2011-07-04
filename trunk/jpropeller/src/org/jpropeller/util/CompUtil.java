package org.jpropeller.util;

/**
 * Utils for comparison
 */
public class CompUtil {
	
	/**
	 * Check each of a list of comparison results, in order, until one
	 * has a difference (a non-zero result), at which point this
	 * result is returned. If all results are zero, zero
	 * is returned.
	 * @param comparisons		The comparison results, in descending order of importance
	 * @return			The	result of the comparisons
	 */
	public static int compareList(int... comparisons) {
		for (int c : comparisons) {
			if (c != 0) {
				return c;
			}
		}
		return 0;
	}
	
	/**
	 * Compare a pair of {@link Comparable} items
	 * @param <T>	The type of comparable item
	 * @param a		First item
	 * @param b		Second item
	 * @return		Comparison of a to b
	 */
	public static <T extends Comparable<T>> int compare(T a, T b) {
		return a.compareTo(b);
	}
}
