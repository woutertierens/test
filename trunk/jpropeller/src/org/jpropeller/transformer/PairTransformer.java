package org.jpropeller.transformer;

/**
 * Allows for transformation from a pair of instances to a single instance
 *
 * @param <A> The first input type
 * @param <B> The second input type
 * @param <C> The output type
 */
public interface PairTransformer<A, B, C> {
	/**
	 * Transform a pair of values
	 * @param a 	First input to transformation
	 * @param b 	Second input to transformation
	 * @return		Output from transformation
	 */
	public C transform (A a, B b);
}
