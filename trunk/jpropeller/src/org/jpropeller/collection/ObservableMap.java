package org.jpropeller.collection;

import java.util.Map;

import org.jpropeller.bean.Bean;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.MapChange;

/**
 * An {@link ObservableMap} is a {@link Bean} that is also a {@link Map}.
 * It is an unusual (but perfectly valid) {@link Bean} in that it has
 * no props. Instead its state is just the contents of the {@link Map}. As such,
 * when any {@link Map} method is used to change those contents, the {@link ObservableMap}
 * will propagate a {@link Change}. This {@link Change} is specifically a {@link MapChange}
 * giving details of the changes that have occurred. A {@link MapChange} is also
 * propagated when a {@link Bean} contained in the {@link Map} has a {@link Change}.
 * The {@link MapChange} must have {@link MapChange#sameInstances()}
 * set to return false, indicating the list itself has changed.
 * The {@link MapDelta} of the {@link MapChange} MUST be at least "as 
 * large" as the actual change - that is, a mapping in the map is altered, 
 * the {@link MapDelta} must be at least an ALTERATION change for the
 * changed mapping. Overstating the change is still allowable. For some changes, 
 * a COMPLETE map change will have
 * to be used, for example if multiple mappings are 
 * inserted/deleted/changed, e.g. by a clear, etc.
 * 
 * Whenever a DEEP change occurs in the {@link Map}, a {@link MapChange} must be started, 
 * with {@link MapChange#sameInstances()} returning true,
 * with a valid {@link MapDelta} - this should always be either an ALTERATION change
 * covering the map elements that have changed in a deep way,
 * or a COMPLETE change if a more specific range cannot be identified.
 * 
 * Note that DEEP changes are only noticed for mappings to values implementing {@link Bean}
 * 
 * @param <K> 
 * 		The type of keys.
 * @param <V> 
 * 		The type of values.
 *
 */
public interface ObservableMap<K, V> extends Bean, Map<K, V> {
	
	/**
	 * Clear all mappings, then replace them with the new mappings.
	 * This is done as an "atomic" change, so only one large change 
	 * will occur.
	 * @param newContents
	 * 		The new contents
	 */
	public void replace(Map<? extends K, ? extends V> newContents);
}
