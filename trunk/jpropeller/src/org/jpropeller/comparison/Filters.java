package org.jpropeller.comparison;

import java.util.Comparator;

/**
 * Factory methods for simple filters
 */
public class Filters {

	private Filters(){
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
	 * @param <T>		The type of value filtered 
	 * @param first		The first required comparison
	 * @param second	The second required comparison
	 * @return 			A {@link RangeFilter}
	 */
	public final static <T> Filter<T> range(ComparatorFilter<T> first, ComparatorFilter<T> second) {
		return RangeFilter.get(first, second);
	}
	
	/**
	 * Gets a {@link ComparatorFilter} using natural ordering
	 * 
	 * @param type			The type of comparison - lets us check the resulting 
	 * 						value of {@link Comparator#compare(Object, Object)}
	 * @param limit			The limit against which we are checking
	 * 						This must be immutable.
	 * @param <S>			The type of value compared
	 *  
	 * @return 				A {@link ComparatorFilter} as specified
	 */
	public static <S extends Comparable<? super S>> ComparatorFilter<S> comparison(ComparisonType type, S limit) {
		return ComparatorFilter.get(type, limit);
	}
	
	/**
	 * Gets a {@link Filter} that accepts only one value
	 * 
	 * @param value			The accepted value.
	 * 						This must be immutable.
	 * @param <S>			The type of value compared
	 *  
	 * @return 				A {@link Filter} as specified
	 */
	public static <S extends Comparable<? super S>> ComparatorFilter<S> equals(S value) {
		return ComparatorFilter.get(ComparisonType.EQUAL, value);
	}

	/**
	 * Gets a {@link Filter} that rejects only one value
	 * 
	 * @param value			The rejected value.
	 * 						This must be immutable.
	 * @param <S>			The type of value compared
	 *  
	 * @return 				A {@link Filter} as specified
	 */
	public static <S extends Comparable<? super S>> Filter<S> differs(S value) {
		return ComparatorFilter.get(ComparisonType.NOT_EQUAL, value);
	}

	/**
	 * Get a {@link ComparatorFilter}
	 * 
	 * @param comparator	The {@link Comparator} to actually compare the objects
	 * 						This must be immutable.
	 * @param type			The type of comparison - lets us check the resulting 
	 * 						value of {@link Comparator#compare(Object, Object)}
	 * @param limit			The limit against which we are checking
	 * 						This must be immutable.
	 * @param <S> 			The type of value compared
	 *  
	 * @return 				A {@link ComparatorFilter} as specified
	 */
	public static <S> Filter<S> get(Comparator<S> comparator, ComparisonType type, S limit) {
		return ComparatorFilter.get(comparator, type, limit);
	}
}
