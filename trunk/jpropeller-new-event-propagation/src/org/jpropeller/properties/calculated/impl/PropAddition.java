package org.jpropeller.properties.calculated.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.calculated.PropCalculation;

/**
 * An example PropCalculation, which adds a set of
 * Prop<? extends Number> to give a Double value result
 * 
 * @author shingoki
 */
public class PropAddition implements PropCalculation<Double> {

	Set<Prop<? extends Number>> sourceProps;
	Set<GeneralProp<?>> umSourceProps;
	
	/**
	 * Create an addition adding the doubleValue() of each Prop value
	 * in the set
	 * @param numbers
	 * 		The set of number properties to add
	 */
	public PropAddition(Prop<? extends Number>... numbers) {
		
		//Make a set of props as Prop<? extends Number>
		sourceProps = new HashSet<Prop<? extends Number>>(numbers.length);
		for (Prop<? extends Number> n : numbers) {
			sourceProps.add(n);
		}
		
		//Copy the set as a Set<GeneralProp<?>> and then make unmodifiable,
		//so we can hand out this set safely
		HashSet<GeneralProp<?>> setAsGeneral = new HashSet<GeneralProp<?>>(sourceProps.size());
		setAsGeneral.addAll(sourceProps);
		umSourceProps = Collections.unmodifiableSet(new HashSet<GeneralProp<?>>(setAsGeneral));
	}
	
	@Override
	public Double calculate() {
		double sum = 0;
		for (Prop<? extends Number> prop : sourceProps) {
			sum += prop.get().doubleValue();
		}
		return sum;
	}

	@Override
	public Set<GeneralProp<?>> getSourceProps() {
		return umSourceProps;
	}

}
