package org.jpropeller.bean.impl;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.jpropeller.bean.Bean;
import org.jpropeller.bean.BuildAndAddCalculatedListProp;
import org.jpropeller.bean.BuildAndAddCalculatedProp;
import org.jpropeller.bean.ExtendedBeanFeatures;
import org.jpropeller.bean.MutableBeanFeatures;
import org.jpropeller.calculation.Calculation;
import org.jpropeller.collection.CList;
import org.jpropeller.collection.CMap;
import org.jpropeller.collection.CSet;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.calculated.impl.BuildCalculation;
import org.jpropeller.properties.calculated.impl.BuildListCalculation;
import org.jpropeller.properties.calculated.impl.CalculatedProp;
import org.jpropeller.properties.calculated.impl.CalculationDefault;
import org.jpropeller.properties.calculated.impl.ListCalculation;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.Immutable;
import org.jpropeller.properties.changeable.impl.ChangeablePropDefault;
import org.jpropeller.properties.immutable.impl.PropImmutable;
import org.jpropeller.properties.impl.SuperClassProp;
import org.jpropeller.properties.impl.ViewProp;
import org.jpropeller.properties.list.ListProp;
import org.jpropeller.properties.list.impl.ListPropDefault;
import org.jpropeller.properties.map.MapProp;
import org.jpropeller.properties.map.impl.MapPropDefault;
import org.jpropeller.properties.set.SetProp;
import org.jpropeller.properties.set.impl.SetPropDefault;
import org.jpropeller.properties.values.ValueProcessor;
import org.jpropeller.system.Props;
import org.jpropeller.ui.impl.ImmutableIcon;
import org.jpropeller.util.Source;

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

	@Override
	public <T> ViewProp<T> readOnly(Prop<T> viewed) {
		return add(Props.readOnly(viewed));
	}

	@Override
	public <T> ViewProp<T> readOnly(String newName, Prop<T> viewed) {
		return add(Props.readOnly(newName, viewed));
	}
	
	@Override
	public <I extends Changeable, S> Prop<S> calculated(Class<S> clazz, String name, ListCalculation<I, S> calc, I firstInput, I... additionalInputs) {
		Prop<S> prop = new CalculatedProp<S>(
							PropName.create(clazz, name), 
							new CalculationDefault<I, S>(calc, firstInput, additionalInputs));
		return add(prop);
	}

	@Override
	public <T> BuildAndAddCalculatedProp<T> calculated(Class<T> clazz,
			String name, Changeable... inputs) {
		return new CalcBuilder<T>(clazz, name, BuildCalculation.<T>on(inputs), this, false, null);
	}

	@Override
	public <T> BuildAndAddCalculatedProp<T> calculatedBackground(Class<T> clazz,
			String name, T initialValue, Changeable... inputs) {
		return new CalcBuilder<T>(clazz, name, BuildCalculation.<T>on(inputs), this, true, initialValue);
	}

	@Override
	public <T> BuildAndAddCalculatedListProp<T> calculatedListOn(
			Class<T> clazz, String name, Changeable... inputs) {
		return new CalcListBuilder<T>(clazz, name, BuildListCalculation.<T>on(inputs), this);
	}
	
	
	/**
	 * Allows building of a {@link Prop}, containing a 
	 * calculated list of values, just call {@link #returning(Source)}
	 *
	 * @param <T>	The type of value in the calculated list
	 */
	private static class CalcListBuilder<T> implements BuildAndAddCalculatedListProp<T> {
		private final BuildListCalculation<T> buildCalculation;
		private final Class<T> clazz;
		private final String name;
		private final ExtendedBeanFeaturesDefault features;
		
		private CalcListBuilder(Class<T> clazz, String name, BuildListCalculation<T> buildCalculation, ExtendedBeanFeaturesDefault features) {
			this.clazz = clazz;
			this.name = name;
			this.buildCalculation = buildCalculation;
			this.features = features;
		}

		@Override
		public Prop<CList<T>> returning(Source<List<T>> source) {
			Calculation<List<T>> calculation = buildCalculation.returning(source);
			return features.add(Props.calculatedList(clazz, name, calculation)); 				
		}
	}
	
	
	
	
	/**
	 * Allows building of a {@link CalculatedProp}, just
	 * call {@link #returning(Source)}
	 *
	 * @param <T>	The type of calculated value
	 */
	private static class CalcBuilder<T> implements BuildAndAddCalculatedProp<T> {
		private final BuildCalculation<T> buildCalculation;
		private final Class<T> clazz;
		private final String name;
		private final ExtendedBeanFeaturesDefault features;
		private final boolean background;
		private final T initialValue;
		
		private CalcBuilder(Class<T> clazz, String name, BuildCalculation<T> buildCalculation, ExtendedBeanFeaturesDefault features, boolean background, T initialValue) {
			this.clazz = clazz;
			this.name = name;
			this.buildCalculation = buildCalculation;
			this.features = features;
			this.background = background;
			this.initialValue = initialValue;
		}

		@Override
		public Prop<T> returning(Source<T> source) {
			Calculation<T> calculation = buildCalculation.returning(source);
			if (background) {
				return features.add(Props.calculatedBackground(clazz, name, calculation, initialValue)); 
			} else {
				return features.add(Props.calculated(clazz, name, calculation)); 				
			}
		}
	}
	
	public <S extends Enum<S>> PropImmutable<S> editable(Class<S> clazz, String name, S value) {
		return add(PropImmutable.editable(clazz, name, value));
	}

	public <S extends Changeable> Prop<S> editable(Class<S> clazz, String name, S value) {
		return add(ChangeablePropDefault.editable(clazz, name, value));
	}

	public <S extends Immutable> Prop<S> editable(Class<S> clazz, String name, S value) {
		return add(PropImmutable.editable(clazz, name, value));
	}
	
	public <S> ListProp<S> createList(Class<S> contentsClass, String name) {
		return add(ListPropDefault.create(contentsClass, name));
	}

	public <S> ListPropDefault<S> createList(Class<S> contentsClass, String name, CList<S> data) {
		return add(ListPropDefault.create(contentsClass, name, data));
	}

	public <S> ListPropDefault<S> calculatedList(Class<S> contentsClass,
			String name, Calculation<List<S>> calculation) {
		return add(ListPropDefault.calculated(contentsClass, name, calculation));
	}
	
	public <S> ListPropDefault<S> editableList(Class<S> contentsClass, String name, CList<S> data) {
		return add(ListPropDefault.editable(contentsClass, name, data));
	}

	public <S> ListPropDefault<S> editableList(Class<S> contentsClass, String name) {
		return add(ListPropDefault.editable(contentsClass, name));
	}

	public <J, S> MapProp<J, S> editableMap(Class<J> keyClass, Class<S> valueClass, String name, CMap<J, S> data) {
		return add(MapPropDefault.editable(keyClass, valueClass, name, data));
	}
	
	public <J, S> MapProp<J, S> editableMap(Class<J> keyClass, Class<S> valueClass, String name) {
		return add(MapPropDefault.<J, S>editable(keyClass, valueClass, name));
	}

