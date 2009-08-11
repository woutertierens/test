package org.jpropeller.properties.values;

import org.jpropeller.info.PropEditability;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.exception.InvalidValueException;
import org.jpropeller.properties.exception.ReadOnlyException;

/**
 * A {@link ValueProcessor} takes input values, and may either
 * pass them to an output unmodified, modify them to produce an 
 * acceptable output value based on the input, or reject them
 * entirely by throwing an exception.
 * 
 * This can be used to implement validation of new values for a 
 * {@link Prop}, including making {@link Prop}s read-only by
 * rejecting all values, enforcing a fixed range of numeric values,
 * etc.
 * 
 * {@link ValueProcessor}s are immutable, and do NOT rely on any
 * external state. This means that a given instance of a {@link ValueProcessor}
 * will always respond to the same value in the same way.
 *
 * @param <T> The type of value that can be processed
 */
public interface ValueProcessor<T> {

	/**
	 * Process an input value.
	 * @param input
	 * 		The input value.
	 * @return
	 * 		An acceptable value, based on the input value.
	 * May be exactly the input value if it is acceptable.
	 * @throws InvalidValueException
	 * 		If the input value is not acceptable, and cannot
	 * be modified to make it acceptable.
	 * @throws ReadOnlyException
	 * 		If NO input value is acceptable - for example 
	 * if the processor is being used to produce a read-only 
	 * {@link Prop}
	 */
	public T process (T input) throws InvalidValueException, ReadOnlyException;
	
	/**
	 * Return the editability offered by this {@link ValueProcessor}.
	 * That is, if any values are accepted, {@link PropEditability#EDITABLE}
	 * will be returned, otherwise if all values are always rejected,
	 * {@link PropEditability#READ_ONLY} will be returned.
	 * @return editability associated with the processor
	 */
	public PropEditability getEditability();
	
}
