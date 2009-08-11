package org.jpropeller.comparison;

import org.jpropeller.properties.change.Immutable;

/**
 * A {@link RangeFilter} accepts values bounded by two {@link ComparatorFilter}s
 *
 * @param <T>		The type of object we can filter 
 */
public class RangeFilter<T> implements Filter<T>, Immutable {

	ComparatorFilter<T> first;
	ComparatorFilter<T> second;

	/**
	 * Create a {@link RangeFilter} bounded by two {@link ComparatorFilter}s.
	 * 
	 * Note that for the filter ever to pass, the {@link ComparatorFilter}s 
	 * must accept each other's limits.
	 * For example, if the {@link ComparatorFilter}s are on double values, then
	 * using normal notation, we might have
	 * 
	 * first requires value < 3
	 * second requires value > 4
	 * 
	 * In this case, the range filter will never return true.
	 *  
	 * @param <T>		The type of value filtered 
	 * @param first		The first required comparison
	 * @param second	The second required comparison
	 * @return 			A {@link RangeFilter}
	 */
	public final static <T> RangeFilter<T> get(ComparatorFilter<T> first, ComparatorFilter<T> second) {
		return new RangeFilter<T>(first, second);
	}
	
	/**
	 * Create a {@link RangeFilter} bounded by two {@link ComparatorFilter}s.
	 * 
	 * Note that for the filter ever to pass, the {@link ComparatorFilter}s 
	 * must accept each other's limits.
	 * For example, if the {@link ComparatorFilter}s are on double values, then
	 * using normal notation, we might have
	 * 
	 * first requires value < 3
	 * second requires value > 4
	 * 
	 * In this case, the range filter will never return true. 
	 * 
	 * @param first
	 * 		The first required comparison
	 * @param second
	 * 		The second required comparison
	 */
	private RangeFilter(ComparatorFilter<T> first, ComparatorFilter<T> second) {
		super();
		this.first = first;
		this.second = second;
	}

	@Override
	public boolean accept(T value) {
		return first.accept(value) && second.accept(value);
	}

	public String requirementsPhrase() {
		return first.requirementsPhrase() + ", " + second.requirementsPhrase();
	}
	
}
