package org.jpropeller.properties.map.impl;

import java.util.Map;

import org.jpropeller.calculation.Calculation;
import org.jpropeller.collection.CMap;
import org.jpropeller.collection.impl.CListCalculated;
import org.jpropeller.collection.impl.CMapCalculated;
import org.jpropeller.collection.impl.CMapDefault;
import org.jpropeller.info.PropAccessType;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.changeable.impl.ChangeablePropDefault;
import org.jpropeller.properties.list.impl.ListPropDefault;
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
	 * @param keyClass
	 * 		The class of key in the map
	 * @param valueClass
	 * 		The class of value in the map
	 * @param name 
	 * 		The string value of the property name
	 * @param data
	 * 		The data to contain
	 * @param <K> 
	 * 		The type of key in the map/indexed property
	 * @param <T> 
	 * 		The type of data in the map/indexed property
	 * @return
	 * 		The new {@link MapPropDefault}
	 */
	public static <K, T> MapPropDefault<K, T> editable(Class<K> keyClass, Class<T> valueClass, String name, CMap<K, T> data) {
		return new MapPropDefault<K, T>(PropName.<K,T>createMap(keyClass, valueClass, name), data, AcceptProcessor.<CMap<K, T>>get());
	}

	/**
	 * Create a new read-only {@link ListPropDefault}
	 * based on a {@link CListCalculated}
	 * @param keyClass			The class of key in the map
	 * @param valueClass		The class of value in the map
	 * @param name 				The string value of the property name
	 * @param calculation		The {@link Calculation} giving list contents
	 * @param <K> 				The type of key in the map
	 * @param <V> 				The type of value in the map
	 * @return					The new {@link ListPropDefault}
	 */
	public static <K, V> MapPropDefault<K, V> calculated(Class<K> keyClass, Class<V> valueClass, String name, Calculation<Map<K, V>> calculation) {
		CMapCalculated<K, V> calculatedMap = 
			new CMapCalculated<K, V>(calculation);
		return create(keyClass, valueClass, name, calculatedMap);
	}
	
	/**
	 * Create a new {@link MapPropDefault}, which will
	 * always accept new values.
	 * @param keyClass
	 * 		The class of key in the map
	 * @param valueClass
	 * 		The class of value in the map
	 * @param name 
	 * 		The string value of the property name
	 * @param <K> 
	 * 		The type of key in the map/indexed property
	 * @param <T> 
	 * 		The type of data in the map/indexed property
	 * @return
	 * 		The new {@link MapPropDefault}
	 */
	public static <K, T> MapPropDefault<K, T> editable(Class<K> keyClass, Class<T> valueClass, String name) {
		return editable(keyClass, valueClass, name, new CMapDefault<K, T>());
	}
	
	/**
	 * Create a new {@link MapPropDefault}, which is
	 * read only (cannot set a new {@link CMap} value).
	 * @param keyClass
	 * 		The class of key in the map
	 * @param valueClass
	 * 		The class of value in the map
	 * @param name 
	 * 		The string value of the property name
	 * @param data
	 * 		The data to contain
	 * @param <K> 
	 * 		The type of key in the map/indexed property
	 * @param <T> 
	 * 		The type of data in the map/indexed property
	 * @return
	 * 		The new {@link MapPropDefault}
	 */
	public static <K, T> MapPropDefault<K, T> create(Class<K> keyClass, Class<T> valueClass, String name, CMap<K, T> data) {
		return new MapPropDefault<K, T>(PropName.<K,T>createMap(keyClass, valueClass, name), data, ReadOnlyProcessor.<CMap<K, T>>get());
	}

	/**
	 * Create a new {@link MapPropDefault}, which is
	 * read only (cannot set a new {@link CMap} value).
	 * @param keyClass
	 * 		The class of key in the map
	 * @param valueClass
	 * 		The class of value in the map
	 * @param name 
	 * 		The string value of the property name
	 * @param <K> 
	 * 		The type of key in the map/indexed property
	 * @param <T> 
	 * 		The type of data in the map/indexed property
	 * @return
	 * 		The new {@link MapPropDefault}
	 */
	public static <K, T> MapPropDefault<K, T> create(Class<K> keyClass, Class<T> valueClass, String name) {
		return create(keyClass, valueClass, name, new CMapDefault<K, T>());
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
