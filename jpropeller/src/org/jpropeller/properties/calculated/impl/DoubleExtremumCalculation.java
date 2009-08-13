package org.jpropeller.properties.calculated.impl;

import java.util.List;

import org.jpropeller.properties.Prop;

/**
 * Calculates either the maximum or the minimum value in a list of
 * Double {@link Prop}s.
 */
public class DoubleExtremumCalculation implements ListCalculation<Prop<Double>, Double> {
	
	private final boolean maximum;

	private final static DoubleExtremumCalculation MINIMUM = new DoubleExtremumCalculation(false);
	private final static DoubleExtremumCalculation MAXIMUM = new DoubleExtremumCalculation(true);
	
	/**
	 * Get a {@link DoubleExtremumCalculation}
	 * @param maximum		True if calculation returns maximum, false for minimum.
	 * @return				A {@link DoubleExtremumCalculation}
	 */
	public final static DoubleExtremumCalculation get(final boolean maximum) {
		return maximum ? MAXIMUM : MINIMUM;
	}

	/**
	 * Get a {@link DoubleExtremumCalculation} finding maximum
	 * @return				A {@link DoubleExtremumCalculation}
	 */
	public final static DoubleExtremumCalculation max() {
		return get(true);
	}

	/**
	 * Get a {@link DoubleExtremumCalculation} finding minimum
	 * @return				A {@link DoubleExtremumCalculation}
	 */
	public final static DoubleExtremumCalculation min() {
		return get(false);
	}

	/**
	 * Create an {@link DoubleExtremumCalculation}
	 * @param maximum		True if calculation returns maximum, false for minimum.
	 */
	private DoubleExtremumCalculation(boolean maximum) {
		super();
		this.maximum = maximum;
	}

	@Override
	public Double calculate(List<? extends Prop<Double>> inputs) {
		double extremum = maximum ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
		for (Prop<Double> prop : inputs) {
			double val = prop.get();
			if (maximum && val > extremum) {
				extremum = val;
			} else if (!maximum && val < extremum) {
				extremum = val;
			}
		}
		return extremum;
	}
}
