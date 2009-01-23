package org.jpropeller.properties.calculated;

import java.util.Set;

import org.jpropeller.properties.GeneralProp;

/**
 * A calculation based on a set of Props, using cached values of those props
 * @author shingoki
 *
 * @param <T>
 * 		The type of value calculated
 */
public interface CachedPropCalculation<T> {

	/**
	 * Get the set of properties used in the calculation
	 * This must be immutable. It may be read at any time, and determines
	 * the set of properties for which values will be available to the
	 * calculation when {@link CachedPropCalculation#calculate(PropValueMap)}
	 * is called
	 * @return
	 * 		The set of properties used
	 */
	public Set<GeneralProp<?>> getSourceProps();
	
	/**
	 * Perform the calculation, using ONLY values of the
	 * source properties, as held in the provided value map
	 * @param map
	 * 		Map from the properties returned by
	 * {@link CachedPropCalculation#getSourceProps()} to their
	 * values - these values must be used to perform the 
	 * calculation. 
	 * @return
	 * 		The calculation result
	 */
	public T calculate(PropValueMap<?> map);
	
}
