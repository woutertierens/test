package org.jpropeller.properties.calculated.impl;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.jpropeller.calculation.Calculation;
import org.jpropeller.collection.CMap;
import org.jpropeller.collection.impl.IdentityHashSet;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.reference.Reference;
import org.jpropeller.reference.impl.ReferenceDefault;

/**
 * A calculation that uses a key value from one {@link Prop}
 * to lookup the entry in a {@link Map} value from another {@link Prop}. 
 * 
 * The lookup is performed even if the key value is null.
 * If the map value is null, then the result is null.
 *
 * @param <K>	The type of key value
 * @param <V>	The type of value in the map
 */
public class MapLookupCalculation<K, V> implements Calculation<V> {

	private final Set<? extends Changeable> sources;
	private final Prop< ? extends CMap<? super K, ? extends V>> map;
	private final Prop< ? extends K> key;

	/**
	 * Create a {@link CalculatedProp} based on a
	 * {@link MapLookupCalculation} 
	 * @param <K>	The type of map key
	 * @param <V>	The type of map value
	 * @param clazz	The {@link Class} of map value
	 * @param name	The (string) name of the property
	 * @param map	The map property from which we look up
	 * @param key	The map key we use to look up
	 * @return		The {@link CalculatedProp}
	 */
	public final static <K, V> Prop<V> create(
			Class<V> clazz,
			String name,
			Prop< ? extends CMap<? super K, ? extends V>> map,
			Prop< ? extends K> key) {
		MapLookupCalculation<K, V> calc = new MapLookupCalculation<K, V>(map, key);
		return new CalculatedProp<V>(PropName.create(clazz, name), calc);
	}
	
	/**
	 * Create a {@link Reference} to a
	 * {@link CalculatedProp} based on a
	 * {@link MapLookupCalculation} 
	 * @param <K>	The type of map key
	 * @param <V>	The type of map value
	 * @param clazz	The {@link Class} of map value
	 * @param map	The map property from which we look up
	 * @param key	The map key we use to look up
	 * @return		The {@link Reference}
	 */
	public final static <K, V> Reference<V> createRef(
			Class<V> clazz,
			Prop< ? extends CMap<? super K, ? extends V>> map,
			Prop< ? extends K> key) {
		Prop<V> prop = create(clazz, "value", map, key);
		return ReferenceDefault.create(prop);
	}
	
	/**
	 * Create a map lookup calculation
	 * @param map		The map
	 * @param key		The key
	 */
	public MapLookupCalculation(
			Prop< ? extends CMap<? super K, ? extends V>> map,
			Prop< ? extends K> key) {
		
		this.map = map;
		this.key = key;
		
		IdentityHashSet<Changeable> sourcesM = new IdentityHashSet<Changeable>();
		sourcesM.add(map);
		sourcesM.add(key);
		sources = Collections.unmodifiableSet(sourcesM);
	}
	
	@Override
	public V calculate() {
		CMap<? super K, ? extends V> m = map.get();
		if (m == null) return null;

		return m.get(key.get()); 
	}

	@Override
	public Set<? extends Changeable> getSources() {
		return sources;
	}
}
