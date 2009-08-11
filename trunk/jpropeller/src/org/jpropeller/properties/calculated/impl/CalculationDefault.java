package org.jpropeller.properties.calculated.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jpropeller.calculation.Calculation;
import org.jpropeller.collection.impl.IdentityHashSet;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.util.GeneralUtils;

/**
 * A default general-purpose {@link Calculation}, 
 * which accepts a list of Prop<? extends Number> to give a Double value result
 * 
 * @param <I>
 * 		The type of input value
 * @param <T>
 * 		The type of result of the calculation		 
 */
public class CalculationDefault<I extends Changeable, T> implements Calculation<T> {

	Set<? extends I> sources;
	Set<I> umSources;
	List<I> inputs;
	ListCalculation<I, T> listCalculation;
	
	/**
	 * Create a {@link CalculationDefault}
	 * 
	 * @param listCalculation	The calculation 
	 * @param firstInput		The first {@link Changeable} to input to calculation
	 * @param additionalInputs	Any additional {@link Changeable}s to input to calculation
	 */
	public CalculationDefault(ListCalculation<I, T> listCalculation, I firstInput, I... additionalInputs) {
		this(listCalculation, GeneralUtils.makeList(firstInput, additionalInputs));
	}
	
	/**
	 * Create a {@link CalculationDefault} using a specified list of inputs, in
	 * iterated order
	 * @param listCalculation
	 * 		The calculation 
	 * @param inputs
	 * 		The {@link Collection} of properties to input to calculation
	 */
	public CalculationDefault(ListCalculation<I, T> listCalculation, Collection<? extends I> inputs) {
		
		this.listCalculation = listCalculation;
		
		//Make a copy of the inputs as an ArrayList for PropListCalculation
		this.inputs = new ArrayList<I>(inputs);
		
		//Make a set of sources 
		sources = new IdentityHashSet<I>(inputs);

		//Make an unmodifiable set of sources to hand out
		umSources = Collections.unmodifiableSet(sources);
	}
	
	@Override
	public T calculate() {
		return listCalculation.calculate(inputs);
	}

	@Override
	public Set<I> getSources() {
		return umSources;
	}
	
}
