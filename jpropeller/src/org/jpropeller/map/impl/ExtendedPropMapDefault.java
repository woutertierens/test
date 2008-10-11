package org.jpropeller.map.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jpropeller.bean.Bean;
import org.jpropeller.map.ExtendedPropMap;
import org.jpropeller.map.PropMapMutable;
import org.jpropeller.name.GenericPropName;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.bean.EditableBeanProp;
import org.jpropeller.properties.bean.impl.EditableBeanPropDefault;
import org.jpropeller.properties.event.PropEvent;
import org.jpropeller.properties.event.PropInternalListener;
import org.jpropeller.properties.event.PropListener;
import org.jpropeller.properties.list.EditableListProp;
import org.jpropeller.properties.list.impl.EditableListPropDefault;
import org.jpropeller.properties.map.EditableMapProp;
import org.jpropeller.properties.map.impl.EditableMapPropDefault;
import org.jpropeller.properties.primitive.impl.EditablePropPrimitive;
import org.jpropeller.properties.primitive.impl.PropPrimitive;

/**
 * A default implementation of {@link ExtendedPropMap} as a wrapper
 * around a {@link PropMapMutable}
 */
public class ExtendedPropMapDefault implements PropMapMutable, ExtendedPropMap {

	PropMapMutable delegate;
	
