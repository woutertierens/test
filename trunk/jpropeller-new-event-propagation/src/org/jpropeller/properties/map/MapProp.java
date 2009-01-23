package org.jpropeller.properties.map;

import java.util.Map;

import org.jpropeller.bean.BeanFeatures;
import org.jpropeller.collection.ObservableMap;
import org.jpropeller.collection.impl.ObservableMapDefault;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.Prop;

/**
 * A {@link Prop} with zero or more values indexed by keys,
 * that can be viewed as an {@link ObservableMapDefault}
 * 
 * @author bwebster
 *
 * @param <K>
 *		Type of key into the map
 * @param <T>
 * 		Type of values in the map, also the type of the {@link Prop}
 */
public interface MapProp<K, T> extends GeneralProp<T>{
	
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
	 * Get the whole map of values of the property as an {@link ObservableMapDefault}
	 * @return
	 * 		values as a list
	 */
	public ObservableMap<K, T> get();
	
	/**
	 * The name of the prop
	 * This is used in the {@link BeanFeatures} to look up {@link Prop}s via {@link BeanFeatures#get(PropName)}
	 * @return
	 * 		Name of the prop
	 */
	public PropName<? extends MapProp<K, T>, T> getName();

}