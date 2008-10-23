package org.jpropeller.properties.calculated.impl;

import org.jpropeller.properties.Prop;

/**
 * Subtract from the first prop, all other props. 
 */
//Varargs array is not used for any time
@SuppressWarnings("unchecked")
public class PropSubtraction extends PropCalculationAbstract<Prop<Number>, Number, Double> {
	
	@Override
	public Double calculate() {
		boolean first = true;
		double sum = 0;
		for(Prop<Number> prop : sourceProps()) {
			if (first) {
				sum = prop.get().doubleValue();
				first = false;
			} else {
				sum -= prop.get().doubleValue();
			}
		}
		return sum;
	}

}
