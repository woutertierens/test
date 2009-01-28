package org.jpropeller.bean;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.Immutable;
import org.jpropeller.properties.immutable.impl.EditablePropImmutable;
import org.jpropeller.properties.immutable.impl.PropImmutable;
import org.jpropeller.properties.list.EditableListProp;
import org.jpropeller.properties.list.ListProp;
import org.jpropeller.properties.map.EditableMapProp;
import org.jpropeller.properties.map.MapProp;
import org.jpropeller.properties.set.EditableSetProp;
import org.jpropeller.properties.set.SetProp;
import org.jpropeller.ui.ImmutableIcon;

/**
 * A {@link BeanFeatures} providing numerous convenience methods
 * for creating Props and adding them to the bean directly.
 * This is an interface that should only be visible to the {@link Bean}
 * itself - {@link Bean}s should only make their {@link BeanFeatures} available
 * externally as the basic {@link BeanFeatures} interface
 */
public interface ExtendedBeanFeatures extends MutableBeanFeatures{
	
	/**
	 * Make a new prop and add to this bean
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public EditableProp<Float> editable(String name, Float value);

	/**
	 * Make a new prop and add to this bean
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public EditableProp<Double> editable(String name, Double value);

	/**
	 * Make a new prop and add to this bean
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public EditableProp<Boolean> editable(String name, Boolean value);

	/**
	 * Make a new prop and add to this bean
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public EditableProp<Byte> editable(String name, Byte value);

	/**
	 * Make a new prop and add to this bean
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public EditableProp<Short> editable(String name, Short value);

	/**
	 * Make a new prop and add to this bean
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public EditableProp<Integer> editable(String name, Integer value);

	/**
	 * Make a new prop and add to this bean
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public EditableProp<Long> editable(String name, Long value);

	/**
	 * Make a new prop and add to this bean
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public EditableProp<String> editable(String name, String value);

	/**
	 * Make a new {@link Prop} with an {@link Enum} value
	 * and add to this bean
	 * @param <S>
	 * 		The type of the enum. 
	 * @param clazz 
	 * 		The type of the enum.
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The initial value of the prop
	 * @return
	 * 		The new prop
	 */
	public <S extends Enum<S>> EditablePropImmutable<S> editable(
			Class<S> clazz, String name, S value);

	/**
	 * Make a new {@link Prop} with an {@link Changeable} value
	 * and add to this bean
	 * @param <S>
	 * 		The type of the {@link Changeable}. 
	 * @param clazz 
	 * 		The class of the {@link Changeable}.
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The initial value of the prop
	 * @return
	 * 		The new prop
	 */
	public <S extends Changeable> EditableProp<S> editable(Class<S> clazz,
			String name, S value);

	/**
	 * Make a new {@link Prop} with an {@link Immutable} value
	 * and add to this bean
	 * @param <S>
	 * 		The type of the {@link Immutable}. 
	 * @param clazz 
	 * 		The class of the {@link Immutable}.
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The initial value of the prop
	 * @return
	 * 		The new prop
	 */
	public <S extends Immutable> EditableProp<S> editable(Class<S> clazz,
			String name, S value);
	
	/**
	 * Make a new prop and add to this bean
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public EditableProp<ImmutableIcon> editable(String name, ImmutableIcon value);
	
	/**
	 * Make a new prop and add to this bean
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public EditableProp<Color> editable(String name, Color value);
	
	/**
	 * Make a new prop and add to this bean
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public EditableProp<DateTime> editable(String name, DateTime value);
	
	/**
	 * Make a new {@link ListProp} with and add to this bean
	 * @param <S>
	 * 		The type of the list contents. 
	 * @param clazz 
	 * 		The type of the list contents.
	 * @param name
	 * 		The name of the prop
	 * @param data
	 * 		The data contents for the prop
	 * @return
	 * 		The new prop
	 */
	public <S> EditableListProp<S> editableList(Class<S> clazz, String name,
			List<S> data);

