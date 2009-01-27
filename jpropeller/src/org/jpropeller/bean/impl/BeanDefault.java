package org.jpropeller.bean.impl;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import org.jpropeller.bean.Bean;
import org.jpropeller.bean.BeanFeatures;
import org.jpropeller.bean.ExtendedBeanFeatures;
import org.jpropeller.name.PropName;
import org.jpropeller.path.impl.BeanPathBuilder;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.GenericEditableProp;
import org.jpropeller.properties.GenericProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.Immutable;
import org.jpropeller.properties.immutable.impl.EditablePropImmutable;
import org.jpropeller.properties.immutable.impl.PropImmutable;
import org.jpropeller.properties.list.EditableListProp;
import org.jpropeller.properties.map.EditableMapProp;
import org.jpropeller.properties.path.impl.EditablePathProp;
import org.jpropeller.properties.path.impl.EditablePathPropBuilder;
import org.jpropeller.properties.path.impl.PathProp;
import org.jpropeller.properties.path.impl.PathPropBuilder;
import org.jpropeller.system.Props;
import org.jpropeller.transformer.Transformer;
import org.jpropeller.ui.ImmutableIcon;

/**
 * A default {@link Bean} with no properties, designed to be
 * subclassed as a (very slightly) easier way to implement {@link Bean}
 * @author bwebster
 */
public abstract class BeanDefault implements Bean {
	
	//Standard code block for a bean
	ExtendedBeanFeatures features = Props.getPropSystem().createExtendedBeanFeatures(this);
	
	@Override
	public BeanFeatures features() {
		return features;
	}
	
	protected <J, S> EditableMapProp<J, S> editableMap(Class<S> clazz,
			String name, Map<J, S> data) {
		return features.editableMap(clazz, name, data);
	}

	protected <J, S> EditableMapProp<J, S> editableMap(Class<S> clazz, String name) {
		return features.editableMap(clazz, name);
	}

	protected <S> EditableListProp<S> editableList(Class<S> clazz, String name) {
		return features.editableList(clazz, name);
	}

	protected <S> EditableListProp<S> editableList(Class<S> clazz, String name,
			List<S> data) {
		return features.editableList(clazz, name, data);
	}

	protected <S extends Enum<S>> EditablePropImmutable<S> editable(Class<S> clazz, String name, S value) {
		return features.editable(clazz, name, value);
	}
	
	protected <S extends Bean> EditableProp<S> editable(Class<S> clazz,
			String name, S value) {
		return features.editable(clazz, name, value);
	}

	protected <S extends Changeable> EditableProp<S> editable(
			Class<S> clazz, String name, S value) {
		return features.editable(clazz, name, value);
	}
	
	protected EditableProp<Boolean> editable(String name, Boolean value) {
		return features.editable(name, value);
	}

	protected EditableProp<Byte> editable(String name, Byte value) {
		return features.editable(name, value);
	}

	protected EditableProp<Double> editable(String name, Double value) {
		return features.editable(name, value);
	}

	protected EditableProp<Float> editable(String name, Float value) {
		return features.editable(name, value);
	}

	protected EditableProp<Integer> editable(String name, Integer value) {
		return features.editable(name, value);
	}

	protected EditableProp<Long> editable(String name, Long value) {
		return features.editable(name, value);
	}

	protected EditableProp<Short> editable(String name, Short value) {
		return features.editable(name, value);
	}

	protected EditableProp<String> editable(String name, String value) {
		return features.editable(name, value);
	}

	protected EditableProp<ImmutableIcon> editable(String name, ImmutableIcon value) {
		return features.editable(name, value);
	}

	protected <S extends Enum<S>> PropImmutable<S> create(
			Class<S> clazz, String name, S value) {
		return features.create(clazz, name, value);
	}

	protected Prop<Boolean> create(String name, Boolean value) {
		return features.create(name, value);
	}

	protected Prop<Byte> create(String name, Byte value) {
		return features.create(name, value);
	}

	protected Prop<Double> create(String name, Double value) {
		return features.create(name, value);
	}

	protected Prop<Float> create(String name, Float value) {
		return features.create(name, value);
	}

	protected Prop<Integer> create(String name, Integer value) {
		return features.create(name, value);
	}

	protected Prop<Long> create(String name, Long value) {
		return features.create(name, value);
	}

	protected Prop<Short> create(String name, Short value) {
		return features.create(name, value);
	}

	protected Prop<String> create(String name, String value) {
		return features.create(name, value);
	}

	protected Prop<ImmutableIcon> create(String name, ImmutableIcon value) {
		return features.create(name, value);
	}

	protected <T extends Immutable> Prop<T> create(String name, Class<T> clazz, T value) {
		return features.create(name, clazz, value);
	}

	protected Prop<Color> create(String name, Color value) {
		return features.create(name, value);
	}

	protected <S extends Immutable> EditableProp<S> editable(Class<S> clazz, String name, S value) {
		return features.editable(clazz, name, value);
	}

	protected EditableProp<Color> editable(String name, Color value) {
		return features.editable(name, value);
	}

	protected <P extends GeneralProp<S>, S> P addProp(P prop) {
		return features.add(prop);
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
	protected <R extends Bean, P extends GenericEditableProp<T>, T> EditablePathPropBuilder<R, R, P, T> editableFrom(String name, Class<T> clazz, R pathRoot) {
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
	protected <R extends Bean, P extends GenericProp<T>, T> PathPropBuilder<R, R, P, T> from(String name, Class<T> clazz, R pathRoot) {
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
