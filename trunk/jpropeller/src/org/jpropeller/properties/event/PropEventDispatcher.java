package org.jpropeller.properties.event;

import java.util.List;

/**
 * Handles the actual dispatching of events to the view layer.
 * This delivers a list of events stored during an update of the bean/prop system
 * @author shingoki
 */
public interface PropEventDispatcher {

	/**
	 * Deliver a list of events
	 * @param dispatches
	 * 		The events to deliver, and the listeners to which to deliver them
	 */
	public void dispatch(List<PropEventDispatch> dispatches);
	
}
