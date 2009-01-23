package org.jpropeller.properties.calculated.impl;

import java.util.List;

import org.jpropeller.properties.Prop;

/**
 * A calculation on a list of props
 * @author shingoki
 *
 * @param <I>
 * 		The type of value in input props
 * @param <T>
 * 		The type of result
 */
public interface PropListCalculation<I, T> {

	/**
	 * Return the result of calculation on specified input list
	 * @param inputs
	 * 		The inputs
	 * @return
	 * 		The result
	 */
	public T calculate(List<Prop<? extends I>> inputs);
	
}
