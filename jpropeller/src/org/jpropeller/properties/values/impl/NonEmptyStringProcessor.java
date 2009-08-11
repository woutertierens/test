package org.jpropeller.properties.values.impl;

import org.jpropeller.info.PropEditability;
import org.jpropeller.properties.exception.InvalidValueException;
import org.jpropeller.properties.exception.ReadOnlyException;
import org.jpropeller.properties.values.ValueProcessor;

/**
 * A {@link ValueProcessor} that will only accept non-empty strings
 */
public class NonEmptyStringProcessor implements ValueProcessor<String> {
	
	private final static NonEmptyStringProcessor TRIM = new NonEmptyStringProcessor(true); 
	private final static NonEmptyStringProcessor NO_TRIM = new NonEmptyStringProcessor(false); 
	
	private boolean trim;

	/**
	 * Get a {@link NonEmptyStringProcessor}
	 * 
	 * @param trim		If true, strings are trimmed before
	 * 					checking - which means that strings
	 * 					containing only whitespace are rejected
	 * 					as being empty
	 * @return			The {@link NonEmptyStringProcessor}
	 */
	public final static NonEmptyStringProcessor get(boolean trim) {
		return trim ? TRIM : NO_TRIM;
	}
	
	/**
	 * Create a {@link NonEmptyStringProcessor}
	 * 
	 * @param trim		If true, strings are trimmed before
	 * 					checking - which means that strings
	 * 					containing only whitespace are rejected
	 * 					as being empty
	 */
	private NonEmptyStringProcessor(boolean trim) {
		super();
		this.trim = trim;
	}

	@Override
	public String process(String input) throws InvalidValueException,
			ReadOnlyException {
		String check = trim ? input.trim() : input;
		if (check.isEmpty()) throw new InvalidValueException("Must not be empty" + (trim ? ", or just whitespace" : ""));
		return input;
	}

	@Override
	public PropEditability getEditability() {
		return PropEditability.EDITABLE;
	}

}
