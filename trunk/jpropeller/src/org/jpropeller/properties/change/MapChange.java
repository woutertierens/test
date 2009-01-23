package org.jpropeller.properties.change;

import java.util.List;

import org.jpropeller.collection.MapDelta;
import org.jpropeller.collection.ObservableMap;

/**
 * A {@link Change} applying to a {@link ObservableMap}, and hence
 * having a list of {@link MapDelta}s.
 * Must return {@link ChangeType#MAP} from {@link #type()}
 */
public interface MapChange extends Change {

	/**
	 * Get a list of the deltas made to the map, in the order
	 * they were made - these give the
	 * mappings changed, type of change etc.
	 * @return
	 * 		The {@link MapDelta}
	 */
	public List<MapDelta> getMapDeltas();
	
}
