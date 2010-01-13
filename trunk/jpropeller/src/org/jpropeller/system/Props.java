package org.jpropeller.system;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.jpropeller.calculation.Calculation;
import org.jpropeller.collection.CList;
import org.jpropeller.collection.CMap;
import org.jpropeller.collection.impl.CListCalculated;
import org.jpropeller.collection.impl.CMapCalculated;
import org.jpropeller.comparison.ComparisonType;
import org.jpropeller.comparison.Filters;
import org.jpropeller.concurrency.CancellableResponse;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.calculated.impl.BuildCalculation;
import org.jpropeller.properties.calculated.impl.CalculatedProp;
import org.jpropeller.properties.change.ChangeSystem;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.Immutable;
import org.jpropeller.properties.changeable.impl.ChangeablePropDefault;
import org.jpropeller.properties.immutable.impl.PropImmutable;
import org.jpropeller.properties.impl.SuperClassProp;
import org.jpropeller.properties.impl.ViewProp;
import org.jpropeller.properties.list.impl.ListPropDefault;
import org.jpropeller.properties.map.impl.MapPropDefault;
import org.jpropeller.properties.values.ValueProcessor;
import org.jpropeller.properties.values.Values;
import org.jpropeller.system.impl.PropSystemDefault;
import org.jpropeller.task.Task;
import org.jpropeller.task.impl.BuildTask;
import org.jpropeller.transformer.Transformer;
import org.jpropeller.ui.impl.ImmutableIcon;
import org.jpropeller.util.NoInstanceAvailableException;
import org.jpropeller.util.Source;

/**
 * Central source for System-wide aspects of JPropeller 
 */
public class Props {

	//Static class
	private Props(){}
	
	private static PropSystem propSystem = new PropSystemDefault();
	
	/**
	 * Get the system-wide {@link PropSystem}, this must not
	 * change after it is first called.
	 * @return
	 * 		The {@link PropSystem}
	 */
	public static PropSystem getPropSystem() {
		return propSystem;
	}
	
	/**
	 * Convenience method for {@link ChangeSystem#acquire()} on the
	 * {@link ChangeSystem}
	 */
	public static void acquire() {
		getPropSystem().getChangeSystem().acquire();
	}

	/**
	 * Convenience method for {@link ChangeSystem#release()} on the
	 * {@link ChangeSystem}
	 */
	public static void release() {
		getPropSystem().getChangeSystem().release();
	}


	//Methods for creating various Props
	
	/**
	 * Make a {@link Prop} containing a {@link CListCalculated}
	 * @param <T>				The type of list contents
	 * @param contentsClass		The class of list contents
	 * @param name				The name of the {@link Prop}
	 * @param calculation		The {@link Calculation} giving list contents
	 * @return					The {@link Prop}
	 */
	public static <T> Prop<CList<T>> calculatedList(Class<T> contentsClass, String name, Calculation<List<T>> calculation) {
		return ListPropDefault.calculated(contentsClass, name, calculation);
	}
	
	/**
	 * Make a {@link Prop} containing a {@link CMapCalculated}
	 * @param <K>				The type of map keys
	 * @param <V>				The type of value keys
	 * @param keyClass			The class of map keys
	 * @param valueClass		The class of map values
	 * @param name				The name of the {@link Prop}
	 * @param calculation		The {@link Calculation} giving map contents
	 * @return					The {@link Prop}
	 */
	public static <K, V> Prop<CMap<K, V>> calculatedMap(Class<K> keyClass, Class<V> valueClass, String name, Calculation<Map<K, V>> calculation) {
		return MapPropDefault.calculated(keyClass, valueClass, name, calculation);
	}
	
	/**
	 * Make a {@link Prop} containing the result of a {@link Calculation}
	 * @param <T>				The type of {@link Prop} contents
	 * @param contentsClass		The class of {@link Prop} contents
	 * @param name				The name of the {@link Prop}
	 * @param calculation		The {@link Calculation} giving list contents
	 * @return					The {@link Prop}
	 */
	public static <T> Prop<T> calculated(Class<T> contentsClass, String name, Calculation<T> calculation) {
		return new CalculatedProp<T>(PropName.create(contentsClass, name), calculation);
	}

