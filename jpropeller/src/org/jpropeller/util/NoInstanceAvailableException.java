package org.jpropeller.util;

/**
 * A {@link RuntimeException} thrown when a source/factory has no
 * instance available
 */
public class NoInstanceAvailableException extends RuntimeException {

	private final boolean userVisibleMessage;
	
	/**
	 * Create a {@link NoInstanceAvailableException}
	 */
	public NoInstanceAvailableException() {
		super();
		this.userVisibleMessage = false;
	}

	/**
	 * Create a {@link NoInstanceAvailableException}
	 * 
	 * @param message
	 */
	public NoInstanceAvailableException(String message) {
		super(message);
		this.userVisibleMessage = false;
	}

	/**
	 * Create a {@link NoInstanceAvailableException}
	 * 
	 * @param message				User-visible message
	 * @param userVisibleMessage 	If true, the message is suitable to be displayed to user to
	 * 								explain why no instance is available.
	 */
	public NoInstanceAvailableException(String message, boolean userVisibleMessage) {
		super(message);
		this.userVisibleMessage = userVisibleMessage;
	}
	
	/**
	 * Create a {@link NoInstanceAvailableException}
	 * 
	 * @param cause
	 */
	public NoInstanceAvailableException(Throwable cause) {
		super(cause);
		this.userVisibleMessage = false;
	}

	/**
	 * Create a {@link NoInstanceAvailableException}
	 * 
	 * @param message
	 * @param cause
	 */
	public NoInstanceAvailableException(String message, Throwable cause) {
		super(message, cause);
		this.userVisibleMessage = false;
	}

	/**
	 * Create a {@link NoInstanceAvailableException}
	 * 
	 * @param message
	 * @param cause
	 * @param userVisibleMessage 	If true, the message is suitable to be displayed to user to
	 * 								explain why no instance is available.
	 */
	public NoInstanceAvailableException(String message, Throwable cause, boolean userVisibleMessage) {
		super(message, cause);
		this.userVisibleMessage = userVisibleMessage;
	}

	/**
	 * If true, the message is suitable to be displayed to user to
	 * explain why no instance is available.
	 * @return userVisibleMessage
	 */
	public boolean isUserVisibleMessage() {
		return userVisibleMessage;
	}
	
}
