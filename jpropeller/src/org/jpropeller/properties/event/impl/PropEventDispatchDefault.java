package org.jpropeller.properties.event.impl;

import org.jpropeller.properties.event.PropEvent;
import org.jpropeller.properties.event.PropEventDispatch;
import org.jpropeller.properties.event.PropListener;

/**
 * Default immutable implementation of {@link PropEventDispatch}
 * @author shingoki
 */
public class PropEventDispatchDefault implements PropEventDispatch {

	PropEvent<?> event;
	PropListener listener;

	/**
	 * Create a new {@link PropEventDispatchDefault}
	 * @param event
	 * 		The event that should be delivered
	 * @param listener
	 * 		The listener to which the event should be delivered
	 */
	public PropEventDispatchDefault(PropEvent<?> event, PropListener listener) {
		super();
		this.event = event;
		this.listener = listener;
	}

	@Override
	public PropEvent<?> getEvent() {
		return event;
	}

	@Override
	public PropListener getListener() {
		return listener;
	}

}
