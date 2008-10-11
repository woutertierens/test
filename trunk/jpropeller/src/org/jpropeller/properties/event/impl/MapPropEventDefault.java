package org.jpropeller.properties.event.impl;

import org.jpropeller.collection.MapChange;
import org.jpropeller.info.PropAccessType;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.event.ListPropEvent;
import org.jpropeller.properties.event.MapPropEvent;
import org.jpropeller.properties.event.PropEvent;
import org.jpropeller.properties.event.PropEventOrigin;

/**
 * Default implementation of {@link ListPropEvent}
 * @author shingoki
 * @param <K> 
 *		The type of keys in the map.
 * @param <T>
 *	The type of the Prop that has changed
 */
public class MapPropEventDefault<K, T> extends PropEventDefault<T> implements
		MapPropEvent<K, T> {

	MapChange<K> mapChange;
	
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
	 * @param mapChange 
	 * 		The map change.
	 */
	public MapPropEventDefault(Prop<T> prop, PropEvent<?> causeEvent, MapChange<K> mapChange) {
		super(prop, causeEvent);
		this.mapChange = mapChange;
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
	 * @param mapChange 
	 * 		The change in the map
	 */
	public MapPropEventDefault(Prop<T> prop, PropEventOrigin rootOrigin, MapChange<K> mapChange) {
		super(prop, rootOrigin);
		this.mapChange = mapChange;
	}

	@Override
	public String toString() {
		String s = super.toString() + ", Map Details " + mapChange;
		return s;
	}

	@Override
	public MapChange<K> getMapChange() {
		return mapChange;
	}

	@Override
	public PropAccessType getType() {
		return PropAccessType.MAP;
	}
}
