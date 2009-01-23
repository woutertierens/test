package org.jpropeller.properties.map.impl;

import java.util.List;
import java.util.Map;

import org.jpropeller.collection.ObservableMap;
import org.jpropeller.collection.impl.ObservableMapDefault;
import org.jpropeller.info.PropInfo;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.ChangeableFeatures;
import org.jpropeller.properties.change.impl.ChangeDefault;
import org.jpropeller.properties.change.impl.ChangeableFeaturesDefault;
import org.jpropeller.properties.change.impl.InternalChangeImplementation;
import org.jpropeller.properties.map.EditableMapProp;

/**
 * An implementation of an {@link EditableMapProp} that uses an {@link ObservableMap}
 * to back the data
 * @author shingoki
 * @param <K> 
 *		The type of key in the map/prop
 * @param <T>
 * 		The type of value in the map/prop
 */
public class EditableMapPropDefault<K, T> implements EditableMapProp<K, T> {

	ChangeableFeatures features;
	private ObservableMap<K, T> map;
	PropName<EditableMapProp<K, T>, T> name;
	
	/**
	 * Create a prop
	 * Please note that you must NOT modify the data map after creating
	 * this prop. If you do, those changes will not be visible to any
	 * listeners watching the prop. 
	 * @param data
	 * 		The wrapped map
	 */
	private EditableMapPropDefault(PropName<EditableMapProp<K, T>, T> name, Map<K, T> data) {
		features = new ChangeableFeaturesDefault(new InternalChangeImplementation() {
			@Override
			public Change internalChange(Changeable changed, Change change,
					List<Changeable> initial, Map<Changeable, Change> changes) {
				boolean mapSameInstances = changes.get(map).sameInstances();
				//We only listen to our current ObservableMap (Bean) value, when it has a change we have a consequent change
				return ChangeDefault.instance(
						false,	//Not initial 
						//FIXME need to look at correct behaviour - do we consider an instance change in map to be an instance change in this prop? probably
						mapSameInstances	//We have same instances if the map has same instances
						);
			}
		}, this); 
		
		this.name = name;
		this.map = new ObservableMapDefault<K, T>(data);
		map.features().addChangeableListener(this);
	}

	/**
	 * Create a new {@link EditableMapPropDefault}
	 * @param <K> 
	 * 		The type of key in the map/indexed property
	 * @param <T> 
	 * 		The type of data in the map/indexed property
	 * @param name 
	 * 		The string value of the property name
	 * @param clazz
	 * 		The class of data in the list/indexed property
	 * @param data
	 * 		The data to contain
	 * @return
	 * 		The new {@link EditableMapPropDefault}
	 */
	public static <K, T> EditableMapPropDefault<K, T> create(String name, Class<T> clazz, Map<K, T> data) {
		return new EditableMapPropDefault<K, T>(PropName.<K,T>editableMap(name, clazz), data);
	}

	/**
	 * Create a new {@link EditableMapPropDefault}
	 * @param <K> 
	 * 		The type of key in the map/indexed property
	 * @param <T> 
	 * 		The type of data in the map/indexed property
	 * @param name 
	 * 		The string value of the property name
	 * @param clazz
	 * 		The class of data in the list/indexed property
	 * @return
	 * 		The new {@link EditableMapPropDefault}
	 */
	public static <K, T> EditableMapPropDefault<K, T> create(String name, Class<T> clazz) {
		return new EditableMapPropDefault<K, T>(PropName.<K,T>editableMap(name, clazz), null);
	}
	
	public PropName<EditableMapProp<K, T>, T> getName() {
		return name;
	}
	
	@Override
	public ChangeableFeatures features() {
		return features;
	}

	@Override
	public PropInfo getInfo() {
		return PropInfo.EDITABLE_MAP;
	}

	@Override
	public String toString() {
		return "Editable Map Prop '" + getName().getString() + "' = '" + get() + "'";
	}

	//Map-style methods delegated to map
	
	@Override
	public T put(K key, T value) {
		return map.put(key, value);
	}

	@Override
	public T get(K key) {
		return map.get(key);
	}

	@Override
	public ObservableMap<K, T> get() {
		return map;
	}
	
	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean containsKey(K key) {
		return map.containsKey(key);
	}

}
