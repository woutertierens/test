package org.jpropeller.collection.impl;

import java.util.Collection;

import org.jpropeller.properties.change.Changeable;

/**
 * An exception thrown when a {@link Changeable} {@link Collection} encounters an error at runtime
 * which cannot be reasonably handled (e.g. code errors, errors arising
 * from incorrectly implemented beans, etc.)
 */
public class CCollectionRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 4333467593750354605L;

	/**
	 * Create an exception
	 */
	public CCollectionRuntimeException() {
		super();
	}

	/**
	 * Create an exception
	 * 
	 * @param message 	Description of error
	 * @param cause		Cause of error
	 */
	public CCollectionRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Create an exception
	 * 
	 * @param message 	Description of error
	 */
	public CCollectionRuntimeException(String message) {
		super(message);
	}

	/**
	 * Create an exception
	 * 
	 * @param cause		Cause of error
	 */
	public CCollectionRuntimeException(Throwable cause) {
		super(cause);
	}

}
