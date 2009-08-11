package org.jpropeller.properties.values;

import java.util.Comparator;
import java.util.List;

import org.jpropeller.comparison.ComparatorFilter;
import org.jpropeller.comparison.ComparisonType;
import org.jpropeller.comparison.Filter;
import org.jpropeller.comparison.RangeFilter;
import org.jpropeller.info.PropEditability;
import org.jpropeller.properties.exception.InvalidValueException;
import org.jpropeller.properties.exception.ReadOnlyException;
import org.jpropeller.properties.values.impl.FilterProcessor;

/**
 * Factory methods for simple {@link ValueProcessor}s, etc.
 */
public class Values {

	private Values(){
	}
	
	/**
	 * Create a {@link RangeFilter} bounded by two {@link ComparatorFilter}s.
	 * 
	 * Note that for the filter ever to pass, the {@link ComparatorFilter}s 
	 * must accept each other's limits.
	 * For example, if the {@link ComparatorFilter}s are on double values, then
	 * using normal notation, we might have
	 * 
	 * first requires value < 3
	 * second requires value > 4
	 * 
	 * In this case, the range filter will never return true.
	 *  
	 * @param <T>		The type of value filtered 
	 * @param first		The first required comparison
	 * @param second	The second required comparison
	 * @return 			A {@link RangeFilter}
	 */
	public final static <T> ValueProcessor<T> range(ComparatorFilter<T> first, ComparatorFilter<T> second) {
		return FilterProcessor.create(RangeFilter.get(first, second));
	}
	
	/**
	 * Gets a {@link ComparatorFilter} using natural ordering
	 * 
	 * @param type			The type of comparison - lets us check the resulting 
	 * 						value of {@link Comparator#compare(Object, Object)}
	 * @param limit			The limit against which we are checking
	 * 						This must be immutable.
	 * @param <S>			The type of value compared
	 *  
	 * @return 				A {@link ComparatorFilter} as specified
	 */
	public static <S extends Comparable<? super S>> ValueProcessor<S> comparison(ComparisonType type, S limit) {
		return FilterProcessor.create(ComparatorFilter.get(type, limit));
	}
	
	/**
	 * Gets a {@link Filter} that accepts only one value
	 * 
	 * @param value			The accepted value.
	 * 						This must be immutable.
	 * @param <S>			The type of value compared
	 *  
	 * @return 				A {@link Filter} as specified
	 */
	public static <S extends Comparable<? super S>> ValueProcessor<S> equals(S value) {
		return FilterProcessor.create(ComparatorFilter.get(ComparisonType.EQUAL, value));
	}

	/**
	 * Gets a {@link Filter} that rejects only one value
	 * 
	 * @param value			The rejected value.
	 * 						This must be immutable.
	 * @param <S>			The type of value compared
	 *  
	 * @return 				A {@link Filter} as specified
	 */
	public static <S extends Comparable<? super S>> ValueProcessor<S> differs(S value) {
		return FilterProcessor.create(ComparatorFilter.get(ComparisonType.NOT_EQUAL, value));
	}

	/**
	 * Get a {@link ComparatorFilter}
	 * 
	 * @param comparator	The {@link Comparator} to actually compare the objects
	 * 						This must be immutable.
	 * @param type			The type of comparison - lets us check the resulting 
	 * 						value of {@link Comparator#compare(Object, Object)}
	 * @param limit			The limit against which we are checking
	 * 						This must be immutable.
	 * @param <S> 			The type of value compared
	 *  
	 * @return 				A {@link ComparatorFilter} as specified
	 */
	public static <S> ValueProcessor<S> comparison(Comparator<S> comparator, ComparisonType type, S limit) {
		return FilterProcessor.create(ComparatorFilter.get(comparator, type, limit));
	}

	/**
	 * Round a double-valued input to a specified number of decimal places.
	 * e.g. 0.546 -> 0.54 for 2 decimal places.
	 * 
	 * @param decimalPlaces Number of decimal places to round to
	 * @return A processor which rounds inputs to the required precision.
	 */
	public static ValueProcessor<Double> round(final int decimalPlaces) {
		return new ValueProcessor<Double>() {

			@Override
			public PropEditability getEditability() {
				return PropEditability.EDITABLE;
			}

			@Override
			public Double process(Double input) throws InvalidValueException, ReadOnlyException {
				//TODO would rounding based on formatting be more predictable? We will only
				//see the effect of rounding when foratted anyway, since double cannot store
				//arbitrary decimal places accurately - something.1 often ends up as something.10000000001 etc.
				return (double)Math.round(input * Math.pow(10, decimalPlaces)) / Math.pow(10, decimalPlaces);				
			}

		};
	}

	/**
	 * Combine two processors into a processor sequence. When the composite
	 * processor is applied to an input, each sub-processor is applied to the
	 * outputs of the previous sub-processor.
	 * Input -> processor 1 -> Output 1
	 * Output 1 -> processor 2 -> Output 2
	 * Output 2 -> processor 3 -> Composite Processor Output
	 * 
	 * @param <S> The type of the sub-processors
	 * @param processors An ordered list of sub-processors
	 * @return A processor which has the effect of sequentially applying several processors
	 */
	public static <S> ValueProcessor<S> composite(final List<ValueProcessor<S>> processors) {
		return new ValueProcessor<S>() {

			@Override
			public PropEditability getEditability() {
				for (ValueProcessor<S> p : processors) {
					if (p.getEditability() == PropEditability.READ_ONLY) {
						return PropEditability.READ_ONLY;
					}
				}
				return PropEditability.EDITABLE;
			}

			@Override
			public S process(S input) throws InvalidValueException,
			ReadOnlyException {
				for (ValueProcessor<S> p : processors) {
					input = p.process(input);
				}
				return input;
			}

		};
	}

}
