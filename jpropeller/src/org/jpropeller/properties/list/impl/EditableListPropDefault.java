package org.jpropeller.properties.list.impl;

import java.util.List;

import org.jpropeller.bean.Bean;
import org.jpropeller.collection.ObservableList;
import org.jpropeller.collection.impl.ObservableListDefault;
import org.jpropeller.info.PropInfo;
import org.jpropeller.map.PropMap;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.event.PropEvent;
import org.jpropeller.properties.event.PropInternalListener;
import org.jpropeller.properties.event.impl.PropEventDefault;
import org.jpropeller.properties.list.EditableListProp;

/**
 * An implementation of an {@link EditableListProp} that uses an {@link ObservableList}
 * to back the data
 * @author mworcester
 *
 * @param <T>
 * 		The type of value in the prop
 */
public class EditableListPropDefault<T> implements EditableListProp<T>, PropInternalListener {

	PropMap propMap;
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
		this.name = name;
		this.list = new ObservableListDefault<T>(data);
		list.props().addInternalListener(this);
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
	
	@Override
	public T set(int key, T value) {
		return list.set(key, value);
	}
	
	@Override
	public <S> void propInternalChanged(PropEvent<S> event) {
		//We need to fire the change on as a deep change
		props().propChanged(new PropEventDefault<T>(this, event));
	}
	
	@Override
	public void setPropMap(PropMap map) {
		if (propMap != null) throw new IllegalArgumentException("Prop '" + this + "' already has its PropMap set to '" + propMap + "'");
		if (map == null) throw new IllegalArgumentException("PropMap must be non-null");
		this.propMap = map;
	}

	@Override
	public T get(int index) {
		return list.get(index);
	}	

	@Override
	public Bean getBean() {
		return props().getBean();
	}

	public PropName<EditableListProp<T>, T> getName() {
		return name;
	}
	
	@Override
	public PropMap props() {
		return propMap;
	}

	@Override
	public PropInfo getInfo() {
		return PropInfo.EDITABLE_LIST;
	}

	@Override
	public String toString() {
		return "Editable List Prop '" + getName().getString() + "' = '" + get() + "'";
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

}
