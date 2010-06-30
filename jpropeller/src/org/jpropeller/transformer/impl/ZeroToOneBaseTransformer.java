package org.jpropeller.transformer.impl;

import org.jpropeller.transformer.BiTransformer;

/**
 * A {@link BiTransformer} from {@link Integer} to {@link Integer}. 
 * {@link #transform(Integer)} adds one to the input, and {@link #transformBack(Integer)}
 * subtracts one from the input.
 * 
 * Hence this transforms from zero-based to one-based indices, and back again
 * from one-based to zero-based. 
 */
public class ZeroToOneBaseTransformer implements BiTransformer<Integer, Integer> {

	@Override
	public Integer transformBack(Integer t) {
		return t - 1;
	}

	@Override
	public Integer transform(Integer s) {
		return s + 1;
	}
}
