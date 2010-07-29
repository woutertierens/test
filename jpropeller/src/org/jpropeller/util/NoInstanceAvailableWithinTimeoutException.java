package org.jpropeller.util;

/**
 * A {@link RuntimeException} thrown when a source/factory has no
 * instance available within a timeout (but may have an instance
 * given more time).
 */
public class NoInstanceAvailableWithinTimeoutException extends RuntimeException {

	/**
	 * Create a {@link NoInstanceAvailableWithinTimeoutException}
	 */
	public NoInstanceAvailableWithinTimeoutException() {
		super();
	}

	/**
	 * Create a {@link NoInstanceAvailableWithinTimeoutException}
	 * 
	 * @param message
	 */
	public NoInstanceAvailableWithinTimeoutException(String message) {
		super(message);
	}

	/**
	 * Create a {@link NoInstanceAvailableWithinTimeoutException}
	 * 
	 * @param cause
	 */
	public NoInstanceAvailableWithinTimeoutException(Throwable cause) {
		super(cause);
	}

	/**
	 * Create a {@link NoInstanceAvailableWithinTimeoutException}
	 * 
	 * @param message
	 * @param cause
	 */
	public NoInstanceAvailableWithinTimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

}
