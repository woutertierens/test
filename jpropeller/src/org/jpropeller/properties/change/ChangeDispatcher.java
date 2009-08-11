package org.jpropeller.properties.change;

import java.util.List;
import java.util.Map;


/**
 * Sends changes to a set of {@link ChangeListener}s
 */
public interface ChangeDispatcher {

	/**
	 * Set the source for future dispatches
	 * @param source
	 * 		Source of changes to dispatch
	 */
	public void setSource(ChangeDispatchSource source);
	
	/**
	 * Call {@link ChangeListener#change(List, Map)} on a list of listeners, passing
	 * given parameters. 
	 * Note that this is NOT required to dispatch the changes instantly, but must dispatch
	 * all current changes at some point in the future.
	 */
	public void dispatch();

}