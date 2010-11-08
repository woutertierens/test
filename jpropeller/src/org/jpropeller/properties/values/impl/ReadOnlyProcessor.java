package org.jpropeller.properties.values.impl;

import org.jpropeller.info.PropEditability;
import org.jpropeller.properties.exception.InvalidValueException;
import org.jpropeller.properties.exception.ReadOnlyException;
import org.jpropeller.properties.values.ValueProcessor;

/**
 * A {@link ValueProcessor} that rejects all inputs with a {@link ReadOnlyException}
 * @param <T> Types of values processed
 */
public class ReadOnlyProcessor<T> implements ValueProcessor<T> {

	//This is the instance we give out. Due to type erasure, and lack of
	//state in the instances, all instances are identical anyway - 
	//we just preserve compile-time type safety by handing out the instance 
	//cast to the correct parametric type to ensure it only ever receives valid input types.
	@SuppressWarnings("rawtypes")
	private final static ReadOnlyProcessor INSTANCE = new ReadOnlyProcessor();

	/**
	 * Create a {@link ReadOnlyProcessor}
	 * 
	 * @param <T>		The type of value processed
	 * @return			An {@link ReadOnlyProcessor} instance
	 */
	//See explanation on SuppressWarnings above
	@SuppressWarnings("unchecked")
	public final static <T> ReadOnlyProcessor<T> get() {
		return (ReadOnlyProcessor<T>)INSTANCE;
	}
	
	private ReadOnlyProcessor(){
	};
	
	@Override
	public PropEditability getEditability() {
		return PropEditability.READ_ONLY;
	}

	@Override
	public T process(T input) throws InvalidValueException, ReadOnlyException {
		throw new ReadOnlyException();
	}

}