	/**
	 * Make a new {@link SuperClassProp} with a given
	 * core prop and name, and add to this bean.
	 * @param <S>		The type of value
	 * @param clazz		The class of value
	 * @param name		The name of the {@link Prop}
	 * @param core		The core for the {@link SuperClassProp}
	 * @return			The new {@link Prop}
	 */
	public static <S> Prop<S> createSuper(Class<S> clazz,
			String name, Prop<? extends S> core){
		return new SuperClassProp<S>(PropName.create(clazz, name), core);
	}
	
	/**
	 * Make a new read-only prop with an {@link Immutable} value
	 * @param <T>
	 * 		The type of {@link Immutable} value in the prop 
	 * @param name
	 * 		The name of the prop
	 * @param clazz 
	 * 		The class of {@link Immutable} value in the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop
	 */
	public static <T extends Immutable> Prop<T> create(Class<T> clazz, String name, T value) {
		return PropImmutable.create(clazz, name, value);
	}

	/**
	 * Make a new editable prop with an {@link Immutable} value
	 * @param <T>
	 * 		The type of {@link Immutable} value in the prop 
	 * @param name
	 * 		The name of the prop
	 * @param clazz 
	 * 		The class of {@link Immutable} value in the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop
	 */
	public static <T extends Immutable> Prop<T> editable(Class<T> clazz, String name, T value) {
		return PropImmutable.editable(clazz, name, value);
	}

	/**
	 * Make a new read-only prop with a {@link Changeable} value
	 * @param <T>
	 * 		The type of {@link Changeable} value in the prop 
	 * @param name
	 * 		The name of the prop
	 * @param clazz 
	 * 		The class of {@link Changeable} value in the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop
	 */
	public static <T extends Changeable> Prop<T> create(Class<T> clazz, String name, T value) {
		return ChangeablePropDefault.create(clazz, name, value);
	}

	/**
	 * Make a new editable prop with a {@link Changeable} value
	 * @param <T>
	 * 		The type of {@link Changeable} value in the prop 
	 * @param name
	 * 		The name of the prop
	 * @param clazz 
	 * 		The class of {@link Changeable} value in the prop
	 * @param value
	 * 		The value of the prop
	 * @return
	 * 		The new prop
	 */
	public static <T extends Changeable> Prop<T> editable(Class<T> clazz, String name, T value) {
		return ChangeablePropDefault.editable(clazz, name, value);
	}

	/**
	 * Make an unmodifiable (read only) view of a given {@link Prop}
	 * @param newName		The new string name for the {@link ViewProp}
	 * @param viewed		The {@link Prop} we are viewing
	 * @param <T>			The type of value in the {@link Prop}
	 * 
	 * @return				A read-only view of the {@link Prop}
	 */
	public final static <T> ViewProp<T> readOnly(String newName, Prop<T> viewed) {
		return ViewProp.unmodifiableProp(newName, viewed);
	}
	
	/**
	 * Make an unmodifiable (read only) view of a given {@link Prop},
	 * with the same name
	 * @param viewed		The {@link Prop} we are viewing
	 * @param <T>			The type of value in the {@link Prop}
	 * 
	 * @return				A read-only view of the {@link Prop}
	 */
	public final static <T> ViewProp<T> readOnly(Prop<T> viewed) {
		return ViewProp.unmodifiableProp(viewed.getName().getString(), viewed);
	}
	
	/**
	 * Make a target {@link Prop} that always contains a transformation
	 * (via a {@link Transformer}) of a source {@link Prop}
	 * @param <S>			The source value type
	 * @param <T>			The target value type
	 * @param targetClass	The target value class
	 * @param targetName	The target {@link Prop} name
	 * @param sourceProp	The source {@link Prop}
	 * @param transformer	The {@link Transformer} - takes values
	 * 						of source {@link Prop} and produces values
	 * 						of target {@link Prop}
	 * @return				The transformed {@link Prop}
	 */
	public final static <S, T> Prop<T> transformed(Class<T> targetClass, String targetName, final Prop<S> sourceProp, final Transformer<S, T> transformer) {
		return calculated(targetClass, targetName, sourceProp).returning(new Source<T>() {
			@Override
			public T get() throws NoInstanceAvailableException {
				return transformer.transform(sourceProp.get());
			}
		});
	}
	
