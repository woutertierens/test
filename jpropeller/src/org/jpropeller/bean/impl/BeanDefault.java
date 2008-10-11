package org.jpropeller.bean.impl;

import java.util.List;
import java.util.Map;

import org.jpropeller.bean.Bean;
import org.jpropeller.map.ExtendedPropMap;
import org.jpropeller.map.PropMap;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.bean.EditableBeanProp;
import org.jpropeller.properties.list.EditableListProp;
import org.jpropeller.properties.map.EditableMapProp;
import org.jpropeller.properties.path.impl.EditablePathProp;
import org.jpropeller.properties.path.impl.EditablePathPropBuilder;
import org.jpropeller.properties.path.impl.PathProp;
import org.jpropeller.properties.path.impl.PathPropBuilder;
import org.jpropeller.properties.primitive.impl.EditablePropPrimitive;
import org.jpropeller.properties.primitive.impl.PropPrimitive;
import org.jpropeller.system.Props;

/**
 * A default {@link Bean} with no properties, designed to be
 * subclassed as a (very slightly) easier way to implement {@link Bean}
 * @author bwebster
 */
public abstract class BeanDefault implements Bean {
	
	//Standard code block for a bean
	ExtendedPropMap props = Props.getPropSystem().createExtendedPropMap(this);
	
	@Override
	public PropMap props() {
		return props;
	}
	
	protected <J, S> EditableMapProp<J, S> editableMap(Class<S> clazz,
			String name, Map<J, S> data) {
		return props.editableMap(clazz, name, data);
	}

	protected <J, S> EditableMapProp<J, S> editableMap(Class<S> clazz, String name) {
		return props.editableMap(clazz, name);
	}

	protected <S> EditableListProp<S> editableList(Class<S> clazz, String name) {
		return props.editableList(clazz, name);
	}

	protected <S> EditableListProp<S> editableList(Class<S> clazz, String name,
			List<S> data) {
		return props.editableList(clazz, name, data);
	}

	protected <S extends Enum<S>> EditablePropPrimitive<S> editable(Class<S> clazz, String name, S value) {
		return props.editable(clazz, name, value);
	}
	
	protected <S extends Bean> EditableBeanProp<S> editable(Class<S> clazz,
			String name, S value) {
		return props.editable(clazz, name, value);
	}

	protected EditableProp<Boolean> editable(String name, Boolean value) {
		return props.editable(name, value);
	}

	protected EditableProp<Byte> editable(String name, Byte value) {
		return props.editable(name, value);
	}

	protected EditableProp<Double> editable(String name, Double value) {
		return props.editable(name, value);
	}

	protected EditableProp<Float> editable(String name, Float value) {
		return props.editable(name, value);
	}

	protected EditableProp<Integer> editable(String name, Integer value) {
		return props.editable(name, value);
	}

	protected EditableProp<Long> editable(String name, Long value) {
		return props.editable(name, value);
	}

	protected EditableProp<Short> editable(String name, Short value) {
		return props.editable(name, value);
	}

	protected EditableProp<String> editable(String name, String value) {
		return props.editable(name, value);
	}

	protected <S extends Enum<S>> PropPrimitive<S> create(
			Class<S> clazz, String name, S value) {
		return props.create(clazz, name, value);
	}

	protected Prop<Boolean> create(String name, Boolean value) {
		return props.create(name, value);
	}

	protected Prop<Byte> create(String name, Byte value) {
		return props.create(name, value);
	}

	protected Prop<Double> create(String name, Double value) {
		return props.create(name, value);
	}

	protected Prop<Float> create(String name, Float value) {
		return props.create(name, value);
	}

	protected Prop<Integer> create(String name, Integer value) {
		return props.create(name, value);
	}

	protected Prop<Long> create(String name, Long value) {
		return props.create(name, value);
	}

	protected Prop<Short> create(String name, Short value) {
		return props.create(name, value);
	}

	protected Prop<String> create(String name, String value) {
		return props.create(name, value);
	}

	protected <P extends GeneralProp<S>, S> P addProp(P prop) {
		return props.add(prop);
	}

	//Present a slightly easier interface for building path props

	/**
	 * Create an editable path property relative to this bean
	 * @param name	
	 * 		The string name of the property
	 * @param clazz
	 * 		The class of the property at the end of the
	 * path (the one that is mirrored by the path property)
	 * 
	 */
	protected <P extends EditableProp<T>, T> EditablePathPropBuilderForBean<P, T> editableFrom(String name, Class<T> clazz) {
		return new EditablePathPropBuilderForBean<P, T>(name, clazz);
	}
	
	protected class EditablePathPropBuilderForBean<P extends EditableProp<T>, T> extends EditablePathPropBuilder<P, T> {
		protected EditablePathPropBuilderForBean(String name, Class<T> clazz) {
			super(name, clazz, BeanDefault.this);
		}
		@Override
		public EditablePathProp<T> to(PropName<? extends P, T> lastName) {
			EditablePathProp<T> prop = super.to(lastName);
			addProp(prop);
			return prop;
		}
	}
	
	/**
	 * Create a path property relative to this bean
	 * @param name	
	 * 		The string name of the property
	 * @param clazz
	 * 		The class of the property at the end of the
	 * path (the one that is mirrored by the path property)
	 * 
	 */
	protected <P extends Prop<T>, T> PathPropBuilderForBean<P, T> from(String name, Class<T> clazz) {
		return new PathPropBuilderForBean<P, T>(name, clazz);
	}
	
	protected class PathPropBuilderForBean<P extends Prop<T>, T> extends PathPropBuilder<P, T> {
		protected PathPropBuilderForBean(String name, Class<T> clazz) {
			super(name, clazz, BeanDefault.this);
		}
		@Override
		public PathProp<T> to(PropName<? extends P, T> lastName) {
			PathProp<T> prop = super.to(lastName);
			addProp(prop);
			return prop;
		}
	}

}
