package org.jpropeller.view.list.impl;

/**
 * Exception thrown when a ListBeanAddFilter rejects the
 * addition of a new element, giving reasons for rejection
 * @author itis
 */
public class ListBeanAddException extends Exception {
	private static final long serialVersionUID = -4001100623006021673L;
	
	String objection;

	/**
	 * Make an exception with only objection message
	 * @param objection
	 * 		The objection to making the change. This should
	 * be worded so as to fit into a list of one or more objections,
	 * formatted as:
	 * <br>
	 * Change cannot be made because:
	 * <br>
	 * Objection 1
	 * <br>
	 * Objection 2
	 * <br>
	 * ...
	 * <br>
	 * "OK Button"
	 */
	public ListBeanAddException(String objection) {
		super();
		this.objection = objection;
	}

	/**
	 * Make an exception
	 * @param objection
	 * 		The objection to making the change. This should
	 * be worded so as to fit into a list of one or more objections,
	 * formatted as:
	 * <br>
	 * Change cannot be made because:
	 * <br>
	 * Objection 1
	 * <br>
	 * Objection 2
	 * <br>
	 * ...
	 * <br>
	 * "OK Button"
	 * @param message
	 * 		The exception message - not for presentation to user, can
	 * carry technical detail about exception
	 * @param cause
	 * 		Cause of this exception - e.g. an exception encountered while
	 * trying to modify the instance in some way
	 */
	public ListBeanAddException(String objection, String message, Throwable cause) {
		super(message, cause);
		this.objection = objection;
	}

	/**
	 * Make an exception
	 * @param objection
	 * 		The objection to making the change. This should
	 * be worded so as to fit into a list of one or more objections,
	 * formatted as:
	 * <br>
	 * Change cannot be made because:
	 * <br>
	 * Objection 1
	 * <br>
	 * Objection 2
	 * <br>
	 * ...
	 * <br>
	 * "OK Button"
	 * @param message
	 * 		The exception message - not for presentation to user, can
	 * carry technical detail about exception
	 */
	public ListBeanAddException(String objection, String message) {
		super(message);
		this.objection = objection;
	}

	/**
	 * Make an exception
	 * @param objection
	 * 		The objection to making the change. This should
	 * be worded so as to fit into a list of one or more objections,
	 * formatted as:
	 * <br>
	 * Change cannot be made because:
	 * <br>
	 * Objection 1
	 * <br>
	 * Objection 2
	 * <br>
	 * ...
	 * <br>
	 * "OK Button"
	 * @param cause
	 * 		Cause of this exception - e.g. an exception encountered while
	 * trying to modify the instance in some way
	 */
	public ListBeanAddException(String objection, Throwable cause) {
		super(cause);
		this.objection = objection;
	}

	/**
	 * 		The objection to making the change. This should
	 * be worded so as to fit into a list of one or more objections,
	 * formatted as:
	 * <br>
	 * Change cannot be made because:
	 * <br>
	 * Objection 1
	 * <br>
	 * Objection 2
	 * <br>
	 * ...
	 * <br>
	 * "OK Button"
	 * 
	 * @return
	 * 		Objection
	 */
	public String getObjection() {
		return objection;
	}
	
}
