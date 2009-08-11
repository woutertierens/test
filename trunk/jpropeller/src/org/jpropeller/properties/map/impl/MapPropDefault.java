package org.jpropeller.properties.map.impl;

import org.jpropeller.collection.CMap;
import org.jpropeller.collection.impl.CMapDefault;
import org.jpropeller.info.PropAccessType;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.changeable.impl.ChangeablePropDefault;
import org.jpropeller.properties.map.MapProp;
import org.jpropeller.properties.values.ValueProcessor;
import org.jpropeller.properties.values.impl.AcceptProcessor;
import org.jpropeller.properties.values.impl.ReadOnlyProcessor;

/**
 * An implementation of a {@link MapProp}
 * @param <K> 
 *		The type of key in the map/prop
 * @param <T>
 * 		The type of value in the map/prop
 */
public class MapPropDefault<K, T> extends ChangeablePropDefault<CMap<K,T>> implements MapProp<K, T> {

	/**
	 * Create a {@link MapProp} with a specified initial
	 * {@link CMap} value.
	 * @param cmap
	 * 		The wrapped map
	 */
	private MapPropDefault(PropName<CMap<K, T>> name, CMap<K, T> cmap, ValueProcessor<CMap<K, T>> processor) {
		super(name, cmap, processor);
	}

	/**
	 * Create a new {@link MapPropDefault}, which will
	 * always accept new values.
	 * @param <K> 
	 * 		The type of key in the map/indexed property
	 * @param <T> 
	 * 		The type of data in the map/indexed property
	 * @param name 
	 * 		The string value of the property name
	 * @param keyClass
	 * 		The class of key in the map
	 * @param valueClass
	 * 		The class of value in the map
	 * @param data
	 * 		The data to contain
	 * @return
	 * 		The new {@link MapPropDefault}
	 */
	public static <K, T> MapPropDefault<K, T> editable(String name, Class<K> keyClass, Class<T> valueClass, CMap<K, T> data) {
		return new MapPropDefault<K, T>(PropName.<K,T>createMap(name, keyClass, valueClass), data, AcceptProcessor.<CMap<K, T>>get());
	}

	/**
	 * Create a new {@link MapPropDefault}, which will
	 * always accept new values.
	 * @param <K> 
	 * 		The type of key in the map/indexed property
	 * @param <T> 
	 * 		The type of data in the map/indexed property
	 * @param name 
	 * 		The string value of the property name
	 * @param keyClass
	 * 		The class of key in the map
	 * @param valueClass
	 * 		The class of value in the map
	 * @return
	 * 		The new {@link MapPropDefault}
	 */
	public static <K, T> MapPropDefault<K, T> editable(String name, Class<K> keyClass, Class<T> valueClass) {
		return editable(name, keyClass, valueClass, new CMapDefault<K, T>());
	}
	
	/**
	 * Create a new {@link MapPropDefault}, which is
	 * read only (cannot set a new {@link CMap} value).
	 * @param <K> 
	 * 		The type of key in the map/indexed property
	 * @param <T> 
	 * 		The type of data in the map/indexed property
	 * @param name 
	 * 		The string value of the property name
	 * @param keyClass
	 * 		The class of key in the map
	 * @param valueClass
	 * 		The class of value in the map
	 * @param data
	 * 		The data to contain
	 * @return
	 * 		The new {@link MapPropDefault}
	 */
	public static <K, T> MapPropDefault<K, T> create(String name, Class<K> keyClass, Class<T> valueClass, CMap<K, T> data) {
		return new MapPropDefault<K, T>(PropName.<K,T>createMap(name, keyClass, valueClass), data, ReadOnlyProcessor.<CMap<K, T>>get());
	}

	/**
	 * Create a new {@link MapPropDefault}, which is
	 * read only (cannot set a new {@link CMap} value).
	 * @param <K> 
	 * 		The type of key in the map/indexed property
	 * @param <T> 
	 * 		The type of data in the map/indexed property
	 * @param name 
	 * 		The string value of the property name
	 * @param keyClass
	 * 		The class of key in the map
	 * @param valueClass
	 * 		The class of value in the map
	 * @return
	 * 		The new {@link MapPropDefault}
	 */
	public static <K, T> MapPropDefault<K, T> create(String name, Class<K> keyClass, Class<T> valueClass) {
		return create(name, keyClass, valueClass, new CMapDefault<K, T>());
	}
	
	@Override
	public PropAccessType getAccessType() {
		return PropAccessType.MAP;
	}

	@Override
	public String toString() {
		return "Map Prop '" + getName().getString() + "' = '" + get() + "'";
	}

	//Map-style methods delegated to map
	
	@Override
	public T put(K key, T value) {
		return this.value.put(key, value);
	}

	@Override
	public T get(K key) {
		return this.value.get(key);
	}

	@Override
	public int size() {
		return this.value.size();
	}

	@Override
	public boolean containsKey(K key) {
		return this.value.containsKey(key);
	}

}
