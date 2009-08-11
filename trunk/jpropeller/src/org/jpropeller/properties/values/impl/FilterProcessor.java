package org.jpropeller.properties.values.impl;

import org.jpropeller.comparison.Filter;
import org.jpropeller.info.PropEditability;
import org.jpropeller.properties.exception.InvalidValueException;
import org.jpropeller.properties.exception.ReadOnlyException;
import org.jpropeller.properties.values.ValueProcessor;

/**
 * A {@link FilterProcessor} passes values accepted by a 
 * {@link Filter} unaltered, and rejects all other values.
 *
 * @param <T>		The type of processed value
 */
public class FilterProcessor<T> implements ValueProcessor<T> {

	private Filter<T> filter;
	
	/**
	 * Create a new {@link FilterProcessor}
	 * @param <T>		The type of processed value
	 * @param filter	The filter that accepts or rejects values
	 * @return			A new {@link FilterProcessor}
	 */
	public final static <T> FilterProcessor<T> create(Filter<T> filter) {
		return new FilterProcessor<T>(filter);
	}
	
	private FilterProcessor(Filter<T> filter) {
		super();
		this.filter = filter;
	}

	@Override
	public T process (T input) throws InvalidValueException, ReadOnlyException {
		if (filter.accept(input)) {
			return input;
		} else {
			throw new InvalidValueException("Value " + filter.requirementsPhrase());
		}
	}
	
	@Override
	public PropEditability getEditability() {
		return PropEditability.EDITABLE;
	}

}
