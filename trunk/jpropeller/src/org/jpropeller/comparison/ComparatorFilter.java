package org.jpropeller.comparison;

import java.util.Comparator;

import org.jpropeller.properties.change.Immutable;

/**
 * A {@link ComparatorFilter} represents a test of a value, in the form of:
 * 
 * value RELATION limit
 * 
 * Where RELATION is a comparison of one of the standard types: (<, <=, ==, >, >=, !=)
 * The types of comparison are represented by a {@link ComparisonType}
 * 
 * A {@link Comparator} actually compares the value to the limit - for example
 * comparing {@link Number}s according to normal rules, {@link String}s alphabetically, 
 * etc.
 * 
 * This class is immutable.
 * 
 * @param <T> 		The type of value we can compare
 */
public class ComparatorFilter<T> implements Immutable, Filter<T> {

	T limit;
	ComparisonType type;
	private Comparator<T> comparator;

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
	public static <S extends Comparable<? super S>> ComparatorFilter<S> get(ComparisonType type, S limit) {
		return new ComparatorFilter<S>(NaturalComparator.<S>create(), type, limit);
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
	public static <S> ComparatorFilter<S> get(Comparator<S> comparator, ComparisonType type, S limit) {
		return new ComparatorFilter<S>(comparator, type, limit);
	}
	
	/**
	 * Creates a {@link ComparatorFilter}
	 * 
	 * @param comparator	The {@link Comparator} to actually compare the objects
	 * 						This must be immutable.
	 * @param type			The type of comparison - lets us check the resulting 
	 * 						value of {@link Comparator#compare(Object, Object)}
	 * @param limit			The limit against which we are checking
	 * 						This must be immutable.
	 */
	private ComparatorFilter(Comparator<T> comparator, ComparisonType type, T limit) {
		super();
		this.limit = limit;
		this.type = type;
		this.comparator = comparator;
	}

	/**
	 * Checks whether a value is accepted by the comparison
	 * 
	 * @param value		The value to compare
	 * 
	 * @return			True if the value compares as specified
	 * 					False otherwise (false is also returned 
	 * 					for null values) 
	 */
	public boolean accept(T value) {
		if (value == null) return false;

		//Get whether value is less than, equal to or greater than limit
		int comparatorResult = comparator.compare(value, limit);

		//Now check whether the comparator result is compatible with the
		//comparison type (<=, =, != etc.)
		return type.checkComparatorResult(comparatorResult);
	}

	/**
	 * Return a phrase describing the requirement for the filter
	 * to return true.
	 * 
	 * @return Requirements Phrase
	 */
	public String requirementsPhrase() {
		return "must be <b>" + type.symbol() + " " + limit + "</b>";
	}
	
}