	/**
	 * Make a builder for a {@link Calculation} operating on given inputs (sources).
	 * Calling {@link BuildCalculation#returning(Source)} on this
	 * builder will produce a {@link Calculation}
	 * @param inputs		The inputs (sources) of data for the {@link Calculation}
	 * @return				A {@link BuildCalculation}
     * @param <T> 			The type of result produced
	 */
	public static <T> BuildCalculation<T> calculationOn(Changeable... inputs) {
		return BuildCalculation.on(inputs);
	}

	/**
	 * Make a builder for a {@link CalculatedProp} operating on given inputs (sources).
	 * Calling {@link BuildCalculatedProp#returning(Source)} on this
	 * builder will produce a {@link CalculatedProp}
	 * @param clazz 		The class of {@link Changeable} value in the prop
	 * @param name			The name of the prop
	 * @param inputs		The inputs (sources) of data for the {@link Calculation}
	 * @return				A {@link BuildCalculatedProp} - use this to get the {@link CalculatedProp}
     * @param <T> 			The type of result produced
	 */
	public static <T> BuildCalculatedProp<T> calculated(Class<T> clazz, String name, Changeable... inputs) {
		return new BuildCalculatedProp<T>(clazz, name, BuildCalculation.<T>on(inputs));
	}

	/**
	 * Allows building of a {@link CalculatedProp}, just
	 * call {@link #returning(Source)}
	 *
	 * @param <T>	The type of calculated value
	 */
	public static class BuildCalculatedProp<T> {
		private final BuildCalculation<T> buildCalculation;
		private final Class<T> clazz;
		private final String name;
		private BuildCalculatedProp(Class<T> clazz, String name, BuildCalculation<T> buildCalculation) {
			this.clazz = clazz;
			this.name = name;
			this.buildCalculation = buildCalculation;
		}
		
		/**
		 * Call this method to produce a {@link CalculatedProp} based on the
		 * inputs provided to {@link Props#calculated(Class, String, Changeable...)},
		 * returning the values produced by the {@link Source}. Note that the {@link Source}
		 * must only use the values of the specified inputs.
		 * @param source	{@link Source} of results of calculation
		 * @return	The new {@link CalculatedProp}
		 */
		public Prop<T> returning(Source<T> source) {
			Calculation<T> calculation = buildCalculation.returning(source);
			return calculated(clazz, name, calculation); 
		}
	}
	
	/**
	 * Make a builder for a task operating on given sources.
	 * Calling {@link BuildTask#withResponse(CancellableResponse)} on this
	 * builder will produce a {@link Task}
	 * @param sources		The sources of data for the task
	 * @return				A {@link BuildTask}
	 */
	public static BuildTask taskOn(Changeable... sources) {
		return BuildTask.on(sources);
	}
	
	/**
	 * Create a {@link Prop} of {@link Integer} constrained
	 * to a given range, inclusive
	 * @param name		The {@link Prop}'s name
	 * @param value		The initial value
	 * @param low		The lowest accepted value
	 * @param high		The highest accepted value
	 * @return			A new constrained {@link Prop}
	 */
	public static Prop<Integer> ranged(String name, int value, int low, int high) {
		//Check we have a valid range, and value is in it
		if (value > high || value < low) {
			throw new IllegalArgumentException("Must have low <= value <= high");
		}
		return create(name, value, Values.range(Filters.comparison(ComparisonType.MORE_THAN_OR_EQUAL, low), Filters.comparison(ComparisonType.LESS_THAN_OR_EQUAL, high)));			
	}

