package org.jpropeller.util;

/**
 * A {@link RuntimeException} thrown when a source/factory has no
 * instance available
 */
public class NoInstanceAvailableException extends RuntimeException {

	/**
	 * Create a {@link NoInstanceAvailableException}
	 */
	public NoInstanceAvailableException() {
		super();
	}

	/**
	 * Create a {@link NoInstanceAvailableException}
	 * 
	 * @param message
	 */
	public NoInstanceAvailableException(String message) {
		super(message);
	}

	/**
	 * Create a {@link NoInstanceAvailableException}
	 * 
	 * @param cause
	 */
	public NoInstanceAvailableException(Throwable cause) {
		super(cause);
	}

	/**
	 * Create a {@link NoInstanceAvailableException}
	 * 
	 * @param message
	 * @param cause
	 */
	public NoInstanceAvailableException(String message, Throwable cause) {
		super(message, cause);
	}

}
