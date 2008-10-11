package org.jpropeller.properties.map;

import java.util.Map;

import org.jpropeller.map.PropMap;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;

/**
 * A {@link MapProp} that can be edited
 * @author bwebster
 *
 * @param <K>
 *		Type of key into the map
 * @param <T>
 * 		Type of values in the map, also the type of the {@link Prop}
 */
public interface EditableMapProp<K, T> extends MapProp<K, T> {
	
	/**
	 * Set a new property for a given key
	 * {@link Map#put(Object, Object)}
	 * @param key
	 * 		The key
	 * @param value
	 * 		The new value
	 * @return
	 * 		The old value for the key - if this is null it may indicate
	 * either that the old value was null, or that there was no mapping
	 */
	public T put(K key, T value);
	
	/**
	 * The name of the prop
	 * This is used in the {@link PropMap} to look up {@link Prop}s via {@link PropMap#get(PropName)}
	 * @return
	 * 		Name of the prop
	 */
	public PropName<EditableMapProp<K, T>, T> getName();

}