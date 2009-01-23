package org.jpropeller.properties.calculated;

import java.util.Set;

import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.Prop;

/**
 * A mapping from {@link Prop} instances to their values
 * @author shingoki
 * @param <T> 
 * 		The type of prop values in the map
 */
public interface PropValueMap<T> {

	/**
	 * Get the set of props used as keys in this map
	 * @return
	 * 		Prop keys
	 */
	public Set<GeneralProp<? extends T>> getPropKeys();
	
	/**
	 * Get the value for a given prop
	 * @param <S>
	 * 		The type of value
	 * @param prop
	 * 		The {@link Prop}
	 * @return
	 * 		The value of the prop
	 * @throws PropValueNotMappedException
	 * 		If there is no mapped value for the prop
	 */
	public <S extends T> S getValue(GeneralProp<S> prop) throws PropValueNotMappedException;
	
}