	/**
	 * Create a {@link Prop} of {@link Double} constrained
	 * to a given range, inclusive
	 * @param name		The {@link Prop}'s name
	 * @param value		The initial value
	 * @param low		The lowest accepted value
	 * @param high		The highest accepted value
	 * @return			A new constrained {@link Prop}
	 */
	public static Prop<Double> ranged(String name, double value, double low, double high) {
		//Check we have a valid range, and value is in it
		if (value > high || value < low) {
			throw new IllegalArgumentException("Must have low <= value <= high");
		}
		return create(name, value, Values.range(Filters.comparison(ComparisonType.MORE_THAN_OR_EQUAL, low), Filters.comparison(ComparisonType.LESS_THAN_OR_EQUAL, high)));			
	}
	
	//Auto generated methods

	/**
	 * Make a new read-only String {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static Prop<String> create(String name, String value){
		return PropImmutable.create(name, value);
	}
	/**
	 * Make a new String {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @param processor	The processor used when new values are set
	 * @return			The new {@link Prop}
	 */
	public final static Prop<String> create(String name, String value, ValueProcessor<String> processor){
		return PropImmutable.create(name, value, processor);
	}
	/**
	 * Make a new editable String {@link Prop}, accepting all values
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static Prop<String> editable(String name, String value){
		return PropImmutable.editable(name, value);
	}
	/**
	 * Make a {@link Prop} containing the result of a {@link Calculation}
	 * @param name				The name of the {@link Prop}
	 * @param calculation		The {@link Calculation} giving list contents
	 * @return					The {@link Prop}
	 */
	public final static Prop<String> calculatedString(String name, Calculation<String> calculation) {
		return new CalculatedProp<String>(PropName.create(String.class, name), calculation);
	}
	/**
	 * Make a new read-only Boolean {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static Prop<Boolean> create(String name, Boolean value){
		return PropImmutable.create(name, value);
	}
	/**
	 * Make a new Boolean {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @param processor	The processor used when new values are set
	 * @return			The new {@link Prop}
	 */
	public final static Prop<Boolean> create(String name, Boolean value, ValueProcessor<Boolean> processor){
		return PropImmutable.create(name, value, processor);
	}
	/**
	 * Make a new editable Boolean {@link Prop}, accepting all values
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static Prop<Boolean> editable(String name, Boolean value){
		return PropImmutable.editable(name, value);
	}
	/**
	 * Make a {@link Prop} containing the result of a {@link Calculation}
	 * @param name				The name of the {@link Prop}
	 * @param calculation		The {@link Calculation} giving list contents
	 * @return					The {@link Prop}
	 */
	public final static Prop<Boolean> calculatedBoolean(String name, Calculation<Boolean> calculation) {
		return new CalculatedProp<Boolean>(PropName.create(Boolean.class, name), calculation);
	}
	/**
	 * Make a new read-only Byte {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static Prop<Byte> create(String name, Byte value){
		return PropImmutable.create(name, value);
	}
	/**
	 * Make a new Byte {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @param processor	The processor used when new values are set
	 * @return			The new {@link Prop}
	 */
	public final static Prop<Byte> create(String name, Byte value, ValueProcessor<Byte> processor){
		return PropImmutable.create(name, value, processor);
	}
	/**
	 * Make a new editable Byte {@link Prop}, accepting all values
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static Prop<Byte> editable(String name, Byte value){
		return PropImmutable.editable(name, value);
	}
	/**
	 * Make a {@link Prop} containing the result of a {@link Calculation}
	 * @param name				The name of the {@link Prop}
	 * @param calculation		The {@link Calculation} giving list contents
	 * @return					The {@link Prop}
	 */
	public final static Prop<Byte> calculatedByte(String name, Calculation<Byte> calculation) {
		return new CalculatedProp<Byte>(PropName.create(Byte.class, name), calculation);
	}
	/**
	 * Make a new read-only Short {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static Prop<Short> create(String name, Short value){
		return PropImmutable.create(name, value);
	}
	/**
	 * Make a new Short {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @param processor	The processor used when new values are set
	 * @return			The new {@link Prop}
	 */
	public final static Prop<Short> create(String name, Short value, ValueProcessor<Short> processor){
		return PropImmutable.create(name, value, processor);
	}
	/**
	 * Make a new editable Short {@link Prop}, accepting all values
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static Prop<Short> editable(String name, Short value){
		return PropImmutable.editable(name, value);
	}
	/**
	 * Make a {@link Prop} containing the result of a {@link Calculation}
	 * @param name				The name of the {@link Prop}
	 * @param calculation		The {@link Calculation} giving list contents
	 * @return					The {@link Prop}
	 */
	public final static Prop<Short> calculatedShort(String name, Calculation<Short> calculation) {
		return new CalculatedProp<Short>(PropName.create(Short.class, name), calculation);
	}
	/**
	 * Make a new read-only Integer {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static Prop<Integer> create(String name, Integer value){
		return PropImmutable.create(name, value);
	}
	/**
	 * Make a new Integer {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @param processor	The processor used when new values are set
	 * @return			The new {@link Prop}
	 */
	public final static Prop<Integer> create(String name, Integer value, ValueProcessor<Integer> processor){
		return PropImmutable.create(name, value, processor);
	}
	/**
	 * Make a new editable Integer {@link Prop}, accepting all values
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static Prop<Integer> editable(String name, Integer value){
		return PropImmutable.editable(name, value);
	}
	/**
	 * Make a {@link Prop} containing the result of a {@link Calculation}
	 * @param name				The name of the {@link Prop}
	 * @param calculation		The {@link Calculation} giving list contents
	 * @return					The {@link Prop}
	 */
	public final static Prop<Integer> calculatedInteger(String name, Calculation<Integer> calculation) {
		return new CalculatedProp<Integer>(PropName.create(Integer.class, name), calculation);
	}
	/**
	 * Make a new read-only Long {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static Prop<Long> create(String name, Long value){
		return PropImmutable.create(name, value);
	}
	/**
	 * Make a new Long {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @param processor	The processor used when new values are set
	 * @return			The new {@link Prop}
	 */
	public final static Prop<Long> create(String name, Long value, ValueProcessor<Long> processor){
		return PropImmutable.create(name, value, processor);
	}
	/**
	 * Make a new editable Long {@link Prop}, accepting all values
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static Prop<Long> editable(String name, Long value){
		return PropImmutable.editable(name, value);
	}
	/**
	 * Make a {@link Prop} containing the result of a {@link Calculation}
	 * @param name				The name of the {@link Prop}
	 * @param calculation		The {@link Calculation} giving list contents
	 * @return					The {@link Prop}
	 */
	public final static Prop<Long> calculatedLong(String name, Calculation<Long> calculation) {
		return new CalculatedProp<Long>(PropName.create(Long.class, name), calculation);
	}
	/**
	 * Make a new read-only Float {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static Prop<Float> create(String name, Float value){
		return PropImmutable.create(name, value);
	}
	/**
	 * Make a new Float {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @param processor	The processor used when new values are set
	 * @return			The new {@link Prop}
	 */
	public final static Prop<Float> create(String name, Float value, ValueProcessor<Float> processor){
		return PropImmutable.create(name, value, processor);
	}
	/**
	 * Make a new editable Float {@link Prop}, accepting all values
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static Prop<Float> editable(String name, Float value){
		return PropImmutable.editable(name, value);
	}
	/**
	 * Make a {@link Prop} containing the result of a {@link Calculation}
	 * @param name				The name of the {@link Prop}
	 * @param calculation		The {@link Calculation} giving list contents
	 * @return					The {@link Prop}
	 */
	public final static Prop<Float> calculatedFloat(String name, Calculation<Float> calculation) {
		return new CalculatedProp<Float>(PropName.create(Float.class, name), calculation);
	}
	/**
	 * Make a new read-only Double {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static Prop<Double> create(String name, Double value){
		return PropImmutable.create(name, value);
	}
	/**
	 * Make a new Double {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @param processor	The processor used when new values are set
	 * @return			The new {@link Prop}
	 */
	public final static Prop<Double> create(String name, Double value, ValueProcessor<Double> processor){
		return PropImmutable.create(name, value, processor);
	}
	/**
	 * Make a new editable Double {@link Prop}, accepting all values
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static Prop<Double> editable(String name, Double value){
		return PropImmutable.editable(name, value);
	}
	/**
	 * Make a {@link Prop} containing the result of a {@link Calculation}
	 * @param name				The name of the {@link Prop}
	 * @param calculation		The {@link Calculation} giving list contents
	 * @return					The {@link Prop}
	 */
	public final static Prop<Double> calculatedDouble(String name, Calculation<Double> calculation) {
		return new CalculatedProp<Double>(PropName.create(Double.class, name), calculation);
	}
	/**
	 * Make a new read-only DateTime {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static Prop<DateTime> create(String name, DateTime value){
		return PropImmutable.create(name, value);
	}
	/**
	 * Make a new DateTime {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @param processor	The processor used when new values are set
	 * @return			The new {@link Prop}
	 */
	public final static Prop<DateTime> create(String name, DateTime value, ValueProcessor<DateTime> processor){
		return PropImmutable.create(name, value, processor);
	}
	/**
	 * Make a new editable DateTime {@link Prop}, accepting all values
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static Prop<DateTime> editable(String name, DateTime value){
		return PropImmutable.editable(name, value);
	}
	/**
	 * Make a {@link Prop} containing the result of a {@link Calculation}
	 * @param name				The name of the {@link Prop}
	 * @param calculation		The {@link Calculation} giving list contents
	 * @return					The {@link Prop}
	 */
	public final static Prop<DateTime> calculatedDateTime(String name, Calculation<DateTime> calculation) {
		return new CalculatedProp<DateTime>(PropName.create(DateTime.class, name), calculation);
	}
	/**
	 * Make a new read-only Color {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static Prop<Color> create(String name, Color value){
		return PropImmutable.create(name, value);
	}
	/**
	 * Make a new Color {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @param processor	The processor used when new values are set
	 * @return			The new {@link Prop}
	 */
	public final static Prop<Color> create(String name, Color value, ValueProcessor<Color> processor){
		return PropImmutable.create(name, value, processor);
	}
	/**
	 * Make a new editable Color {@link Prop}, accepting all values
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static Prop<Color> editable(String name, Color value){
		return PropImmutable.editable(name, value);
	}
	/**
	 * Make a {@link Prop} containing the result of a {@link Calculation}
	 * @param name				The name of the {@link Prop}
	 * @param calculation		The {@link Calculation} giving list contents
	 * @return					The {@link Prop}
	 */
	public final static Prop<Color> calculatedColor(String name, Calculation<Color> calculation) {
		return new CalculatedProp<Color>(PropName.create(Color.class, name), calculation);
	}
	/**
	 * Make a new read-only ImmutableIcon {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static Prop<ImmutableIcon> create(String name, ImmutableIcon value){
		return PropImmutable.create(name, value);
	}
	/**
	 * Make a new ImmutableIcon {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @param processor	The processor used when new values are set
	 * @return			The new {@link Prop}
	 */
	public final static Prop<ImmutableIcon> create(String name, ImmutableIcon value, ValueProcessor<ImmutableIcon> processor){
		return PropImmutable.create(name, value, processor);
	}
	/**
	 * Make a new editable ImmutableIcon {@link Prop}, accepting all values
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static Prop<ImmutableIcon> editable(String name, ImmutableIcon value){
		return PropImmutable.editable(name, value);
	}
	/**
	 * Make a {@link Prop} containing the result of a {@link Calculation}
	 * @param name				The name of the {@link Prop}
	 * @param calculation		The {@link Calculation} giving list contents
	 * @return					The {@link Prop}
	 */
	public final static Prop<ImmutableIcon> calculatedImmutableIcon(String name, Calculation<ImmutableIcon> calculation) {
		return new CalculatedProp<ImmutableIcon>(PropName.create(ImmutableIcon.class, name), calculation);
	}
}
