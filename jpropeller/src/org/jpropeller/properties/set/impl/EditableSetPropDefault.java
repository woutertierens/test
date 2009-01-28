package org.jpropeller.properties.set.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jpropeller.collection.ObservableSet;
import org.jpropeller.collection.impl.ObservableSetDefault;
import org.jpropeller.info.PropInfo;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.ChangeableFeatures;
import org.jpropeller.properties.change.impl.ChangeDefault;
import org.jpropeller.properties.change.impl.ChangeableFeaturesDefault;
import org.jpropeller.properties.change.impl.InternalChangeImplementation;
import org.jpropeller.properties.set.EditableSetProp;

/**
 * An implementation of an {@link EditableSetProp} that uses an {@link ObservableSet}
 * to back the data.
 *
 * @param <T>
 * 		The type of value in the prop
 */
public class EditableSetPropDefault<T> implements EditableSetProp<T> {

	ChangeableFeatures features;
	private ObservableSet<T> set;
	PropName<EditableSetProp<T>, T> name;
	
	/**
	 * Create a prop
	 * Please note that you must NOT modify the data set after creating
	 * this prop. If you do, those changes will not be visible to any
	 * listeners watching the prop. 
	 * @param data
	 * 		The wrapped set
	 */
	private EditableSetPropDefault(PropName<EditableSetProp<T>, T> name, Set<T> data) {
		features = new ChangeableFeaturesDefault(new InternalChangeImplementation() {
			@Override
			public Change internalChange(Changeable changed, Change change,
					List<Changeable> initial, Map<Changeable, Change> changes) {
				
				boolean setSameInstances = changes.get(set).sameInstances();
				
				//We only listen to our current ObservableSet (Changeable) value, 
				//when it has a change we have a consequent change
				return ChangeDefault.instance(
						false,	//Not initial 
						setSameInstances	//We have same instances if the set has same instances
						);
			}
		}, this); 

		
		this.name = name;
		this.set = new ObservableSetDefault<T>(data);
		set.features().addChangeableListener(this);
	}

	/**
	 * Create a new {@link EditableSetPropDefault}
	 * @param name 
	 * 		The string value of the property name
	 * @param clazz
	 * 		The class of data in the set
	 * @param <S> 
	 * 		The type of data in the set
	 * @param data
	 * 		The data to contain
	 * @return
	 * 		The new {@link EditableSetPropDefault}
	 */
	public static <S> EditableSetPropDefault<S> create(String name, Class<S> clazz, Set<S> data) {
		return new EditableSetPropDefault<S>(PropName.editableSet(name, clazz), data);
	}

	/**
	 * Create a new {@link EditableSetPropDefault}
	 * @param name 
	 * 		The string value of the property name
	 * @param clazz
	 * 		The class of data in the list/indexed property
	 * @param <S> 
	 * 		The type of data in the list/indexed property
	 * @return
	 * 		The new {@link EditableSetPropDefault}
	 */
	public static <S> EditableSetPropDefault<S> create(String name, Class<S> clazz) {
		return new EditableSetPropDefault<S>(PropName.editableSet(name, clazz), null);
	}
	
	public PropName<EditableSetProp<T>, T> getName() {
		return name;
	}
	
	@Override
	public ChangeableFeatures features() {
		return features;
	}

	@Override
	public PropInfo getInfo() {
		return PropInfo.EDITABLE_SET;
	}

	@Override
	public String toString() {
		return "Editable Set Prop '" + getName().getString() + "' = '" + get() + "'";
	}
	
	@Override
	public ObservableSet<T> get() {
		return set;
	}
	
	//Set-style methods, delegated to set
	
	@Override
	public int size() {
		return set.size();
	}

	@Override
	public boolean add(T value) {
		return set.add(value);
	}

	@Override
	public void replace(Iterable<T> newContents) {
		set.replace(newContents);
	}

	@Override
	public Iterator<T> iterator() {
		return set.iterator();
	}

	@Override
	public boolean remove(Object e) {
		return set.remove(e);
	}

	@Override
	public boolean contains(Object element) {
		return set.contains(element);
	}

}
