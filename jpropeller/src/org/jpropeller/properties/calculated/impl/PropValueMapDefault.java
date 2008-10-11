package org.jpropeller.properties.calculated.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.calculated.PropValueMap;
import org.jpropeller.properties.calculated.PropValueNotMappedException;

/**
 * Default implementation of {@link PropValueMap}
 * @author shingoki
 *
 * @param <T>
 * 		The type of prop values in the map
 */
public class PropValueMapDefault<T> implements PropValueMap<T> {

	Map<GeneralProp<? extends T>, Object> map = new HashMap<GeneralProp<? extends T>, Object>();
	
	/**
	 * Put a mapping
	 * @param <S>
	 * 		Type of value
	 * @param prop
	 * 		The prop
	 * @param value
	 * 		The value
	 */
	<S extends T> void putValue(Prop<S> prop, S value) {
		map.put(prop, value);
	}
	
	@Override
	public Set<GeneralProp<? extends T>> getPropKeys() {
		return map.keySet();
	}
	
	//We suppress this warning since we only 
	//ever put mappings from Prop<S> to S, so when 
	//we retrieve a mapping from Prop<S>, it must be S
	@SuppressWarnings("unchecked")	
	@Override
	public <S extends T> S getValue(GeneralProp<S> prop)
			throws PropValueNotMappedException {
		Object value = map.get(prop);
		if (value == null) {
			throw new PropValueNotMappedException(prop);
		} else {
			return (S)value;
		}
	}

}