	/**
	 * Create a convenient wrapper around another {@link PropMapMutable}
	 * @param delegate
	 */
	public ExtendedPropMapDefault(PropMapMutable delegate) {
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

	/* (non-Javadoc)
	 * @see org.jpropeller.map.impl.ExtendedPropMap#editable(java.lang.String, java.lang.Float)
	 */
	public EditableProp<Float> editable(String name, Float value) {
		return add(EditablePropPrimitive.create(name, value));
	}

	/* (non-Javadoc)
	 * @see org.jpropeller.map.impl.ExtendedPropMap#editable(java.lang.String, java.lang.Double)
	 */
	public EditableProp<Double> editable(String name, Double value) {
		return add(EditablePropPrimitive.create(name, value));
	}

	/* (non-Javadoc)
	 * @see org.jpropeller.map.impl.ExtendedPropMap#editable(java.lang.String, java.lang.Boolean)
	 */
	public EditableProp<Boolean> editable(String name, Boolean value) {
		return add(EditablePropPrimitive.create(name, value));
	}

	/* (non-Javadoc)
	 * @see org.jpropeller.map.impl.ExtendedPropMap#editable(java.lang.String, java.lang.Byte)
	 */
	public EditableProp<Byte> editable(String name, Byte value) {
		return add(EditablePropPrimitive.create(name, value));
	}

	/* (non-Javadoc)
	 * @see org.jpropeller.map.impl.ExtendedPropMap#editable(java.lang.String, java.lang.Short)
	 */
	public EditableProp<Short> editable(String name, Short value) {
		return add(EditablePropPrimitive.create(name, value));
	}

	/* (non-Javadoc)
	 * @see org.jpropeller.map.impl.ExtendedPropMap#editable(java.lang.String, java.lang.Integer)
	 */
	public EditableProp<Integer> editable(String name, Integer value) {
		return add(EditablePropPrimitive.create(name, value));
	}

	/* (non-Javadoc)
	 * @see org.jpropeller.map.impl.ExtendedPropMap#editable(java.lang.String, java.lang.Long)
	 */
	public EditableProp<Long> editable(String name, Long value) {
		return add(EditablePropPrimitive.create(name, value));
	}

	
	/* (non-Javadoc)
	 * @see org.jpropeller.map.impl.ExtendedPropMap#editable(java.lang.String, java.lang.String)
	 */
	public EditableProp<String> editable(String name, String value) {
		return add(EditablePropPrimitive.create(name, value));
	}
	
	/* (non-Javadoc)
	 * @see org.jpropeller.map.impl.ExtendedPropMap#editable(java.lang.Class, java.lang.String, S)
	 */
	public <S extends Enum<S>> EditablePropPrimitive<S> editable(Class<S> clazz, String name, S value) {
		return add(new EditablePropPrimitive<S>(PropName.editable(name, clazz), value));
	}

	/* (non-Javadoc)
	 * @see org.jpropeller.map.impl.ExtendedPropMap#editable(java.lang.Class, java.lang.String, S)
	 */
	public <S extends Bean> EditableBeanProp<S> editable(Class<S> clazz, String name, S value) {
		return add(EditableBeanPropDefault.create(name, clazz, value));
	}
	
	/* (non-Javadoc)
	 * @see org.jpropeller.map.impl.ExtendedPropMap#editableList(java.lang.Class, java.lang.String, java.util.List)
	 */
	public <S> EditableListProp<S> editableList(Class<S> clazz, String name, List<S> data) {
		return add(EditableListPropDefault.create(name, clazz, data));
	}
	
	/* (non-Javadoc)
	 * @see org.jpropeller.map.impl.ExtendedPropMap#editableList(java.lang.Class, java.lang.String)
	 */
	public <S> EditableListProp<S> editableList(Class<S> clazz, String name) {
		return add(EditableListPropDefault.create(name, clazz));
	}
	
	/* (non-Javadoc)
	 * @see org.jpropeller.map.impl.ExtendedPropMap#editableMap(java.lang.Class, java.lang.String, java.util.Map)
	 */
	public <J, S> EditableMapProp<J, S> editableMap(Class<S> clazz, String name, Map<J, S> data) {
		return add(EditableMapPropDefault.create(name, clazz, data));
	}
	
	/* (non-Javadoc)
	 * @see org.jpropeller.map.impl.ExtendedPropMap#editableMap(java.lang.Class, java.lang.String)
	 */
	public <J, S> EditableMapProp<J, S> editableMap(Class<S> clazz, String name) {
		return add(EditableMapPropDefault.<J, S>create(name, clazz));
	}

	/////////////////////////////////////////////////////////////////
	//	Uneditable versions, for:
	//
	//	Float, Double
	//	Boolean, Byte, Short, Integer, Long
	//	String
	//	Enum
	//	Bean
	//	List
	//	Map
	/////////////////////////////////////////////////////////////////

	public Prop<Float> uneditable(String name, Float value) {
		return add(PropPrimitive.create(name, value));
	}
	public Prop<Double> uneditable(String name, Double value) {
		return add(PropPrimitive.create(name, value));
	}
	public Prop<Boolean> create(String name, Boolean value) {
		return add(PropPrimitive.create(name, value));
	}
	public Prop<Byte> uneditable(String name, Byte value) {
		return add(PropPrimitive.create(name, value));
	}
	public Prop<Short> uneditable(String name, Short value) {
		return add(PropPrimitive.create(name, value));
	}
	public Prop<Integer> uneditable(String name, Integer value) {
		return add(PropPrimitive.create(name, value));
	}
	public Prop<Long> uneditable(String name, Long value) {
		return add(PropPrimitive.create(name, value));
	}
	public Prop<String> uneditable(String name, String value) {
		return add(PropPrimitive.create(name, value));
	}
	public <S extends Enum<S>> PropPrimitive<S> create(Class<S> clazz, String name, S value) {
		return add(new PropPrimitive<S>(PropName.create(name, clazz), value));
	}

	//TODO put this back when we have these uneditable types
//	/**
//	 * Make a new {@link Prop} with an {@link Bean} value
//	 * and add to this map
//	 * @param <S>
//	 * 		The type of the enum. 
//	 * @param clazz 
//	 * 		The type of the enum.
//	 * @param name
//	 * 		The name of the prop
//	 * @param value
//	 * 		The initial value of the prop
//	 * @return
//	 * 		The new prop
//	 */
//	public <S extends Bean> UneditableBeanProp<S> uneditable(Class<S> clazz, String name, S value) {
//		return add(UneditableBeanPropDefault.create(name, clazz, value));
//	}
	
//	/**
//	 * Make a new {@link ListProp} with and add to this map
//	 * @param <S>
//	 * 		The type of the list contents. 
//	 * @param clazz 
//	 * 		The type of the list contents.
//	 * @param name
//	 * 		The name of the prop
//	 * @param data
//	 * 		The data contents for the prop
//	 * @return
//	 * 		The new prop
//	 */
//	public <S> UneditableListProp<S> uneditableList(Class<S> clazz, String name, List<S> data) {
//		return add(UneditableListPropDefault.create(name, clazz, data));
//	}
	
//	/**
//	 * Make a new {@link MapProp} with given data and add to this map
//	 * @param <J> 
//	 * 		The type of key for the map/property
//	 * @param <S>
//	 * 		The type of the map contents. 
//	 * @param clazz 
//	 * 		The type of the map contents.
//	 * @param name
//	 * 		The name of the prop
//	 * @param data 
//	 * 		Initial data for the map/property
//	 * @return
//	 * 		The new prop
//	 */
//	public <J, S> UneditableMapProp<J, S> uneditableMap(Class<S> clazz, String name, Map<J, S> data) {
//		return add(UneditableMapPropDefault.create(name, clazz, data));
//	}
//	
//	/**
//	 * Make a new {@link MapProp} with no mappings and add to this map
//	 * @param <J> 
//	 * 		The type of key for the map/property
//	 * @param <S>
//	 * 		The type of the map contents. 
//	 * @param clazz 
//	 * 		The type of the map contents.
//	 * @param name
//	 * 		The name of the prop
//	 * @return
//	 * 		The new prop
//	 */
//	public <J, S> UneditableMapProp<J, S> uneditableMap(Class<S> clazz, String name) {
//		return add(UneditableMapPropDefault.<J, S>create(name, clazz));
//	}

	
	/////////////////////////////////////////////////////////////////
	//                                                             //
	//	Delegated methods										   //
	//                                                             //
	/////////////////////////////////////////////////////////////////
	

	public <P extends GeneralProp<S>, S> P add(P prop) {
		return delegate.add(prop);
	}


	public void addInternalListener(PropInternalListener listener) {
		delegate.addInternalListener(listener);
	}


	public void addListener(PropListener listener) {
		delegate.addListener(listener);
	}


	public <P extends GeneralProp<S>, S> P get(PropName<P, S> name) {
		return delegate.get(name);
	}

	public <P extends GeneralProp<S>, S> P getUnsafe(GenericPropName<P, S> name) {
		return delegate.getUnsafe(name);
	}

	public Bean getBean() {
		return delegate.getBean();
	}


	public List<GeneralProp<?>> getList() {
		return delegate.getList();
	}


	public Iterator<GeneralProp<?>> iterator() {
		return delegate.iterator();
	}


	public void propChanged(PropEvent<?> event) {
		delegate.propChanged(event);
	}


	public void removeInternalListener(PropInternalListener listener) {
		delegate.removeInternalListener(listener);
	}


	public void removeListener(PropListener listener) {
		delegate.removeListener(listener);
	}

	
}
