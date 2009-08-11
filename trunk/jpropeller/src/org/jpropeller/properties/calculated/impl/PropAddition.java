package org.jpropeller.properties.calculated.impl;

import java.util.Collections;
import java.util.Set;

import org.jpropeller.calculation.Calculation;
import org.jpropeller.collection.impl.IdentityHashSet;
import org.jpropeller.properties.Prop;

/**
 * An example PropCalculation, which adds a set of
 * Prop<? extends Number> to give a Double value result
 */
public class PropAddition implements Calculation<Double> {

	Set<Prop<? extends Number>> sourceProps;
	Set<Prop<?>> umSourceProps;
	
	/**
	 * Create an addition adding the doubleValue() of each Prop value
	 * in the set
	 * @param numbers
	 * 		The set of number properties to add
	 */
	public PropAddition(Prop<? extends Number>... numbers) {
		
		//Make a set of props as Prop<? extends Number>
		sourceProps = new IdentityHashSet<Prop<? extends Number>>(numbers.length);
		for (Prop<? extends Number> n : numbers) {
			sourceProps.add(n);
		}
		
		//Copy the set as a Set<GeneralProp<?>> and then make unmodifiable,
		//so we can hand out this set safely
		IdentityHashSet<Prop<?>> setAsGeneral = new IdentityHashSet<Prop<?>>(sourceProps.size());
		setAsGeneral.addAll(sourceProps);
		umSourceProps = Collections.unmodifiableSet(setAsGeneral);
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
	public Set<Prop<?>> getSources() {
		return umSourceProps;
	}

}
