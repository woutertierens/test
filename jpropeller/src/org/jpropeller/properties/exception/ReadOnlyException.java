package org.jpropeller.properties.exception;

import org.jpropeller.properties.Prop;
import org.jpropeller.properties.calculated.impl.CalculatedProp;

/**
 * An {@link UnsupportedOperationException} thrown when an attempt
 * is made to write to a read-only value - for example to a 
 * read-only {@link Prop}, such as a {@link CalculatedProp}
 */
public class ReadOnlyException extends UnsupportedOperationException {

	/**
	 * Create a default {@link ReadOnlyException}
	 */
	public ReadOnlyException() {
	}

	/**
	 * Create a {@link ReadOnlyException}
	 * @param message Detailed description of error
	 */
	public ReadOnlyException(String message) {
		super(message);
	}

}
