package org.jpropeller.properties.calculated.impl;

import java.util.Collections;
import java.util.Set;

import org.jpropeller.calculation.Calculation;
import org.jpropeller.collection.CList;
import org.jpropeller.collection.CMap;
import org.jpropeller.collection.impl.IdentityHashSet;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.reference.Reference;
import org.jpropeller.reference.impl.ReferenceDefault;

/**
 * A calculation that uses a {@link Boolean} value from one {@link Prop}
 * to choose between the values of a pair of {@link Prop}s. 
 * 
 * If the {@link Boolean} is null, null value is calculated.
 *
 * @param <V>	The type of value chosen
 */
public class BooleanChoiceCalculation<V> implements Calculation<V> {

	private final Set<? extends Changeable> sources;
	private final Prop<Boolean> choice;
	private final Prop<? extends V> first;
	private final Prop<? extends V> second;

	/**
	 * Create a {@link CalculatedProp} based on a
	 * {@link BooleanChoiceCalculation} 
	 * @param <V>	The type of chosen value
	 * @param clazz	The {@link Class} of chosen value
	 * @param name	The (string) name of the property
	 * @param choice	The prop giving boolean choice
	 * @param first		The prop giving value of first choice
	 * @param second	The prop giving value of second choice
	 * @return		The {@link CalculatedProp}
	 */
	public final static <V> Prop<V> create(
			Class<V> clazz,
			String name,
			Prop<Boolean> choice,
			Prop<? extends V> first,
			Prop<? extends V> second) {
		return create(PropName.create(name, clazz), choice, first, second);
	}
	
	/**
	 * Create a {@link CalculatedProp} based on a
	 * {@link BooleanChoiceCalculation} , and giving
	 * a {@link CList} value
	 * @param <V>	The type of value in chosen {@link CList}
	 * @param clazz	The {@link Class} of value in chosen {@link CList}
	 * @param name	The (string) name of the property
	 * @param choice	The prop giving boolean choice
	 * @param first		The prop giving value of first choice
	 * @param second	The prop giving value of second choice
	 * @return		The {@link CalculatedProp}
	 */
	public final static <V> Prop<CList<V>> createList(
			Class<V> clazz,
			String name,
			Prop<Boolean> choice,
			Prop<? extends CList<V>> first,
			Prop<? extends CList<V>> second) {
		return create(PropName.createList(name, clazz), choice, first, second);
	}

	/**
	 * Create a {@link CalculatedProp} based on a
	 * {@link BooleanChoiceCalculation} , and giving
	 * a {@link CList} value
	 * @param <K>			The type of key in chosen {@link CMap}
	 * @param <V>			The type of value in chosen {@link CMap}
	 * @param keyClass		The {@link Class} of key in chosen {@link CMap}
	 * @param valueClass	The {@link Class} of value in chosen {@link CMap}
	 * @param name			The (string) name of the property
	 * @param choice		The prop giving boolean choice
	 * @param first			The prop giving value of first choice
	 * @param second		The prop giving value of second choice
	 * @return				The {@link CalculatedProp}
	 */
	public final static <K, V> Prop<CMap<K, V>> createMap(
			Class<K> keyClass,
			Class<V> valueClass,
			String name,
			Prop<Boolean> choice,
			Prop<? extends CMap<K, V>> first,
			Prop<? extends CMap<K, V>> second) {
		return create(PropName.createMap(name, keyClass, valueClass), choice, first, second);
	}

	/**
	 * Create a {@link CalculatedProp} based on a
	 * {@link BooleanChoiceCalculation} 
	 * @param <V>	The type of chosen value
	 * @param name	The {@link PropName} of the property
	 * @param choice	The prop giving boolean choice
	 * @param first		The prop giving value of first choice
	 * @param second	The prop giving value of second choice
	 * @return		The {@link CalculatedProp}
	 */
	public final static <V> Prop<V> create(
			PropName<V> name,
			Prop<Boolean> choice,
			Prop<? extends V> first,
			Prop<? extends V> second) {
		BooleanChoiceCalculation<V> calc = new BooleanChoiceCalculation<V>(choice, first, second);
		return new CalculatedProp<V>(name, calc);
	}
	
	/**
	 * Create a {@link Reference} to a
	 * {@link CalculatedProp} based on a
	 * {@link BooleanChoiceCalculation} 
	 * @param <V>	The type of chosen value
	 * @param clazz	The {@link Class} of map value
	 * @param choice	The prop giving boolean choice
	 * @param first		The prop giving value of first choice
	 * @param second	The prop giving value of second choice
	 * @return		The {@link Reference}
	 */
	public final static <V> Reference<V> createRef(
			Class<V> clazz,
			Prop<Boolean> choice,
			Prop<? extends V> first,
			Prop<? extends V> second) {
		Prop<V> prop = create(clazz, "value", choice, first, second);
		return ReferenceDefault.create(prop);
	}
	

	/**
	 * Create a {@link ReferenceDefault} to a {@link CalculatedProp} based on a
	 * {@link BooleanChoiceCalculation} , and giving
	 * a {@link CList} value
	 * @param <V>	The type of value in chosen {@link CList}
	 * @param clazz	The {@link Class} of value in chosen {@link CList}
	 * @param choice	The prop giving boolean choice
	 * @param first		The prop giving value of first choice
	 * @param second	The prop giving value of second choice
	 * @return		The {@link Reference}
	 */
	public final static <V> Reference<CList<V>> createListRef(
			Class<V> clazz,
			Prop<Boolean> choice,
			Prop<? extends CList<V>> first,
			Prop<? extends CList<V>> second) {
		return ReferenceDefault.create(create(PropName.createList("value", clazz), choice, first, second));
	}

	/**
	 * Create a {@link ReferenceDefault} to a {@link CalculatedProp} based on a
	 * {@link BooleanChoiceCalculation} , and giving
	 * a {@link CList} value
	 * @param <K>			The type of key in chosen {@link CMap}
	 * @param <V>			The type of value in chosen {@link CMap}
	 * @param keyClass		The {@link Class} of key in chosen {@link CMap}
	 * @param valueClass	The {@link Class} of value in chosen {@link CMap}
	 * @param choice		The prop giving boolean choice
	 * @param first			The prop giving value of first choice
	 * @param second		The prop giving value of second choice
	 * @return				The {@link Reference}
	 */
	public final static <K, V> Reference<CMap<K, V>> createMapRef(
			Class<K> keyClass,
			Class<V> valueClass,
			Prop<Boolean> choice,
			Prop<? extends CMap<K, V>> first,
			Prop<? extends CMap<K, V>> second) {
		return ReferenceDefault.create(create(PropName.createMap("value", keyClass, valueClass), choice, first, second));
	}
	
	
	/**
	 * Create a map lookup calculation
	 * @param choice	The prop giving boolean choice
	 * @param first		The prop giving value of first choice
	 * @param second	The prop giving value of second choice
	 */
	public BooleanChoiceCalculation(
			Prop<Boolean> choice,
			Prop<? extends V> first,
			Prop<? extends V> second) {
		
		this.choice = choice;
		this.first = first;
		this.second = second;
		
		IdentityHashSet<Changeable> sourcesM = new IdentityHashSet<Changeable>();
		sourcesM.add(choice);
		sourcesM.add(first);
		sourcesM.add(second);
		sources = Collections.unmodifiableSet(sourcesM);
	}
	
	@Override
	public V calculate() {
		Boolean c = choice.get();
		
		if (c == null) {
			return null;
		} else if (c) {
			return first.get();
		} else {
			return second.get();
		}
	}

	@Override
	public Set<? extends Changeable> getSources() {
		return sources;
	}
}
