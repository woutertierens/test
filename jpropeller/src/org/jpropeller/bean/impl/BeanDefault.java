package org.jpropeller.bean.impl;

import java.awt.Color;
import org.joda.time.DateTime;
import org.jpropeller.bean.Bean;
import org.jpropeller.bean.BeanFeatures;
import org.jpropeller.bean.ExtendedBeanFeatures;
import org.jpropeller.collection.CList;
import org.jpropeller.collection.CMap;
import org.jpropeller.collection.CSet;
import org.jpropeller.name.PropName;
import org.jpropeller.path.impl.BeanPathBuilder;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.calculated.impl.ListCalculation;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.Immutable;
import org.jpropeller.properties.immutable.impl.PropImmutable;
import org.jpropeller.properties.list.ListProp;
import org.jpropeller.properties.list.impl.ListPropDefault;
import org.jpropeller.properties.map.MapProp;
import org.jpropeller.properties.path.impl.PathProp;
import org.jpropeller.properties.path.impl.PathPropBuilder;
import org.jpropeller.properties.set.SetProp;
import org.jpropeller.properties.values.ValueProcessor;
import org.jpropeller.properties.values.impl.AcceptProcessor;
import org.jpropeller.properties.values.impl.ReadOnlyProcessor;
import org.jpropeller.system.Props;
import org.jpropeller.transformer.Transformer;
import org.jpropeller.ui.impl.ImmutableIcon;

/**
 * A default {@link Bean} with no properties, designed to be
 * subclassed as a (very slightly) easier way to implement {@link Bean}
 */
public abstract class BeanDefault implements Bean {
	
	//Standard code block for a bean
	ExtendedBeanFeatures features = Props.getPropSystem().createExtendedBeanFeatures(this);
	
	@Override
	public BeanFeatures features() {
		return features;
	}

	//TODO provide toString() implementation listing prop values?
	
	//Methods for making and adding props
	
	protected Prop<Boolean> create(String name, Boolean value,
			ValueProcessor<Boolean> processor) {
		return features.create(name, value, processor);
	}

	protected <I extends Changeable, S> Prop<S> calculated(Class<S> clazz,
			String name, ListCalculation<I, S> calc, I firstInput,
			I... additionalInputs) {
		return features.calculated(clazz, name, calc, firstInput,
				additionalInputs);
	}

	protected <S extends Changeable> Prop<S> create(Class<S> clazz, String name,
			S value) {
		return features.create(clazz, name, value);
	}

	protected Prop<Byte> create(String name, Byte value,
			ValueProcessor<Byte> processor) {
		return features.create(name, value, processor);
	}

	protected Prop<Color> create(String name, Color value,
			ValueProcessor<Color> processor) {
		return features.create(name, value, processor);
	}

	protected Prop<DateTime> create(String name, DateTime value,
			ValueProcessor<DateTime> processor) {
		return features.create(name, value, processor);
	}

	protected Prop<Double> create(String name, Double value,
			ValueProcessor<Double> processor) {
		return features.create(name, value, processor);
	}

	protected Prop<Float> create(String name, Float value,
			ValueProcessor<Float> processor) {
		return features.create(name, value, processor);
	}

	protected Prop<ImmutableIcon> create(String name, ImmutableIcon value,
			ValueProcessor<ImmutableIcon> processor) {
		return features.create(name, value, processor);
	}

	protected Prop<Integer> create(String name, Integer value,
			ValueProcessor<Integer> processor) {
		return features.create(name, value, processor);
	}

	protected Prop<Long> create(String name, Long value,
			ValueProcessor<Long> processor) {
		return features.create(name, value, processor);
	}

	protected Prop<Short> create(String name, Short value,
			ValueProcessor<Short> processor) {
		return features.create(name, value, processor);
	}

	protected <J, S> MapProp<J, S> editableMap(Class<J> keyClass,
			Class<S> valueClass, String name, CMap<J, S> data) {
		return features.editableMap(keyClass, valueClass, name, data);
	}

	protected <J, S> MapProp<J, S> editableMap(Class<J> keyClass,
			Class<S> valueClass, String name) {
		return features.editableMap(keyClass, valueClass, name);
	}

	protected Prop<DateTime> create(String name, DateTime value) {
		return features.create(name, value);
	}

	protected <S> ListPropDefault<S> createList(
			Class<S> contentsClass, String name, CList<S> data) {
		return features.createList(contentsClass, name, data);
	}

	protected <S> ListProp<S> createList(
			Class<S> contentsClass, String name) {
		return features.createList(contentsClass, name);
	}

	protected Prop<DateTime> editable(String name, DateTime value) {
		return features.editable(name, value);
	}

	protected <S> ListPropDefault<S> editableList(
			Class<S> contentsClass, String name, CList<S> data) {
		return features.editableList(contentsClass, name, data);
	}

	protected <S> ListProp<S> editableList(
			Class<S> contentsClass, String name) {
		return features.editableList(contentsClass, name);
	}

	protected <S> SetProp<S> editableSet(Class<S> clazz, String name) {
		return features.editableSet(clazz, name);
	}

	protected <S> SetProp<S> editableSet(Class<S> clazz, String name,
			CSet<S> data) {
		return features.editableSet(clazz, name, data);
	}
	
	protected <S extends Enum<S>> PropImmutable<S> editable(Class<S> clazz, String name, S value) {
		return features.editable(clazz, name, value);
	}
	
	protected <S extends Bean> Prop<S> editable(Class<S> clazz,
			String name, S value) {
		return features.editable(clazz, name, value);
	}

	protected <S> Prop<S> createSuper(Class<S> clazz,
			String name, Prop<? extends S> core) {
		return features.createSuper(clazz, name, core);
	}

