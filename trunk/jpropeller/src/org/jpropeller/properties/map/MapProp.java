package org.jpropeller.properties.map;

import java.util.Map;

import org.jpropeller.collection.CMap;
import org.jpropeller.properties.Prop;

/**
 * A {@link Prop} containing a {@link CMap} value, and
 * having extra methods to access methods of that
 * {@link CMap} directly
 *
 * @param <K>
 *		Type of key into the map
 * @param <T>
 * 		Type of values in the map, also the type of the {@link Prop}
 */
public interface MapProp<K, T> extends Prop<CMap<K, T>> {
	
	/**
	 * Get the property value for a given key
	 * {@link Map#get(Object)}
	 * @param key
	 * 		The key
	 * @return
	 * 		The mapped property value
	 */
	public T get(K key);
	
	/**
	 * Get the number of elements indexed by this property.
	 * {@link Map#size()}
	 * @return
	 * 		The number of elements.
	 */
	public int size();
	
	/**
	 * Check whether a value is contained for a given key
	 * {@link Map#containsKey(Object)}
	 * @param key
	 * 		The key to check
	 * @return
	 * 		True if the map contains a value for the key, false otherwise
	 */
	public boolean containsKey(K key);
	
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
	
}