//	public <J, S> MapProp<J, S> editableMap(Class<J> keyClass, Class<S> valueClass, String name, boolean trackKeys) {
//		return add(MapPropDefault.<J, S>editable(keyClass, valueClass, name, trackKeys));
//	}
	
	public <J, S> MapProp<J, S> createMap(Class<J> keyClass, Class<S> valueClass, String name, CMap<J, S> data) {
		return add(MapPropDefault.create(keyClass, valueClass, name, data));
	}
	
	public <J, S> MapProp<J, S> createMap(Class<J> keyClass, Class<S> valueClass, String name) {
		return add(MapPropDefault.<J, S>create(keyClass, valueClass, name));
	}

	public <S> SetProp<S> editableSet(Class<S> clazz, String name, CSet<S> data) {
		return add(SetPropDefault.editable(clazz, name, data));
	}
	
	public <S> SetProp<S> editableSet(Class<S> clazz, String name) {
		return add(SetPropDefault.editable(clazz, name));
	}

	public <S> SetProp<S> createSet(Class<S> clazz, String name, CSet<S> data) {
		return add(SetPropDefault.create(clazz, name, data));
	}
	
	public <S> SetProp<S> createSet(Class<S> clazz, String name) {
		return add(SetPropDefault.create(clazz, name));
	}

	public <S extends Enum<S>> PropImmutable<S> create(Class<S> clazz, String name, S value) {
		return add(PropImmutable.create(clazz, name, value));
	}

	public <T extends Immutable> Prop<T> create(Class<T> clazz, String name, T value) {
		return add(PropImmutable.create(clazz, name, value));
	}

	public <S extends Changeable> Prop<S> create(Class<S> clazz, String name, S value) {
		return add(ChangeablePropDefault.create(clazz, name, value));
	}

	public <S> Prop<S> createSuper(Class<S> clazz,
			String name, Prop<? extends S> core){
		return add(new SuperClassProp<S>(PropName.create(clazz, name), core));
	}
	
	public Prop<Integer> ranged(String name, int value, int low, int high) {
		return add(Props.ranged(name, value, low, high));			
	}

	public Prop<Double> ranged(String name, double value, double low, double high) {
		return add(Props.ranged(name, value, low, high));
	}


	/////////////////////////////////////////////////////////////////
	//                                                             //
	//	Delegated methods										   //
	//                                                             //
	/////////////////////////////////////////////////////////////////

	public <R extends Prop<S>, S> R add(R prop) {
		return delegate.add(prop);
	}

	public void addChangeableListener(Changeable listener) {
		delegate.addChangeableListener(listener);
	}

	public void addListener(ChangeListener listener) {
		delegate.addListener(listener);
	}

	public Iterable<Changeable> changeableListenerList() {
		return delegate.changeableListenerList();
	}

	public <S> Prop<S> get(PropName<S> name) {
		return delegate.get(name);
	}

	public Bean getBean() {
		return delegate.getBean();
	}

	public List<Prop<?>> getList() {
		return delegate.getList();
	}

	public <S> Prop<S> getUnsafe(PropName<S> name) {
		return delegate.getUnsafe(name);
	}

	public Change internalChange(Changeable changed, Change change,
			List<Changeable> initial, Map<Changeable, Change> allChanges) {
		return delegate.internalChange(changed, change, initial, allChanges);
	}

	public Iterator<Prop<?>> iterator() {
		return delegate.iterator();
	}

	public Iterable<ChangeListener> listenerList() {
		return delegate.listenerList();
	}

	public void removeChangeableListener(Changeable listener) {
		delegate.removeChangeableListener(listener);
	}

	public void removeListener(ChangeListener listener) {
		delegate.removeListener(listener);
	}
	
	public String getMetadata(String key) {
		return delegate.getMetadata(key);
	}

	public boolean hasMetadata(String key) {
		return delegate.hasMetadata(key);
	}

	public String putMetadata(String key, String value) {
		return delegate.putMetadata(key, value);
	}

	public String removeMetadata(String key) {
		return delegate.removeMetadata(key);
	}
	
	public String putMetadata(String key) {
		return putMetadata(key, "true");
	}

	//#########################################################################
	//###																	###
	//###  Auto-generated factory methods for Props with immutable content	###
	//###																	###
	//#########################################################################
	
	@Override
	public Prop<String> create(String name, String value) {
		return add(PropImmutable.create(name, value));
	}
	@Override
	public Prop<String> create(String name, String value, ValueProcessor<String> processor) {
		return add(PropImmutable.create(name, value, processor));
	}
	@Override
	public Prop<String> editable(String name, String value) {
		return add(PropImmutable.editable(name, value));
	}
	@Override
	public Prop<Boolean> create(String name, Boolean value) {
		return add(PropImmutable.create(name, value));
	}
	@Override
	public Prop<Boolean> create(String name, Boolean value, ValueProcessor<Boolean> processor) {
		return add(PropImmutable.create(name, value, processor));
	}
	@Override
	public Prop<Boolean> editable(String name, Boolean value) {
		return add(PropImmutable.editable(name, value));
	}
	@Override
	public Prop<Byte> create(String name, Byte value) {
		return add(PropImmutable.create(name, value));
	}
	@Override
	public Prop<Byte> create(String name, Byte value, ValueProcessor<Byte> processor) {
		return add(PropImmutable.create(name, value, processor));
	}
	@Override
	public Prop<Byte> editable(String name, Byte value) {
		return add(PropImmutable.editable(name, value));
	}
	@Override
	public Prop<Short> create(String name, Short value) {
		return add(PropImmutable.create(name, value));
	}
	@Override
	public Prop<Short> create(String name, Short value, ValueProcessor<Short> processor) {
		return add(PropImmutable.create(name, value, processor));
	}
	@Override
	public Prop<Short> editable(String name, Short value) {
		return add(PropImmutable.editable(name, value));
	}
	@Override
	public Prop<Integer> create(String name, Integer value) {
		return add(PropImmutable.create(name, value));
	}
	@Override
	public Prop<Integer> create(String name, Integer value, ValueProcessor<Integer> processor) {
		return add(PropImmutable.create(name, value, processor));
	}
	@Override
	public Prop<Integer> editable(String name, Integer value) {
		return add(PropImmutable.editable(name, value));
	}
	@Override
	public Prop<Long> create(String name, Long value) {
		return add(PropImmutable.create(name, value));
	}
	@Override
	public Prop<Long> create(String name, Long value, ValueProcessor<Long> processor) {
		return add(PropImmutable.create(name, value, processor));
	}
	@Override
	public Prop<Long> editable(String name, Long value) {
		return add(PropImmutable.editable(name, value));
	}
	@Override
	public Prop<Float> create(String name, Float value) {
		return add(PropImmutable.create(name, value));
	}
	@Override
	public Prop<Float> create(String name, Float value, ValueProcessor<Float> processor) {
		return add(PropImmutable.create(name, value, processor));
	}
	@Override
	public Prop<Float> editable(String name, Float value) {
		return add(PropImmutable.editable(name, value));
	}
	@Override
	public Prop<Double> create(String name, Double value) {
		return add(PropImmutable.create(name, value));
	}
	@Override
	public Prop<Double> create(String name, Double value, ValueProcessor<Double> processor) {
		return add(PropImmutable.create(name, value, processor));
	}
	@Override
	public Prop<Double> editable(String name, Double value) {
		return add(PropImmutable.editable(name, value));
	}
	@Override
	public Prop<DateTime> create(String name, DateTime value) {
		return add(PropImmutable.create(name, value));
	}
	@Override
	public Prop<DateTime> create(String name, DateTime value, ValueProcessor<DateTime> processor) {
		return add(PropImmutable.create(name, value, processor));
	}
	@Override
	public Prop<DateTime> editable(String name, DateTime value) {
		return add(PropImmutable.editable(name, value));
	}
	@Override
	public Prop<Color> create(String name, Color value) {
		return add(PropImmutable.create(name, value));
	}
	@Override
	public Prop<Color> create(String name, Color value, ValueProcessor<Color> processor) {
		return add(PropImmutable.create(name, value, processor));
	}
	@Override
	public Prop<Color> editable(String name, Color value) {
		return add(PropImmutable.editable(name, value));
	}
	@Override
	public Prop<ImmutableIcon> create(String name, ImmutableIcon value) {
		return add(PropImmutable.create(name, value));
	}
	@Override
	public Prop<ImmutableIcon> create(String name, ImmutableIcon value, ValueProcessor<ImmutableIcon> processor) {
		return add(PropImmutable.create(name, value, processor));
	}
	@Override
	public Prop<ImmutableIcon> editable(String name, ImmutableIcon value) {
		return add(PropImmutable.editable(name, value));
	}
}
