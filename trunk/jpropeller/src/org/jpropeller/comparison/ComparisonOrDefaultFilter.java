package org.jpropeller.comparison;

import java.util.Comparator;

import org.jpropeller.info.PropEditability;
import org.jpropeller.properties.change.Immutable;
import org.jpropeller.properties.exception.InvalidValueException;
import org.jpropeller.properties.exception.ReadOnlyException;
import org.jpropeller.properties.values.ValueProcessor;

/**
 * A {@link ComparisonOrDefaultFilter} represents a test of a value, in the form of:
 * 
 * value RELATION limit
 * 
 * Where RELATION is a comparison of one of the standard types: (<, <=, ==, >, >=, !=)
 * The types of comparison are represented by a {@link ComparisonType}
 * When this fails, the value is set to a specific other value - for example
 * we might check >= 5, and if this is false set 5.
 * 
 * A {@link Comparator} actually compares the value to the limit - for example
 * comparing {@link Number}s according to normal rules, {@link String}s alphabetically, 
 * etc.
 * 
 * This class is immutable.
 * 
 * @param <T> 		The type of value we can compare
 */
public class ComparisonOrDefaultFilter<T> implements Immutable, Filter<T>, ValueProcessor<T> {

	T limit;
	T defaultValue;
	ComparisonType type;
	private Comparator<T> comparator;

	/**
	 * Gets a {@link ComparisonOrDefaultFilter} using natural ordering
	 * 
	 * @param type			The type of comparison - lets us check the resulting 
	 * 						value of {@link Comparator#compare(Object, Object)}
	 * @param limit			The limit against which we are checking
	 * 						This must be immutable.
	 * @param defaultValue	The value set when the comparison fails
	 * @param <S>			The type of value compared
	 *  
	 * @return 				A {@link ComparisonOrDefaultFilter} as specified
	 */
	public static <S extends Comparable<? super S>> ComparisonOrDefaultFilter<S> get(ComparisonType type, S limit, S defaultValue) {
		return new ComparisonOrDefaultFilter<S>(NaturalComparator.<S>create(), type, limit, defaultValue);
	}
	
	/**
	 * Get a {@link ComparisonOrDefaultFilter}
	 * 
	 * @param comparator	The {@link Comparator} to actually compare the objects
	 * 						This must be immutable.
	 * @param type			The type of comparison - lets us check the resulting 
	 * 						value of {@link Comparator#compare(Object, Object)}
	 * @param limit			The limit against which we are checking
	 * 						This must be immutable.
	 * @param defaultValue	The value set when the comparison fails
	 * @param <S> 			The type of value compared
	 *  
	 * @return 				A {@link ComparisonOrDefaultFilter} as specified
	 */
	public static <S> ComparisonOrDefaultFilter<S> get(Comparator<S> comparator, ComparisonType type, S limit, S defaultValue) {
		return new ComparisonOrDefaultFilter<S>(comparator, type, limit, defaultValue);
	}
	
	/**
	 * Creates a {@link ComparisonOrDefaultFilter}
	 * 
	 * @param comparator	The {@link Comparator} to actually compare the objects
	 * 						This must be immutable.
	 * @param type			The type of comparison - lets us check the resulting 
	 * 						value of {@link Comparator#compare(Object, Object)}
	 * @param limit			The limit against which we are checking
	 * 						This must be immutable.
	 * @param defaultValue	The value set when the comparison fails
	 */
	private ComparisonOrDefaultFilter(Comparator<T> comparator, ComparisonType type, T limit, T defaultValue) {
		super();
		this.limit = limit;
		this.type = type;
		this.comparator = comparator;
		this.defaultValue = defaultValue;
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
	 * The default value that is set when filter fails
	 * @return	Default value
	 */
	public T defaultValue() {
		return defaultValue;
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
	
	@Override
	public T process (T input) throws InvalidValueException, ReadOnlyException {
		if (accept(input)) {
			return input;
		} else {
			return defaultValue;
		}
	}
	
	@Override
	public PropEditability getEditability() {
		return PropEditability.EDITABLE;
	}
	
}
