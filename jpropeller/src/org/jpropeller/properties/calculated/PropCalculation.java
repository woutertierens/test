package org.jpropeller.properties.calculated;

import java.util.Set;

import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.change.Changeable;

/**
 * A calculation based on a set of Props
 * 
 * NOTE: {@link PropCalculation}s MUST produce
 * values that are immutable (in the sense that
 * they never change state in any detectable way after
 * creation). This is because the results of calculations
 * are considered to be transient - they can always be 
 * recreated by recalculating based on the same inputs.
 * If a mutable result is produced, and it is mutated,
 * then any state in it will be lost whenever the
 * calculation is repeated. In addition, code using
 * the results of {@link PropCalculation} will not expect
 * changes to be made, and so may not listen to results
 * even if they care {@link Changeable}.
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
