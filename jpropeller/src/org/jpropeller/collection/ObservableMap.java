package org.jpropeller.collection;

import java.util.Map;

import org.jpropeller.bean.Bean;
import org.jpropeller.map.PropMap;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.event.MapPropEvent;

/**
 * An {@link ObservableMap} is a {@link Bean} with a single property, a {@link Prop} of type {@link Long}
 * called "modifications", which must be accessible in the {@link Bean} {@link PropMap},
 * and also via a method {@link #modifications()}
 * 
 * The modifications property must return the number of modifications via
 * calling of {@link Map} methods or by internal changes, since the {@link Map} was created.
 * 
 * Whenever the contents of the {@link Map} are directly changed, either internally or via
 * {@link Map} methods, the modifications property must be incremented, and then 
 * a shallow {@link MapPropEvent} fired showing the change, and also carrying a {@link MapChange}
 * describing the change to the {@link Map}. This {@link MapChange} MUST be at least "as 
 * large" as the actual change - that is, if some elements of the {@link Map} have
 * changed, the {@link MapChange} must be at least an ALTERATION change covering all
 * changed elements (and possibly some non-changed elements - overstating
 * the change is allowable). For some changes, a COMPLETE map change will have
 * to be used, for example if multiple non-contiguous elements are 
 * inserted/deleted/changed
 * 
 * Whenever a DEEP change occurs in the {@link Map}, a DEEP {@link MapPropEvent} must be fired
 * with a valid {@link MapChange} - this should always be either an ALTERATION change
 * covering the mappings that have changed in a deep way,
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
	 * A Property holding the number of modifications made to the map,
	 * this is also the number of PropEvents this Prop has fired
	 * @return
	 * 		modification count
	 */
	public Prop<Long> modifications();
	
}
