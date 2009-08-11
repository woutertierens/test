package org.jpropeller.comparison;

/**
 * A {@link Filter} will accept or reject items of a given type
 *
 * @param <T>		The type of object we can filter
 */
public interface Filter<T> {

	/**
	 * Filters a value
	 * @param value		The value to filter
	 * @return			True if the value is accepted by 
	 * 					the filter, false if it is rejected.
	 */
	public boolean accept(T value);

	/**
	 * Return a phrase describing the requirement for the filter
	 * to return true.
	 * 
	 * @return Requirements Phrase
	 */
	public String requirementsPhrase();
	
}
