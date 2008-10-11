package org.jpropeller.properties.event.impl;

import java.util.List;

import org.jpropeller.properties.event.PropEventDispatch;
import org.jpropeller.properties.event.PropEventDispatcher;

/**
 * Simplest implementation of {@link PropEventDispatcher}, this just directly
 * delivers events.
 * @author shingoki
 */
public class PropEventDispatcherDefault implements PropEventDispatcher {

	@Override
	public void dispatch(List<PropEventDispatch> dispatches) {
		for (PropEventDispatch dispatch : dispatches) {
			dispatch.getListener().propChanged(dispatch.getEvent());
		}
	}

}
