package org.jpropeller.properties.calculated.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.calculated.PropCalculation;

/**
 * A default general-purpose {@link PropCalculation}, 
 * which accepts a list of Prop<? extends Number> to give a Double value result
 * 
 * @author shingoki
 * 
 * @param <I>
 * 		The type of input value
 * @param <T>
 * 		The type of result of the calculation		 
 */
public class PropCalculationDefault<I, T> implements PropCalculation<T> {

	Set<Prop<? extends I>> sourceProps;
	Set<GeneralProp<?>> umSourceProps;
	List<Prop<? extends I>> inputs;
	PropListCalculation<I, T> listCalculation;
	
	/**
	 * Create a {@link PropCalculationDefault} using a specified input
	 * @param listCalculation
	 * 		The calculation 
	 * @param input
	 * 		The property to input to calculation
	 */
	public PropCalculationDefault(PropListCalculation<I, T> listCalculation, Prop<? extends I> input) {
		this(listCalculation, makeList(input));
	}
	
	/**
	 * Create a {@link PropCalculationDefault} using a specified inputs
	 * @param listCalculation
	 * 		The calculation 
	 * @param inputs
	 * 		The properties to input to calculation
	 */
	public PropCalculationDefault(PropListCalculation<I, T> listCalculation, Prop<? extends I>... inputs) {
		this(listCalculation, makeList(inputs));
	}
	
	/**
	 * Create a {@link PropCalculationDefault} using a specified list of inputs, in
	 * iterated order
	 * @param listCalculation
	 * 		The calculation 
	 * @param inputs
	 * 		The {@link Collection} of properties to input to calculation
	 */
	public PropCalculationDefault(PropListCalculation<I, T> listCalculation, Collection<Prop<? extends I>> inputs) {
		
		this.listCalculation = listCalculation;
		
		//Make a copy of the inputs as an ArrayList for PropListCalculation
		this.inputs = new ArrayList<Prop<? extends I>>(inputs);
		
		//Make a set of props as Prop<? extends Number>
		sourceProps = new HashSet<Prop<? extends I>>(inputs);
		
		//Copy the set as a Set<GeneralProp<?>> and then make unmodifiable,
		//so we can hand out this set safely
		HashSet<GeneralProp<?>> setAsGeneral = new HashSet<GeneralProp<?>>(sourceProps.size());
		setAsGeneral.addAll(sourceProps);
		umSourceProps = Collections.unmodifiableSet(new HashSet<GeneralProp<?>>(setAsGeneral));
	}
	
	@Override
	public T calculate() {
		return listCalculation.calculate(inputs);
	}

	@Override
	public Set<GeneralProp<?>> getSourceProps() {
		return umSourceProps;
	}

	/**
	 * Make a list with a single entry
	 * @param <J>
	 * 		The type of entry
	 * @param input
	 * 		The entry
	 * @return
	 * 		A list with the single entry
	 */
	private static <J> List<Prop<? extends J>> makeList(Prop<? extends J> input) {
		List<Prop<? extends J>> list = new LinkedList<Prop<? extends J>>();
		list.add(input);
		return list;
	}
	
	/**
	 * Make a list with entries from varargs
	 * @param <J>
	 * 		The type of entry
	 * @param input
	 * 		The entry
	 * @return
	 * 		A list with the entries
	 */
	private static <J> List<Prop<? extends J>> makeList(Prop<? extends J>... inputs) {
		List<Prop<? extends J>> list = new ArrayList<Prop<? extends J>>(inputs.length);
		for (Prop<? extends J> input : inputs){
			list.add(input);
		}
		return list;
	}
}
