package org.jpropeller.undo.delegates;

import org.jpropeller.properties.change.Changeable;

/**
 * An {@link Exception} thrown when an {@link UndoDelegateSource} cannot
 * find an {@link UndoDelegate} for a given {@link Changeable}
 */
public class UndoDelegateSourceException extends Exception {

	/**
	 * Create an exception
	 */
	public UndoDelegateSourceException() {
		super();
	}

	/**
	 * Create an exception
	 * 
	 * @param message
	 * @param cause
	 */
	public UndoDelegateSourceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Create an exception
	 * 
	 * @param message
	 */
	public UndoDelegateSourceException(String message) {
		super(message);
	}

	/**
	 * Create an exception
	 * 
	 * @param cause
	 */
	public UndoDelegateSourceException(Throwable cause) {
		super(cause);
	}

}
