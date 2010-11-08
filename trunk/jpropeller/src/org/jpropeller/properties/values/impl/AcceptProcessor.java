package org.jpropeller.properties.values.impl;

import org.jpropeller.info.PropEditability;
import org.jpropeller.properties.exception.InvalidValueException;
import org.jpropeller.properties.exception.ReadOnlyException;
import org.jpropeller.properties.values.ValueProcessor;

/**
 * {@link ValueProcessor} that accepts all values unaltered
 *
 * @param <T> The type of value processed
 */
public class AcceptProcessor<T> implements ValueProcessor<T> {

	//This is the instance we give out. Due to type erasure, and lack of
	//state in the instances, all instances are identical anyway - 
	//we just preserve compile-time type safety by handing out the instance 
	//cast to the correct parametric type to ensure it only ever receives valid input types.
	@SuppressWarnings("rawtypes")
	private final static AcceptProcessor INSTANCE = new AcceptProcessor();

	/**
	 * Create an {@link AcceptProcessor}
	 * 
	 * @param <T>		The type of value processed
	 * @return			An {@link AcceptProcessor} instance
	 */
	//See explanation on SuppressWarnings above
	@SuppressWarnings("unchecked")
	public final static <T> AcceptProcessor<T> get() {
		return (AcceptProcessor<T>)INSTANCE;
	}
	
	private AcceptProcessor(){
	};
	
	@Override
	public PropEditability getEditability() {
		return PropEditability.EDITABLE;
	}

	@Override
	public T process(T input) throws InvalidValueException, ReadOnlyException {
		return input;
	}

}
