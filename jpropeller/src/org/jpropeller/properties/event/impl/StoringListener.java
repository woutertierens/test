package org.jpropeller.properties.event.impl;

import java.util.ArrayList;
import java.util.List;

import org.jpropeller.properties.event.PropEvent;
import org.jpropeller.properties.event.PropListener;

/**
 * Stores events received as a {@link PropListener}
 * @author bwebster
 */
public class StoringListener implements PropListener {

	//Store received events, first received at lowest index
	private List<PropEvent<?>> events = new ArrayList<PropEvent<?>>();
	
	@Override
	public <T> void propChanged(PropEvent<T> event) {
		events.add(event);
	}
	
	/**
	 * Get a list of the events received, new events are
	 * added to the end of this list. Please note this
	 * is the actual list to which events are appended.
	 * @return
	 * 		events
	 */
	public List<PropEvent<?>> getEvents() {
		return events;
	}

}
