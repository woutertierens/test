package org.jpropeller.collection.impl;

/**
 * An exception thrown when an observable collection encounters an error at runtime
 * which cannot be reasonably handled (e.g. code errors, errors arising
 * from incorrectly implemented beans, etc.)
 * 
 * @author shingoki
 */
public class ObservableCollectionRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 4333467593750354605L;

	/**
	 * Create an exception
	 */
	public ObservableCollectionRuntimeException() {
		super();
	}

	/**
	 * Create an exception
	 * @param message
	 * @param cause
	 */
	public ObservableCollectionRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Create an exception
	 * @param message
	 */
	public ObservableCollectionRuntimeException(String message) {
		super(message);
	}

	/**
	 * Create an exception
	 * @param cause
	 */
	public ObservableCollectionRuntimeException(Throwable cause) {
		super(cause);
	}

}
