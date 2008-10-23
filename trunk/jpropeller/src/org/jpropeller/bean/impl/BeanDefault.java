package org.jpropeller.bean.impl;

import java.util.List;
import java.util.Map;

import org.jpropeller.bean.Bean;
import org.jpropeller.map.ExtendedPropMap;
import org.jpropeller.map.PropMap;
import org.jpropeller.name.PropName;
import org.jpropeller.path.impl.BeanPathBuilder;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.GenericEditableProp;
import org.jpropeller.properties.GenericProp;
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
import org.jpropeller.transformer.Transformer;

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
	 * Make a builder that will build an {@link EditablePathProp} by use of
	 * {@link EditablePathPropBuilder#via(PropName)} and {@link EditablePathPropBuilder#to(PropName)},
	 * etc.
	 * <br />
	 * When the {@link EditablePathProp} is built and returned, it will be added to the props of 
	 * this {@link BeanDefault} instance. This slightly reduces the code needed to create a new
	 * property, from something like:
	 * 
	 * private EditableProp<D> dByTransforms = addProp(EditablePathPropBuilder.from("dByTransforms", D.class, this).via(aToB).via(bToC).to(cToD));
	 * 
	 * to something like:
	 * 
	 * private EditableProp<D> dByTransforms = editableFrom("dByTransforms", D.class, this).via(aToB).via(bToC).to(cToD);
	 * 
	 * @param <R>
	 * 		The type of the root of the path
	 * @param <P>
	 * 		The type of property reached by the path
	 * @param <T>
	 * 		The type of data in the property reached by the path (and hence in the built property)
	 * @param name
	 * 		The name of the property
	 * @param clazz
	 * 		The class of data in the property reached by the path (and hence in the built property)
	 * @param pathRoot
	 * 		The root of the path - often "this", but may be another bean.
	 * @return
	 * 		A builder for a new property.
	 */
	public <R extends Bean, P extends GenericEditableProp<T>, T> EditablePathPropBuilder<R, R, P, T> editableFrom(String name, Class<T> clazz, R pathRoot) {
		return new BeanEditablePathPropBuilder<R, R, P, T>(name, clazz, pathRoot, BeanPathBuilder.<R>create());
	}
	
	private class BeanEditablePathPropBuilder<R extends Bean, E extends Bean, P extends GenericEditableProp<T>, T> extends EditablePathPropBuilder<R, E, P, T>{
		private BeanEditablePathPropBuilder(String name, Class<T> clazz,
				R pathRoot, BeanPathBuilder<R, E> builder) {
			super(name, clazz, pathRoot, builder);
		}
		private BeanEditablePathPropBuilder(PropName<EditableProp<T>, T> name,
				R pathRoot, BeanPathBuilder<R, E> builder) {
			super(name, pathRoot, builder);
		}

		@Override
		public <F extends Bean, G extends GenericProp<F>> EditablePathPropBuilder<R, F, P, T> via(
				PropName<G, F> nextName) {
			return new BeanEditablePathPropBuilder<R, F, P, T>(name, pathRoot, builder.via(nextName));
		}

		@Override
		public <F extends Bean> EditablePathPropBuilder<R, F, P, T> via(Transformer<? super E, ? extends GenericProp<F>> nextTransform) {
			return new BeanEditablePathPropBuilder<R, F, P, T>(name, pathRoot, builder.via(nextTransform));
		}
	
		@Override
		public EditablePathProp<R, T> to(PropName<? extends P, T> lastName) {
			EditablePathProp<R,T> editablePathProp = super.to(lastName);
			addProp(editablePathProp);
			return editablePathProp;
		}

		@Override
		public EditablePathProp<R, T> to(
				Transformer<? super E, ? extends P> lastTransform) {
			EditablePathProp<R,T> editablePathProp = super.to(lastTransform);
			addProp(editablePathProp);
			return editablePathProp;
		}
	}

	/**
	 * Make a builder that will build an {@link PathProp} by use of
	 * {@link PathPropBuilder#via(PropName)} and {@link PathPropBuilder#to(PropName)},
	 * etc.
	 * <br />
	 * When the {@link PathProp} is built and returned, it will be added to the props of 
	 * this {@link BeanDefault} instance. This slightly reduces the code needed to create a new
	 * property, from something like:
	 * 
	 * private Prop<D> dByTransforms = addProp(PathPropBuilder.from("dByTransforms", D.class, this).via(aToB).via(bToC).to(cToD));
	 * 
	 * to something like:
	 * 
	 * private Prop<D> dByTransforms = from("dByTransforms", D.class, this).via(aToB).via(bToC).to(cToD);
	 * 
	 * @param <R>
	 * 		The type of the root of the path
	 * @param <P>
	 * 		The type of property reached by the path
	 * @param <T>
	 * 		The type of data in the property reached by the path (and hence in the built property)
	 * @param name
	 * 		The name of the property
	 * @param clazz
	 * 		The class of data in the property reached by the path (and hence in the built property)
	 * @param pathRoot
	 * 		The root of the path - often "this", but may be another bean.
	 * @return
	 * 		A builder for a new property.
	 */
	public <R extends Bean, P extends GenericProp<T>, T> PathPropBuilder<R, R, P, T> from(String name, Class<T> clazz, R pathRoot) {
		return new BeanPathPropBuilder<R, R, P, T>(name, clazz, pathRoot, BeanPathBuilder.<R>create());
	}
	
	private class BeanPathPropBuilder<R extends Bean, E extends Bean, P extends GenericProp<T>, T> extends PathPropBuilder<R, E, P, T>{
		
		private BeanPathPropBuilder(PropName<Prop<T>, T> name, R pathRoot,
				BeanPathBuilder<R, E> builder) {
			super(name, pathRoot, builder);
		}

		private BeanPathPropBuilder(String name, Class<T> clazz,
				R pathRoot, BeanPathBuilder<R, E> builder) {
			super(name, clazz, pathRoot, builder);
		}

		@Override
		public <F extends Bean, G extends GenericProp<F>> PathPropBuilder<R, F, P, T> via(
				PropName<G, F> nextName) {
			return new BeanPathPropBuilder<R, F, P, T>(name, pathRoot, builder.via(nextName));
		}

		@Override
		public <F extends Bean> PathPropBuilder<R, F, P, T> via(Transformer<? super E, ? extends GenericProp<F>> nextTransform) {
			return new BeanPathPropBuilder<R, F, P, T>(name, pathRoot, builder.via(nextTransform));
		}

		
		@Override
		public PathProp<R, T> to(PropName<P, T> lastName) {
			PathProp<R,T> pathProp = super.to(lastName);
			addProp(pathProp);
			return pathProp;
		}

		@Override
		public PathProp<R, T> to(
				Transformer<? super E, ? extends P> lastTransform) {
			PathProp<R,T> pathProp = super.to(lastTransform);
			addProp(pathProp);
			return pathProp;
		}		

	}
}