	protected <S extends Changeable> Prop<S> editable(
			Class<S> clazz, String name, S value) {
		return features.editable(clazz, name, value);
	}
	
	protected Prop<Boolean> editable(String name, Boolean value) {
		return features.editable(name, value);
	}

	protected Prop<Byte> editable(String name, Byte value) {
		return features.editable(name, value);
	}

	protected Prop<Double> editable(String name, Double value) {
		return features.editable(name, value);
	}

	protected Prop<Float> editable(String name, Float value) {
		return features.editable(name, value);
	}

	protected Prop<Integer> editable(String name, Integer value) {
		return features.editable(name, value);
	}

	protected Prop<Long> editable(String name, Long value) {
		return features.editable(name, value);
	}

	protected Prop<Short> editable(String name, Short value) {
		return features.editable(name, value);
	}

	protected Prop<String> editable(String name, String value) {
		return features.editable(name, value);
	}

	protected Prop<String> create(String name, String value, ValueProcessor<String> processor) {
		return features.create(name, value, processor);
	}

	protected Prop<ImmutableIcon> editable(String name, ImmutableIcon value) {
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

	protected <T extends Immutable> Prop<T> create(Class<T> clazz, String name, T value) {
		return features.create(clazz, name, value);
	}

	protected Prop<Color> create(String name, Color value) {
		return features.create(name, value);
	}

	protected <S extends Immutable> Prop<S> editable(Class<S> clazz, String name, S value) {
		return features.editable(clazz, name, value);
	}

	protected Prop<Color> editable(String name, Color value) {
		return features.editable(name, value);
	}

	protected <P extends Prop<S>, S> P addProp(P prop) {
		return features.add(prop);
	}
	
	protected <S> SetProp<S> createSet(Class<S> clazz, String name, CSet<S> data) {
		return features.createSet(clazz, name, data);
	}

	protected <S> SetProp<S> createSet(Class<S> clazz, String name) {
		return features.createSet(clazz, name);
	}

	protected <J, S> MapProp<J, S> createMap(Class<J> keyClass,
			Class<S> valueClass, String name, CMap<J, S> data) {
		return features.createMap(keyClass, valueClass, name, data);
	}

	protected <J, S> MapProp<J, S> createMap(Class<J> keyClass,
			Class<S> valueClass, String name) {
		return features.createMap(keyClass, valueClass, name);
	}

	protected Prop<Double> ranged(String name, double value, double low,
			double high) {
		return features.ranged(name, value, low, high);
	}

	protected Prop<Integer> ranged(String name, int value, int low, int high) {
		return features.ranged(name, value, low, high);
	}

	//Present a slightly easier interface for building path props

	/**
	 * Make a builder that will build an {@link PathProp} by use of
	 * {@link PathPropBuilder#via(PropName)} and {@link PathPropBuilder#to(PropName)},
	 * etc. The {@link PathProp} will be editable, and will pass any values through to the
	 * mirrored prop - note they may still be rejected by the mirrored prop itself.
	 * <br />
	 * When the {@link PathProp} is built and returned, it will be added to the props of 
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
	protected <R extends Bean, T> PathPropBuilder<R, R, T> editableFrom(String name, Class<T> clazz, R pathRoot) {
		return new BeanPathPropBuilder<R, R, T>(name, clazz, pathRoot, BeanPathBuilder.<R>create(), AcceptProcessor.<T>get());
	}

	/**
	 * Make a builder that will build an {@link PathProp} by use of
	 * {@link PathPropBuilder#via(PropName)} and {@link PathPropBuilder#to(PropName)},
	 * etc. The {@link PathProp} will be read only, regardless of the {@link Prop} it mirrors.
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
	protected <R extends Bean, P extends Prop<T>, T> PathPropBuilder<R, R, T> from(String name, Class<T> clazz, R pathRoot) {
		return new BeanPathPropBuilder<R, R, T>(name, clazz, pathRoot, BeanPathBuilder.<R>create(), ReadOnlyProcessor.<T>get());
	}
	
	/**
	 * This class just adapts {@link PathPropBuilder} so that the {@link PathProp} is added to this
	 * {@link BeanDefault} when it is completed. 
	 */
	private class BeanPathPropBuilder<R extends Bean, E extends Bean, T> extends PathPropBuilder<R, E, T>{
		
		private BeanPathPropBuilder(PropName<T> name, R pathRoot,
				BeanPathBuilder<R, E> builder, ValueProcessor<T> processor) {
			super(name, pathRoot, builder, processor);
		}

		private BeanPathPropBuilder(String name, Class<T> clazz,
				R pathRoot, BeanPathBuilder<R, E> builder, ValueProcessor<T> processor) {
			super(name, clazz, pathRoot, builder, processor);
		}

		@Override
		public <F extends Bean> PathPropBuilder<R, F, T> via(
				PropName<F> nextName) {
			return new BeanPathPropBuilder<R, F, T>(name, pathRoot, builder.via(nextName), processor);
		}

		@Override
		public <F extends Bean> PathPropBuilder<R, F, T> via(Transformer<? super E, ? extends Prop<F>> nextTransform) {
			return new BeanPathPropBuilder<R, F, T>(name, pathRoot, builder.via(nextTransform), processor);
		}

		
		@Override
		public PathProp<R, T> to(PropName<T> lastName) {
			PathProp<R,T> pathProp = super.to(lastName);
			addProp(pathProp);
			return pathProp;
		}

		@Override
		public PathProp<R, T> to(Transformer<? super E, ? extends Prop<T>> lastTransform) {
			PathProp<R,T> pathProp = super.to(lastTransform);
			addProp(pathProp);
			return pathProp;
		}		

	}

}
