package org.jpropeller.properties.calculated;

import java.util.Set;

import org.jpropeller.properties.GeneralProp;

/**
 * A calculation based on a set of Props
 *
 * @param <T>
 * 		The type of value calculated
 */
public interface PropCalculation<T> {

	/**
	 * Get the set of properties used in the calculation
	 * @return
	 * 		The set of properties used
	 */
	public Set<GeneralProp<?>> getSourceProps();
	
	/**
	 * Perform the calculation
	 * @return
	 * 		The calculation result
	 */
	public T calculate();
	
}
