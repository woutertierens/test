package org.jpropeller.properties.event.impl;

import org.jpropeller.collection.ListChange;
import org.jpropeller.info.PropAccessType;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.event.ListPropEvent;
import org.jpropeller.properties.event.PropEvent;
import org.jpropeller.properties.event.PropEventOrigin;

/**
 * Default implementation of {@link ListPropEvent}
 * @author shingoki
 *
 * @param <T>
 *	The type of the Prop that has changed
 */
public class ListPropEventDefault<T> extends PropEventDefault<T> implements ListPropEvent<T> {

	ListChange listChange;
	
	/**
	 * Create a deep change. This is a change that occurs as a 
	 * result of another change to a child of the prop, or a child of
	 * a child of the prop, etc.
	 * Only the event that caused this event is required to construct
	 * the other "deep" properties of the event
	 * Note that since the prop itself has not changed in a shallow way,
	 * the old and new values will both be set to the current value of the
	 * prop
	 * @param prop
	 * 		The property that has changed
	 * @param causeEvent
	 * 		The event that caused this event
	 * @param listChange
	 * 		The change in the list
	 */
	public ListPropEventDefault(Prop<T> prop, PropEvent<?> causeEvent, ListChange listChange) {
		super(prop, causeEvent);
		this.listChange = listChange;
	}

	/**
	 * Create a shallow ListPropEvent. This indicates that
	 * the prop has had set(value) called directly,
	 * and so the event is NOT the result of a deep
	 * change.
	 * 
	 * deep will be set to false, and causeEvent/rootEvent
	 * set to null
	 * 
	 * @param prop
	 * 		The property that has changed
	 * @param rootOrigin
	 * 		The root origin of the change - user, consequence, etc.
	 * @param listChange
	 * 		The change in the list
	 */
	public ListPropEventDefault(Prop<T> prop, PropEventOrigin rootOrigin, ListChange listChange) {
		super(prop, rootOrigin);
		this.listChange = listChange;
	}

	@Override
	public ListChange getListChange() {
		return listChange;
	}
	
	@Override
	public String toString() {
		String s = super.toString() + ", List Details " + listChange;
		return s;
	}

	@Override
	public PropAccessType getType() {
		return PropAccessType.LIST;
	}
}
