package org.jpropeller.bean.impl;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.jpropeller.bean.Bean;
import org.jpropeller.bean.ExtendedBeanFeatures;
import org.jpropeller.bean.MutableBeanFeatures;
import org.jpropeller.collection.ObservableList;
import org.jpropeller.name.GenericPropName;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.Immutable;
import org.jpropeller.properties.changeable.impl.EditableChangeablePropDefault;
import org.jpropeller.properties.immutable.impl.EditablePropImmutable;
import org.jpropeller.properties.immutable.impl.PropImmutable;
import org.jpropeller.properties.list.EditableListProp;
import org.jpropeller.properties.list.ListProp;
import org.jpropeller.properties.list.impl.EditableListPropDefault;
import org.jpropeller.properties.list.impl.ListPropDefault;
import org.jpropeller.properties.map.EditableMapProp;
import org.jpropeller.properties.map.impl.EditableMapPropDefault;
import org.jpropeller.properties.set.EditableSetProp;
import org.jpropeller.properties.set.impl.EditableSetPropDefault;
import org.jpropeller.ui.ImmutableIcon;

/**
 * A default implementation of {@link ExtendedBeanFeatures} as a wrapper
 * around a {@link MutableBeanFeatures}
 */
public class ExtendedBeanFeaturesDefault implements ExtendedBeanFeatures {

	MutableBeanFeatures delegate;
	
	/**
	 * Create a convenient wrapper around another {@link MutableBeanFeatures}
	 * @param delegate
	 * 		The {@link MutableBeanFeatures} used to actually provide
	 * implementation of {@link MutableBeanFeatures} interface. This
	 * wrapper just delegates through, and adds implementations of the
	 * additional {@link ExtendedBeanFeatures} methods for creating and
	 * adding props of different types
	 */
	public ExtendedBeanFeaturesDefault(MutableBeanFeatures delegate) {
		super();
		this.delegate = delegate;
	}
	
	/////////////////////////////////////////////////////////////////
	//                                                             //
	//	Convenience methods for creating and adding props          //
	//                                                             //
	/////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////
	//	Editable versions, for:
	//
	//	Float, Double
	//	Boolean, Byte, Short, Integer, Long
	//	String
	//	Enum
	//	Bean
	//	List
	//	Map
	/////////////////////////////////////////////////////////////////

	public EditableProp<Float> editable(String name, Float value) {
		return add(EditablePropImmutable.create(name, value));
	}

	public EditableProp<Double> editable(String name, Double value) {
		return add(EditablePropImmutable.create(name, value));
	}

	public EditableProp<Boolean> editable(String name, Boolean value) {
		return add(EditablePropImmutable.create(name, value));
	}

	public EditableProp<Byte> editable(String name, Byte value) {
		return add(EditablePropImmutable.create(name, value));
	}

	public EditableProp<Short> editable(String name, Short value) {
		return add(EditablePropImmutable.create(name, value));
	}

	public EditableProp<Integer> editable(String name, Integer value) {
		return add(EditablePropImmutable.create(name, value));
	}

	public EditableProp<Long> editable(String name, Long value) {
		return add(EditablePropImmutable.create(name, value));
	}
	
	public EditableProp<String> editable(String name, String value) {
		return add(EditablePropImmutable.create(name, value));
	}

	public EditableProp<DateTime> editable(String name, DateTime value) {
		return add(EditablePropImmutable.create(name, value));
	}

	public EditableProp<Color> editable(String name, Color value) {
		return add(EditablePropImmutable.create(name, value));
	}

	public <S extends Enum<S>> EditablePropImmutable<S> editable(Class<S> clazz, String name, S value) {
		return add(EditablePropImmutable.create(name, clazz, value));
	}

	public EditableProp<ImmutableIcon> editable(String name, ImmutableIcon value) {
		return add(EditablePropImmutable.create(name, value));
	}
	
	public <S extends Changeable> EditableProp<S> editable(Class<S> clazz, String name, S value) {
		return add(EditableChangeablePropDefault.create(name, clazz, value));
	}

	public <S extends Immutable> EditableProp<S> editable(Class<S> clazz, String name, S value) {
		return add(EditablePropImmutable.create(name, clazz, value));
	}

	public <S> EditableListProp<S> editableList(Class<S> clazz, String name, List<S> data) {
		return add(EditableListPropDefault.create(name, clazz, data));
	}

	public <S> EditableListProp<S> editableList(Class<S> clazz, String name, ObservableList<S> data) {
		return add(EditableListPropDefault.create(name, clazz, data));
	}

