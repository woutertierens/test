package org.jpropeller.comparison;

import java.util.Comparator;

/**
 * Types of comparison
 */
public enum ComparisonType {
	/** Value must be strictly less than limit */
	LESS_THAN("<", -1, -1),
	/** Value must be less than or equal to limit */
	LESS_THAN_OR_EQUAL("<=", -1, 0),
	/** Value must equal expected */
	EQUAL("=", 0, 0),
	/** Value must be more than or equal to limit */
	MORE_THAN_OR_EQUAL(">=", 0, 1),
	/** Value must be strictly more than limit  */
	MORE_THAN(">", 1, 1),
	/** Value must not equal specified value*/
	NOT_EQUAL("!=", -1, 1);

	String symbol;
	int first;
	int second;
	
	private ComparisonType(String symbol, int first, int second) {
		this.symbol = symbol;
		this.first = first;
		this.second = second;
	}

	/**
	 * String giving the symbol for the comparison
	 * @return symbol
	 */
	public String symbol() {
		return symbol;
	}
	
	/**
	 * Checks whether the result of calling a {@link Comparator}, with
	 * the checked value first and the limit value second, indicates the
	 * comparison is valid for this type.
	 * <p/>
	 * As an example, using a <code>Comparator&lt;Double&gt; comparator</code>,
	 * and a {@link ComparisonType} <code>type</code> for "<=", we might call:
	 * <code>
	 * type.checkComparatorResult(comparator.compare(3, limit));
	 * </code>
	 * This will return true if and only if 3 <= limit.
	 * 
	 * @param result		The result of a 
	 * 						{@link Comparator#compare(Object, Object)}
	 * 						where the first parameter is the checked value, and 
	 * 						the second parameter is the limit.
	 * @return				True if the result is accepted for this comparison type
	 */
	public boolean checkComparatorResult(int result) {
		return result == first || result == second;
	}
	
	@Override
	public String toString() {
		return symbol();
	}
	
}