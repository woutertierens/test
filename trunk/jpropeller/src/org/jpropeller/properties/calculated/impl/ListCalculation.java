package org.jpropeller.properties.calculated.impl;

import java.util.List;

import org.jpropeller.properties.change.Changeable;

/**
 * A calculation on a list of {@link Changeable}s
 *
 * @param <I>		The type of value in input 
 * 					{@link Changeable}s
 * @param <T>		The type of result
 */
public interface ListCalculation<I extends Changeable, T> {

	/**
	 * Return the result of calculation on specified input list
	 * 
	 * @param inputs	The inputs
	 * @return			The result
	 */
	public T calculate(List<? extends I> inputs);
	
}
