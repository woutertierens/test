package org.jpropeller.properties.exception;

import org.jpropeller.properties.Prop;
import org.jpropeller.properties.values.ValueProcessor;

/**
 * Exception thrown when a value cannot be accepted by 
 * a {@link ValueProcessor} and/or {@link Prop}
 */
public class InvalidValueException extends RuntimeException {

	/**
	 * Create an {@link InvalidValueException}
	 * with no message or cause. Note: Generally a message
	 * should be provided.
	 */
	public InvalidValueException() {
		super();
	}

	/**
	 * Create an {@link InvalidValueException}
	 * 
	 * @param message Description of error - should be
	 * localised and suitable for printing as a standalone
	 * sentence, with capitals and full stop as appropriate
	 * for language. 
	 * This should be phrased as a requirement (or list of
	 * requirements) that has not been fulfilled.
	 * 
	 * For example:
	 * 
	 * "Age must be a positive number."
	 * "Postcode must contain only numbers and letters, and a space."
	 */
	public InvalidValueException(String message) {
		super(message);
	}

	/**
	 * Create an {@link InvalidValueException} with no message.
	 * Note: Generally a message should be provided.
	 * @param cause of the exception
	 */
	public InvalidValueException(Throwable cause) {
		super(cause);
	}

	/**
	 * Create an {@link InvalidValueException}
	 * 
	 * @param message Description of error - should be
	 * localised and suitable for printing as a standalone
	 * sentence, with capitals and full stop as appropriate
	 * for language. 
	 * This should be phrased as a requirement (or list of
	 * requirements) that has not been fulfilled.
	 * 
	 * For example:
	 * 
	 * "Age must be a positive number."
	 * "Postcode must contain only numbers and letters, and a space."
	 * 
	 * @param cause of the exception
	 */
	public InvalidValueException(String message, Throwable cause) {
		super(message, cause);
	}

}
