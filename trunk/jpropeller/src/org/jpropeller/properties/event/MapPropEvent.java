package org.jpropeller.properties.event;

import org.jpropeller.collection.MapChange;
import org.jpropeller.info.PropAccessType;

/**
 * A PropEvent for a prop change in a MapBean
 * 
 * Implementations must return {@link PropAccessType#MAP} from {@link #getType()}
 * 
 * @author shingoki
 * @param <K> 
 *		The type of keys in the map.
 * @param <T>
 * 		The type of Prop that has changed
 */
public interface MapPropEvent<K, T> extends PropEvent<T> {

	/**
	 * Get a map change describing the scope of the changes to the map
	 * @return
	 * 		The MapChange
	 */
	public MapChange<K> getMapChange();
	
}
