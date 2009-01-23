package org.jpropeller.properties.list.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jpropeller.collection.ObservableList;
import org.jpropeller.collection.impl.ObservableListDefault;
import org.jpropeller.info.PropInfo;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.ChangeableFeatures;
import org.jpropeller.properties.change.impl.ChangeDefault;
import org.jpropeller.properties.change.impl.ChangeableFeaturesDefault;
import org.jpropeller.properties.change.impl.InternalChangeImplementation;
import org.jpropeller.properties.list.EditableListProp;

/**
 * An implementation of an {@link EditableListProp} that uses an {@link ObservableList}
 * to back the data.
 * @author shingoki
 *
 * @param <T>
 * 		The type of value in the prop
 */
public class EditableListPropDefault<T> implements EditableListProp<T> {

	ChangeableFeatures features;
	private ObservableList<T> list;
	PropName<EditableListProp<T>, T> name;
	
	/**
	 * Create a prop
	 * Please note that you must NOT modify the data list after creating
	 * this prop. If you do, those changes will not be visible to any
	 * listeners watching the prop. 
	 * @param data
	 * 		The wrapped list
	 */
	private EditableListPropDefault(PropName<EditableListProp<T>, T> name, List<T> data) {
		features = new ChangeableFeaturesDefault(new InternalChangeImplementation() {
			@Override
			public Change internalChange(Changeable changed, Change change,
					List<Changeable> initial, Map<Changeable, Change> changes) {
				boolean listSameInstances = changes.get(list).sameInstances();
				//We only listen to our current ObservableList (Bean) value, when it has a change we have a consequent change
				return ChangeDefault.instance(
						false,	//Not initial 
						listSameInstances	//We have same instances if the list has same instances
						);
			}
		}, this); 

		
		this.name = name;
		this.list = new ObservableListDefault<T>(data);
		list.features().addChangeableListener(this);
	}

	/**
	 * Create a new {@link EditableListPropDefault}
	 * @param name 
	 * 		The string value of the property name
	 * @param clazz
	 * 		The class of data in the list/indexed property
	 * @param <S> 
	 * 		The type of data in the list/indexed property
	 * @param data
	 * 		The data to contain
	 * @return
	 * 		The new {@link EditableListPropDefault}
	 */
	public static <S> EditableListPropDefault<S> create(String name, Class<S> clazz, List<S> data) {
		return new EditableListPropDefault<S>(PropName.editableList(name, clazz), data);
	}

	/**
	 * Create a new {@link EditableListPropDefault}
	 * @param name 
	 * 		The string value of the property name
	 * @param clazz
	 * 		The class of data in the list/indexed property
	 * @param <S> 
	 * 		The type of data in the list/indexed property
	 * @return
	 * 		The new {@link EditableListPropDefault}
	 */
	public static <S> EditableListPropDefault<S> create(String name, Class<S> clazz) {
		return new EditableListPropDefault<S>(PropName.editableList(name, clazz), null);
	}
	
	public PropName<EditableListProp<T>, T> getName() {
		return name;
	}
	
	@Override
	public ChangeableFeatures features() {
		return features;
	}

	@Override
	public PropInfo getInfo() {
		return PropInfo.EDITABLE_LIST;
	}

	@Override
	public String toString() {
		return "Editable List Prop '" + getName().getString() + "' = '" + get() + "'";
	}
	
	//List-style methods, delegated to list
	
	@Override
	public T set(int key, T value) {
		return list.set(key, value);
	}

	@Override
	public T get(int index) {
		return list.get(index);
	}	

	@Override
	public ObservableList<T> get() {
		return list;
	}
	
	@Override
	public int size() {
		return list.size();
	}

	@Override
	public void add(T value) {
		list.add(value);
	}

	@Override
	public void replace(Iterable<T> newContents) {
		list.replace(newContents);
	}

	@Override
	public Iterator<T> iterator() {
		return list.iterator();
	}

}
