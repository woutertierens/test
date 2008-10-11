package org.jpropeller.properties.event;

/**
 * Stores the dispatch of an event - the event itself,
 * and the listener to which it should be delivered
 * @author shingoki
 */
public interface PropEventDispatch {

	/**
	 * The listener to which the event should be delivered
	 * @return
	 * 		listener
	 */
	public PropListener getListener();
	
	/**
	 * The event that should be delivered
	 * @return
	 * 		event
	 */
	public PropEvent<?> getEvent();
	
}
