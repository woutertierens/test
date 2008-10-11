package org.jpropeller.map;

import java.util.List;
import java.util.Map;

import org.jpropeller.bean.Bean;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.bean.EditableBeanProp;
import org.jpropeller.properties.list.EditableListProp;
import org.jpropeller.properties.list.ListProp;
import org.jpropeller.properties.map.EditableMapProp;
import org.jpropeller.properties.map.MapProp;
import org.jpropeller.properties.primitive.impl.EditablePropPrimitive;
import org.jpropeller.properties.primitive.impl.PropPrimitive;

/**
 * A {@link PropMapMutable} that also contains convenience methods
 * for directly adding new {@link GeneralProp}s of various types to
 * the {@link PropMapMutable}
 */
public interface ExtendedPropMap extends PropMapMutable{

	/**
	 * Make a new prop and add to this map
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public EditableProp<Float> editable(String name, Float value);

	/**
	 * Make a new prop and add to this map
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public EditableProp<Double> editable(String name, Double value);

	/**
	 * Make a new prop and add to this map
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public EditableProp<Boolean> editable(String name, Boolean value);

	/**
	 * Make a new prop and add to this map
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public EditableProp<Byte> editable(String name, Byte value);

	/**
	 * Make a new prop and add to this map
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public EditableProp<Short> editable(String name, Short value);

	/**
	 * Make a new prop and add to this map
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public EditableProp<Integer> editable(String name, Integer value);

	/**
	 * Make a new prop and add to this map
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public EditableProp<Long> editable(String name, Long value);

	/**
	 * Make a new prop and add to this map
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
	 * and add to this map
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
	public <S extends Enum<S>> EditablePropPrimitive<S> editable(
			Class<S> clazz, String name, S value);

	/**
	 * Make a new {@link Prop} with an {@link Bean} value
	 * and add to this map
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
	public <S extends Bean> EditableBeanProp<S> editable(Class<S> clazz,
			String name, S value);

	/**
	 * Make a new {@link ListProp} with and add to this map
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
	 * Make a new {@link ListProp} with and add to this map
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
	 * Make a new {@link MapProp} with given data and add to this map
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
	 * Make a new {@link MapProp} with no mappings and add to this map
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
	 * Make a new prop and add to this map
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public Prop<Float> create(String name, Float value);

	/**
	 * Make a new prop and add to this map
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public Prop<Double> create(String name, Double value);

	/**
	 * Make a new prop and add to this map
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public Prop<Boolean> create(String name, Boolean value);

	/**
	 * Make a new prop and add to this map
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public Prop<Byte> create(String name, Byte value);

	/**
	 * Make a new prop and add to this map
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public Prop<Short> create(String name, Short value);

	/**
	 * Make a new prop and add to this map
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public Prop<Integer> create(String name, Integer value);

	/**
	 * Make a new prop and add to this map
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public Prop<Long> create(String name, Long value);

	/**
	 * Make a new prop and add to this map
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop itself (already added to the map)
	 */
	public Prop<String> create(String name, String value);

	/**
	 * Make a new {@link Prop} with an {@link Enum} value
	 * and add to this map
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
	public <S extends Enum<S>> PropPrimitive<S> create(
			Class<S> clazz, String name, S value);

}