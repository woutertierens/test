package org.jpropeller.collection;

import java.util.Map;

import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.MapChange;

/**
 * An {@link CMap} is a {@link Map} that implements {@link Changeable}
 * to allow tracking of changes to the {@link Map} or its contents.
 * Note that by default implementations may choose to guarantee that 
 * only the VALUES are tracked in a deep way - keys are expected
 * to be immutable, since mutable values do not generally work as good
 * map keys.
 * When any {@link Map} method is used to change the {@link Map} contents, 
 * the {@link CMap} will propagate a {@link Change}. 
 * This {@link Change} is specifically a {@link MapChange}
 * giving details of the changes that have occurred. A {@link MapChange} is also
 * propagated when a {@link Changeable} contained in the {@link Map} has a {@link Change}.
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
 * Note that DEEP changes are only noticed for mappings to values implementing {@link Changeable}
 * 
 * @param <K> 
 * 		The type of keys.
 * @param <V> 
 * 		The type of values.
 *
 */
public interface CMap<K, V> extends Changeable, Map<K, V> {
	
	/**
	 * Clear all mappings, then replace them with the new mappings.
	 * This is done as an "atomic" change, so only one large change 
	 * will occur.
	 * @param newContents
	 * 		The new contents
	 */
	public void replace(Map<? extends K, ? extends V> newContents);
}