	public <S> EditableListProp<S> editableList(Class<S> clazz, String name) {
		return add(EditableListPropDefault.create(name, clazz));
	}

	public <S> ListProp<S> createList(Class<S> clazz, String name, List<S> data) {
		return add(ListPropDefault.create(name, clazz, data));
	}

	public <S> ListProp<S> createList(Class<S> clazz, String name, ObservableList<S> data) {
		return add(ListPropDefault.create(name, clazz, data));
	}

	public <S> ListProp<S> createList(Class<S> clazz, String name) {
		return add(ListPropDefault.create(name, clazz));
	}
	
	public <J, S> EditableMapProp<J, S> editableMap(Class<S> clazz, String name, Map<J, S> data) {
		return add(EditableMapPropDefault.create(name, clazz, data));
	}
	
	public <J, S> EditableMapProp<J, S> editableMap(Class<S> clazz, String name) {
		return add(EditableMapPropDefault.<J, S>create(name, clazz));
	}

	public <S> EditableSetProp<S> editableSet(Class<S> clazz, String name, Set<S> data) {
		return add(EditableSetPropDefault.create(name, clazz, data));
	}
	
	public <S> EditableSetProp<S> editableSet(Class<S> clazz, String name) {
		return add(EditableSetPropDefault.create(name, clazz));
	}

	/////////////////////////////////////////////////////////////////
	//	Default versions, for:
	//
	//	Float, Double
	//	Boolean, Byte, Short, Integer, Long
	//	String
	//	Enum
	//	Bean
	//	List
	//	Map
	/////////////////////////////////////////////////////////////////

	public Prop<Float> create(String name, Float value) {
		return add(PropImmutable.create(name, value));
	}
	public Prop<Double> create(String name, Double value) {
		return add(PropImmutable.create(name, value));
	}
	public Prop<Boolean> create(String name, Boolean value) {
		return add(PropImmutable.create(name, value));
	}
	public Prop<Byte> create(String name, Byte value) {
		return add(PropImmutable.create(name, value));
	}
	public Prop<Short> create(String name, Short value) {
		return add(PropImmutable.create(name, value));
	}
	public Prop<Integer> create(String name, Integer value) {
		return add(PropImmutable.create(name, value));
	}
	public Prop<Long> create(String name, Long value) {
		return add(PropImmutable.create(name, value));
	}
	public Prop<String> create(String name, String value) {
		return add(PropImmutable.create(name, value));
	}
	
	public Prop<Color> create(String name, Color value) {
		return add(PropImmutable.create(name, value));
	}

	public Prop<DateTime> create(String name, DateTime value) {
		return add(PropImmutable.create(name, value));
	}

	public <S extends Enum<S>> PropImmutable<S> create(Class<S> clazz, String name, S value) {
		return add(PropImmutable.create(name, clazz, value));
	}

	public Prop<ImmutableIcon> create(String name, ImmutableIcon value) {
		return add(PropImmutable.create(name, value));
	}

	public <T extends Immutable> Prop<T> create(String name, Class<T> clazz, T value) {
		return add(PropImmutable.create(name, clazz, value));
	}

	/////////////////////////////////////////////////////////////////
	//                                                             //
	//	Delegated methods										   //
	//                                                             //
	/////////////////////////////////////////////////////////////////

	public <P extends GeneralProp<S>, S> P add(P prop) {
		return delegate.add(prop);
	}

	public void addChangeableListener(Changeable listener) {
		delegate.addChangeableListener(listener);
	}

	public void addListener(ChangeListener listener) {
		delegate.addListener(listener);
	}

	public List<Changeable> changeableListenerList() {
		return delegate.changeableListenerList();
	}

	public <P extends GeneralProp<S>, S> P get(PropName<P, S> name) {
		return delegate.get(name);
	}

	public Bean getBean() {
		return delegate.getBean();
	}

	public List<GeneralProp<?>> getList() {
		return delegate.getList();
	}

	public <P extends GeneralProp<S>, S> P getUnsafe(GenericPropName<P, S> name) {
		return delegate.getUnsafe(name);
	}

	public Change internalChange(Changeable changed, Change change,
			List<Changeable> initial, Map<Changeable, Change> allChanges) {
		return delegate.internalChange(changed, change, initial, allChanges);
	}

	public Iterator<GeneralProp<?>> iterator() {
		return delegate.iterator();
	}

	public List<ChangeListener> listenerList() {
		return delegate.listenerList();
	}

	public void removeChangeableListener(Changeable listener) {
		delegate.removeChangeableListener(listener);
	}

	public void removeListener(ChangeListener listener) {
		delegate.removeListener(listener);
	}
	
}
