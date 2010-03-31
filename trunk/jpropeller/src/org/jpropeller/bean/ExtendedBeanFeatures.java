package org.jpropeller.bean;

import java.awt.Color;
import java.util.List;

import org.joda.time.DateTime;
import org.jpropeller.calculation.Calculation;
import org.jpropeller.collection.CList;
import org.jpropeller.collection.CMap;
import org.jpropeller.collection.CSet;
import org.jpropeller.collection.impl.CListCalculated;
import org.jpropeller.collection.impl.CListDefault;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.calculated.impl.CalculatedProp;
import org.jpropeller.properties.calculated.impl.ListCalculation;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.Immutable;
import org.jpropeller.properties.immutable.impl.PropImmutable;
import org.jpropeller.properties.impl.SuperClassProp;
import org.jpropeller.properties.impl.ViewProp;
import org.jpropeller.properties.list.ListProp;
import org.jpropeller.properties.list.impl.ListPropDefault;
import org.jpropeller.properties.map.MapProp;
import org.jpropeller.properties.map.impl.MapPropDefault;
import org.jpropeller.properties.set.SetProp;
import org.jpropeller.properties.values.ValueProcessor;
import org.jpropeller.ui.impl.ImmutableIcon;
import org.jpropeller.util.Source;

/**
 * A {@link BeanFeatures} providing numerous convenience methods
 * for creating Props and adding them to the bean directly.
 * This is an interface that should only be visible to the {@link Bean}
 * itself - {@link Bean}s should only make their {@link BeanFeatures} available
 * externally as the basic {@link BeanFeatures} interface
 */
public interface ExtendedBeanFeatures extends MutableBeanFeatures{

	/**
	 * Make an unmodifiable (read only) view of a given {@link Prop},
	 * and add to the bean.
	 * @param newName		The new string name for the {@link ViewProp}
	 * @param viewed		The {@link Prop} we are viewing
	 * @param <T>			The type of value in the {@link Prop}
	 * 
	 * @return				A read-only view of the {@link Prop}
	 */
	public <T> ViewProp<T> readOnly(String newName, Prop<T> viewed);
	
	/**
	 * Make an unmodifiable (read only) view of a given {@link Prop},
	 * with the same name, and add to the bean.
	 * @param viewed		The {@link Prop} we are viewing
	 * @param <T>			The type of value in the {@link Prop}
	 * 
	 * @return				A read-only view of the {@link Prop}
	 */
	public <T> ViewProp<T> readOnly(Prop<T> viewed);
	
	/**
	 * Create a calculated prop, and add to this bean
	 * 
	 * @param <I>		The type of value in the inputs to the calculation
	 * @param <S>		The type of value in the prop
	 * @param clazz		The class of value in the prop
	 * @param name		The name of the prop
	 * @param calc		The calculation performed by the prop
	 * @param firstInput		The first input for the calculation
	 * @param additionalInputs	Any additional inputs for the calculation
	 * @return			A new calculated prop
	 */
	public <I extends Changeable, S> Prop<S> calculated(Class<S> clazz, String name, ListCalculation<I, S> calc, I firstInput, I... additionalInputs);

	/**
	 * Make a builder for a {@link CalculatedProp} operating on given inputs (sources).
	 * Calling {@link BuildAndAddCalculatedProp#returning(Source)} on this
	 * builder will produce a {@link CalculatedProp} and add to this bean.
	 * @param clazz 		The class of {@link Changeable} value in the prop
	 * @param name			The name of the prop
	 * @param inputs		The inputs (sources) of data for the {@link Calculation}
	 * @return				A {@link BuildAndAddCalculatedProp} - use this to get the {@link CalculatedProp} and add it to the bean.
     * @param <T> 			The type of result produced
	 */
	public <T> BuildAndAddCalculatedProp<T> calculated(Class<T> clazz, String name, Changeable... inputs);
	
	/**
	 * Make a builder for a {@link CalculatedProp} operating on given inputs (sources).
	 * Calling {@link BuildAndAddCalculatedProp#returning(Source)} on this
	 * builder will produce a {@link CalculatedProp} and add to this bean.
	 * The calculation will be performed in a background thread.
	 * @param clazz 		The class of {@link Changeable} value in the prop
	 * @param name			The name of the {@link Prop}
	 * @param initialValue	The initial value of the {@link Prop}
	 * @param inputs		The inputs (sources) of data for the {@link Calculation}
	 * @return				A {@link BuildAndAddCalculatedProp} - use this to get the {@link CalculatedProp} and add it to the bean.
     * @param <T> 			The type of result produced
	 */
	public <T> BuildAndAddCalculatedProp<T> calculatedBackground(Class<T> clazz, String name, T initialValue, Changeable... inputs);
	
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
	public <S extends Enum<S>> PropImmutable<S> editable(
			Class<S> clazz, String name, S value);

