package org.jpropeller.properties.calculated.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.calculated.PropCalculation;

/**
 * An abstract {@link PropCalculation}, which handles the source
 * props.
 * @param <P>
 * 		The type of source prop used for the calculation 
 * @param <T> 
 * 		The type of data in the props used for calculation
 * @param <R>
 * 		The type of result data produced by the calculation 
 * 		
 */
public abstract class PropCalculationAbstract<P extends GeneralProp<? extends T>, T, R> implements PropCalculation<R> {

	private List<P> sourceProps;
	private Set<GeneralProp<?>> umSourceProps;
	
	//This is to give a warning in subclasses that attempt to use the default constructor
	@SuppressWarnings("unused")
	private PropCalculationAbstract(){
	}
	
	/**
	 * Create a {@link PropCalculation}
	 * @param sources
	 * 		The set of number properties to add
	 */
	public PropCalculationAbstract(final P... sources) {
		
		//Make a set of props as Prop<? extends Number>
		sourceProps = Collections.unmodifiableList(Arrays.asList(sources));
		
		//Copy the set as a Set<GeneralProp<?>> and then make unmodifiable,
		//so we can hand out this set safely
		HashSet<GeneralProp<?>> setAsGeneral = new HashSet<GeneralProp<?>>(sourceProps.size());
		setAsGeneral.addAll(sourceProps);
		umSourceProps = Collections.unmodifiableSet(new HashSet<GeneralProp<?>>(setAsGeneral));
	}
	
	@Override
	public Set<GeneralProp<?>> getSourceProps() {
		return umSourceProps;
	}

	protected List<P> sourceProps() {
		return sourceProps;
	}
}
