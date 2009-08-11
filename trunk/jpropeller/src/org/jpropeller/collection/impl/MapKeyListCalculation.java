package org.jpropeller.collection.impl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jpropeller.calculation.Calculation;
import org.jpropeller.collection.CMap;
import org.jpropeller.properties.Prop;

/**
 * Calculation that makes a list from the keys of {@link CMap}
 * @param <K>		The type of key in the map
 * @param <V>		The type of value in the map 
 */
public class MapKeyListCalculation<K, V> implements Calculation<List<K>> {

	private Set<Prop<?>> sourceProps;
	Prop<CMap<K, V>> map;
	
	/**
	 * Make a calculation
	 * @param map		The map whose keys we will list
	 */
	public MapKeyListCalculation(Prop<CMap<K, V>> map) {
		this.map = map;
		IdentityHashSet<Prop<?>> props = new IdentityHashSet<Prop<?>>();
		props.add(map);
		sourceProps = Collections.unmodifiableSet(props);
	}
	
	@Override
	public List<K> calculate() {
		
		//Make a list of keys
		List<K> keyList = new LinkedList<K>();
		for (K t : map.get().keySet()) {
			keyList.add(t);
		}
		
		return keyList;
	}

	@Override		
	public Set<Prop<?>> getSources() {
		return sourceProps;
	}
}