	/**
	 * Make a new {@link SuperClassProp} with a given
	 * core prop and name, and add to this bean.
	 * @param <S>		The type of value
	 * @param clazz		The class of value
	 * @param name		The name of the {@link Prop}
	 * @param core		The core for the {@link SuperClassProp}
	 * @return			The new {@link Prop}
	 */
	public <S> Prop<S> createSuper(Class<S> clazz,
			String name, Prop<? extends S> core);
	
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
	public <S extends Changeable> Prop<S> editable(Class<S> clazz,
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
	public <S extends Immutable> Prop<S> editable(Class<S> clazz,
			String name, S value);
	
	/**
	 * Create a new {@link ListPropDefault}, using
	 * an empty new {@link CListDefault},
	 * and add to this {@link BeanFeatures}
	 * @param name 
	 * 		The string value of the property name
	 * @param contentsClass
	 * 		The class of data in the list/indexed property
	 * @param <S> 
	 * 		The type of data in the list
	 * @return
	 * 		The new {@link ListPropDefault}
	 */
	public <S> ListProp<S> createList(Class<S> contentsClass, String name);

	/**
	 * Create a new {@link ListPropDefault},
	 * and add to this {@link BeanFeatures}
	 * @param name 
	 * 		The string value of the property name
	 * @param contentsClass
	 * 		The class of data in the list/indexed property
	 * @param <S>
	 * 		The type of data in the list/indexed property
	 * @param data
	 * 		The data to contain
	 * @return
	 * 		The new {@link ListPropDefault}
	 */
	public <S> ListPropDefault<S> createList(Class<S> contentsClass, String name, CList<S> data);
	
	/**
	 * Create a new read-only {@link ListPropDefault}
	 * based on a {@link CListCalculated}
	 * and add to this {@link BeanFeatures}
	 * @param contentsClass		The class of data in the list/indexed property
	 * @param name 				The string value of the property name
	 * @param calculation		The {@link Calculation} giving list contents
	 * @param <S>				The type of data in the list/indexed property
	 * @return					The new {@link ListPropDefault}
	 */
	public <S> ListPropDefault<S> calculatedList(Class<S> contentsClass, String name, Calculation<List<S>> calculation);
	
	/**
	 * Create a new {@link ListPropDefault}, using
	 * an empty new {@link CListDefault},
	 * and add to this {@link BeanFeatures}
	 * @param name 
	 * 		The string value of the property name
	 * @param contentsClass
	 * 		The class of data in the list/indexed property
	 * @param <S> 
	 * 		The type of data in the list
	 * @return
	 * 		The new {@link ListPropDefault}
	 */
	public <S> ListProp<S> editableList(Class<S> contentsClass, String name);

	/**
	 * Create a new {@link ListPropDefault},
	 * and add to this {@link BeanFeatures}
	 * @param name 
	 * 		The string value of the property name
	 * @param contentsClass
	 * 		The class of data in the list/indexed property
	 * @param <S>
	 * 		The type of data in the list/indexed property
	 * @param data
	 * 		The data to contain
	 * @return
	 * 		The new {@link ListPropDefault}
	 */
	public <S> ListPropDefault<S> editableList(Class<S> contentsClass, String name, CList<S> data);
	
	/**
	 * Make a new {@link MapProp} with given data and add to this bean
	 * @param <J> 
	 * 		The type of key for the map/property
	 * @param <S>
	 * 		The type of the map contents. 
	 * @param keyClass
	 * 		The type of the map keys.
	 * @param valueClass
	 * 		The type of the map values.
	 * @param name
	 * 		The name of the prop
	 * @param data 
	 * 		Initial data for the map/property
	 * @return
	 * 		The new prop
	 */
	public <J, S> MapProp<J, S> editableMap(Class<J> keyClass, Class<S> valueClass, String name, CMap<J, S> data);

	/**
	 * Make a new {@link MapProp} with no mappings and add to this bean
	 * @param <J> 
	 * 		The type of key for the map/property
	 * @param <S>
	 * 		The type of the map contents. 
	 * @param keyClass
	 * 		The type of the map keys.
	 * @param valueClass
	 * 		The type of the map values.
	 * @param name
	 * 		The name of the prop
	 * @return
	 * 		The new prop
	 */
	public <J, S> MapProp<J, S> editableMap(Class<J> keyClass, Class<S> valueClass, String name);

//	/**
//	 * Make a new {@link MapProp} with no mappings and add to this bean
//	 * @param <J> 
//	 * 		The type of key for the map/property
//	 * @param <S>
//	 * 		The type of the map contents. 
//	 * @param keyClass
//	 * 		The type of the map keys.
//	 * @param valueClass
//	 * 		The type of the map values.
//	 * @param name
//	 * 		The name of the prop
//	 * @param trackKeys
//	 * 		True to track keys as well as values, if they are {@link Changeable} - 
//	 * see comments in {@link CMap} and {@link CMapDefault}
//	 * @return
//	 * 		The new prop
//	 */
//	public <J, S> MapProp<J, S> editableMap(Class<J> keyClass, Class<S> valueClass, String name, boolean trackKeys);
	
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
	public <S> SetProp<S> editableSet(Class<S> clazz, String name,
			CSet<S> data);

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
	public <S> SetProp<S> editableSet(Class<S> clazz, String name);

	/**
	 * Make a new read-only {@link SetProp} and add to this bean
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
	public <S> SetProp<S> createSet(Class<S> clazz, String name,
			CSet<S> data);

	/**
	 * Make a new read-only {@link SetProp} from empty default set, and add to this bean
	 * @param <S>
	 * 		The type of the set contents. 
	 * @param clazz 
	 * 		The type of the set contents.
	 * @param name
	 * 		The name of the prop
	 * @return
	 * 		The new prop
	 */
	public <S> SetProp<S> createSet(Class<S> clazz, String name);


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
	public <T extends Immutable> Prop<T> create(Class<T> clazz, String name, T value);

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
	 * Make a new read-only {@link Prop} with a {@link Changeable} value
	 * and add to this bean
	 * @param <S>
	 * 		The type of the {@link Changeable}. 
	 * @param clazz 
	 * 		The type of the {@link Changeable}.
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The initial value of the prop
	 * @return
	 * 		The new prop
	 */
	public <S extends Changeable> Prop<S> create(Class<S> clazz, String name, S value);

	/**
	 * Create a new {@link MapPropDefault}, which is
	 * read only (cannot set a new {@link CMap} value),
	 * and add to thes bean.
	 * @param <J> 
	 * 		The type of key in the map/indexed property
	 * @param <S> 
	 * 		The type of data in the map/indexed property
	 * @param name 
	 * 		The string value of the property name
	 * @param keyClass
	 * 		The class of key in the map
	 * @param valueClass
	 * 		The class of value in the map
	 * @param data
	 * 		The data to contain
	 * @return
	 * 		The new {@link MapPropDefault}
	 */
	public <J, S> MapProp<J, S> createMap(Class<J> keyClass, Class<S> valueClass, String name, CMap<J, S> data);
	
	/**
	 * Create a new {@link MapPropDefault}, with new
	 * empty {@link CMap} contents, which is
	 * read only (cannot set a new {@link CMap} value),
	 * and add to this bean.
	 * @param <J> 
	 * 		The type of key in the map/indexed property
	 * @param <S> 
	 * 		The type of data in the map/indexed property
	 * @param name 
	 * 		The string value of the property name
	 * @param keyClass
	 * 		The class of key in the map
	 * @param valueClass
	 * 		The class of value in the map
	 * @return
	 * 		The new {@link MapPropDefault}
	 */
	public <J, S> MapProp<J, S> createMap(Class<J> keyClass, Class<S> valueClass, String name);
	
	/**
	 * Create a {@link Prop} of {@link Integer} constrained
	 * to a given range, inclusive
	 * @param name		The {@link Prop}'s name
	 * @param value		The initial value
	 * @param low		The lowest accepted value
	 * @param high		The highest accepted value
	 * @return			A new constrained {@link Prop}
	 */
	public Prop<Integer> ranged(String name, int value, int low, int high);
	
	/**
	 * Create a {@link Prop} of {@link Double} constrained
	 * to a given range, inclusive
	 * @param name		The {@link Prop}'s name
	 * @param value		The initial value
	 * @param low		The lowest accepted value
	 * @param high		The highest accepted value
	 * @return			A new constrained {@link Prop}
	 */
	public Prop<Double> ranged(String name, double value, double low, double high);

	//#########################################################################
	//###																	###
	//###  Auto-generated factory methods for Props with immutable content	###
	//###																	###
	//#########################################################################
	
	/**
	 * Make a new read-only String {@link Prop} and add to
	 * this {@link Bean}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop} itself (already added to the {@link BeanFeatures})
	 */
	public Prop<String> create(String name, String value);


	/**
	 * Make a new String {@link Prop} and add to this {@link Bean}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @param processor	The processor used when new values are set
	 * @return			The new {@link Prop} itself (already added to the {@link BeanFeatures})
	 */
	public Prop<String> create(String name, String value, ValueProcessor<String> processor);


	/**
	 * Make a new editable String {@link Prop}, accepting all values,
	 * and add to this {@link Bean}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop} itself (already added to the {@link BeanFeatures})
	 */
	public Prop<String> editable(String name, String value);


	/**
	 * Make a new read-only Boolean {@link Prop} and add to
	 * this {@link Bean}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop} itself (already added to the {@link BeanFeatures})
	 */
	public Prop<Boolean> create(String name, Boolean value);


	/**
	 * Make a new Boolean {@link Prop} and add to this {@link Bean}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @param processor	The processor used when new values are set
	 * @return			The new {@link Prop} itself (already added to the {@link BeanFeatures})
	 */
	public Prop<Boolean> create(String name, Boolean value, ValueProcessor<Boolean> processor);


	/**
	 * Make a new editable Boolean {@link Prop}, accepting all values,
	 * and add to this {@link Bean}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop} itself (already added to the {@link BeanFeatures})
	 */
	public Prop<Boolean> editable(String name, Boolean value);


	/**
	 * Make a new read-only Byte {@link Prop} and add to
	 * this {@link Bean}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop} itself (already added to the {@link BeanFeatures})
	 */
	public Prop<Byte> create(String name, Byte value);


	/**
	 * Make a new Byte {@link Prop} and add to this {@link Bean}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @param processor	The processor used when new values are set
	 * @return			The new {@link Prop} itself (already added to the {@link BeanFeatures})
	 */
	public Prop<Byte> create(String name, Byte value, ValueProcessor<Byte> processor);


	/**
	 * Make a new editable Byte {@link Prop}, accepting all values,
	 * and add to this {@link Bean}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop} itself (already added to the {@link BeanFeatures})
	 */
	public Prop<Byte> editable(String name, Byte value);


	/**
	 * Make a new read-only Short {@link Prop} and add to
	 * this {@link Bean}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop} itself (already added to the {@link BeanFeatures})
	 */
	public Prop<Short> create(String name, Short value);


	/**
	 * Make a new Short {@link Prop} and add to this {@link Bean}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @param processor	The processor used when new values are set
	 * @return			The new {@link Prop} itself (already added to the {@link BeanFeatures})
	 */
	public Prop<Short> create(String name, Short value, ValueProcessor<Short> processor);


	/**
	 * Make a new editable Short {@link Prop}, accepting all values,
	 * and add to this {@link Bean}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop} itself (already added to the {@link BeanFeatures})
	 */
	public Prop<Short> editable(String name, Short value);


	/**
	 * Make a new read-only Integer {@link Prop} and add to
	 * this {@link Bean}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop} itself (already added to the {@link BeanFeatures})
	 */
	public Prop<Integer> create(String name, Integer value);


	/**
	 * Make a new Integer {@link Prop} and add to this {@link Bean}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @param processor	The processor used when new values are set
	 * @return			The new {@link Prop} itself (already added to the {@link BeanFeatures})
	 */
	public Prop<Integer> create(String name, Integer value, ValueProcessor<Integer> processor);


	/**
	 * Make a new editable Integer {@link Prop}, accepting all values,
	 * and add to this {@link Bean}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop} itself (already added to the {@link BeanFeatures})
	 */
	public Prop<Integer> editable(String name, Integer value);


	/**
	 * Make a new read-only Long {@link Prop} and add to
	 * this {@link Bean}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop} itself (already added to the {@link BeanFeatures})
	 */
	public Prop<Long> create(String name, Long value);


	/**
	 * Make a new Long {@link Prop} and add to this {@link Bean}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @param processor	The processor used when new values are set
	 * @return			The new {@link Prop} itself (already added to the {@link BeanFeatures})
	 */
	public Prop<Long> create(String name, Long value, ValueProcessor<Long> processor);


	/**
	 * Make a new editable Long {@link Prop}, accepting all values,
	 * and add to this {@link Bean}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop} itself (already added to the {@link BeanFeatures})
	 */
	public Prop<Long> editable(String name, Long value);


	/**
	 * Make a new read-only Float {@link Prop} and add to
	 * this {@link Bean}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop} itself (already added to the {@link BeanFeatures})
	 */
	public Prop<Float> create(String name, Float value);


	/**
	 * Make a new Float {@link Prop} and add to this {@link Bean}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @param processor	The processor used when new values are set
	 * @return			The new {@link Prop} itself (already added to the {@link BeanFeatures})
	 */
	public Prop<Float> create(String name, Float value, ValueProcessor<Float> processor);


	/**
	 * Make a new editable Float {@link Prop}, accepting all values,
	 * and add to this {@link Bean}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop} itself (already added to the {@link BeanFeatures})
	 */
	public Prop<Float> editable(String name, Float value);


	/**
	 * Make a new read-only Double {@link Prop} and add to
	 * this {@link Bean}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop} itself (already added to the {@link BeanFeatures})
	 */
	public Prop<Double> create(String name, Double value);


	/**
	 * Make a new Double {@link Prop} and add to this {@link Bean}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @param processor	The processor used when new values are set
	 * @return			The new {@link Prop} itself (already added to the {@link BeanFeatures})
	 */
	public Prop<Double> create(String name, Double value, ValueProcessor<Double> processor);


	/**
	 * Make a new editable Double {@link Prop}, accepting all values,
	 * and add to this {@link Bean}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop} itself (already added to the {@link BeanFeatures})
	 */
	public Prop<Double> editable(String name, Double value);


	/**
	 * Make a new read-only DateTime {@link Prop} and add to
	 * this {@link Bean}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop} itself (already added to the {@link BeanFeatures})
	 */
	public Prop<DateTime> create(String name, DateTime value);


	/**
	 * Make a new DateTime {@link Prop} and add to this {@link Bean}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @param processor	The processor used when new values are set
	 * @return			The new {@link Prop} itself (already added to the {@link BeanFeatures})
	 */
	public Prop<DateTime> create(String name, DateTime value, ValueProcessor<DateTime> processor);


	/**
	 * Make a new editable DateTime {@link Prop}, accepting all values,
	 * and add to this {@link Bean}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop} itself (already added to the {@link BeanFeatures})
	 */
	public Prop<DateTime> editable(String name, DateTime value);


	/**
	 * Make a new read-only Color {@link Prop} and add to
	 * this {@link Bean}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop} itself (already added to the {@link BeanFeatures})
	 */
	public Prop<Color> create(String name, Color value);


	/**
	 * Make a new Color {@link Prop} and add to this {@link Bean}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @param processor	The processor used when new values are set
	 * @return			The new {@link Prop} itself (already added to the {@link BeanFeatures})
	 */
	public Prop<Color> create(String name, Color value, ValueProcessor<Color> processor);


	/**
	 * Make a new editable Color {@link Prop}, accepting all values,
	 * and add to this {@link Bean}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop} itself (already added to the {@link BeanFeatures})
	 */
	public Prop<Color> editable(String name, Color value);


	/**
	 * Make a new read-only ImmutableIcon {@link Prop} and add to
	 * this {@link Bean}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop} itself (already added to the {@link BeanFeatures})
	 */
	public Prop<ImmutableIcon> create(String name, ImmutableIcon value);


	/**
	 * Make a new ImmutableIcon {@link Prop} and add to this {@link Bean}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @param processor	The processor used when new values are set
	 * @return			The new {@link Prop} itself (already added to the {@link BeanFeatures})
	 */
	public Prop<ImmutableIcon> create(String name, ImmutableIcon value, ValueProcessor<ImmutableIcon> processor);


	/**
	 * Make a new editable ImmutableIcon {@link Prop}, accepting all values,
	 * and add to this {@link Bean}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop} itself (already added to the {@link BeanFeatures})
	 */
	public Prop<ImmutableIcon> editable(String name, ImmutableIcon value);

	
}