	/**
	 * Make a new {@link ListProp} with and add to this bean
	 * @param <S>
	 * 		The type of the list contents. 
	 * @param clazz 
	 * 		The type of the list contents.
	 * @param name
	 * 		The name of the prop
	 * @return
	 * 		The new prop
	 */
	public <S> EditableListProp<S> editableList(Class<S> clazz, String name);

	/**
	 * Make a new {@link MapProp} with given data and add to this bean
	 * @param <J> 
	 * 		The type of key for the map/property
	 * @param <S>
	 * 		The type of the map contents. 
	 * @param clazz 
	 * 		The type of the map contents.
	 * @param name
	 * 		The name of the prop
	 * @param data 
	 * 		Initial data for the map/property
	 * @return
	 * 		The new prop
	 */
	public <J, S> EditableMapProp<J, S> editableMap(Class<S> clazz,
			String name, Map<J, S> data);

	/**
	 * Make a new {@link MapProp} with no mappings and add to this bean
	 * @param <J> 
	 * 		The type of key for the map/property
	 * @param <S>
	 * 		The type of the map contents. 
	 * @param clazz 
	 * 		The type of the map contents.
	 * @param name
	 * 		The name of the prop
	 * @return
	 * 		The new prop
	 */
	public <J, S> EditableMapProp<J, S> editableMap(Class<S> clazz, String name);


	/**
	 * Make a new {@link SetProp} and add to this bean
	 * @param <S>
	 * 		The type of the set contents. 
	 * @param clazz 
	 * 		The type of the set contents.
	 * @param name
	 * 		The name of the prop
	 * @param data
	 * 		The data contents for the prop
	 * @return
	 * 		The new prop
	 */
	public <S> EditableSetProp<S> editableSet(Class<S> clazz, String name,
			Set<S> data);

	/**
	 * Make a new {@link SetProp} from empty default set, and add to this bean
	 * @param <S>
	 * 		The type of the set contents. 
	 * @param clazz 
	 * 		The type of the set contents.
	 * @param name
	 * 		The name of the prop
	 * @return
	 * 		The new prop
	 */
	public <S> EditableSetProp<S> editableSet(Class<S> clazz, String name);

	
	/**
	 * Make a new prop and add to this bean
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public Prop<Float> create(String name, Float value);

	/**
	 * Make a new prop and add to this bean
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public Prop<Double> create(String name, Double value);

	/**
	 * Make a new prop and add to this bean
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public Prop<Boolean> create(String name, Boolean value);

	/**
	 * Make a new prop and add to this bean
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public Prop<Byte> create(String name, Byte value);

	/**
	 * Make a new prop and add to this bean
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public Prop<Short> create(String name, Short value);

	/**
	 * Make a new prop and add to this bean
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public Prop<Integer> create(String name, Integer value);

	/**
	 * Make a new prop and add to this bean
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public Prop<Long> create(String name, Long value);

	/**
	 * Make a new prop and add to this bean
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public Prop<String> create(String name, String value);

	/**
	 * Make a new prop and add to this bean
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public Prop<ImmutableIcon> create(String name, ImmutableIcon value);

	/**
	 * Make a new prop and add to this bean
	 * @param <T>
	 * 		The type of {@link Immutable} value in the prop 
	 * @param name
	 * 		The name of the prop
	 * @param clazz 
	 * 		The class of {@link Immutable} value in the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public <T extends Immutable> Prop<T> create(String name, Class<T> clazz, T value);

	/**
	 * Make a new {@link Prop} with an {@link Enum} value
	 * and add to this bean
	 * @param <S>
	 * 		The type of the enum. 
	 * @param clazz 
	 * 		The type of the enum.
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The initial value of the prop
	 * @return
	 * 		The new prop
	 */
	public <S extends Enum<S>> PropImmutable<S> create(
			Class<S> clazz, String name, S value);

	/**
	 * Make a new prop and add to this bean
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public Prop<Color> create(String name, Color value);

	/**
	 * Make a new prop and add to this bean
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public Prop<DateTime> create(String name, DateTime value);
}
