package org.jpropeller.transformer.impl;

import org.jpropeller.transformer.BiTransformer;

/**
 * A {@link BiTransformer} from {@link Long} to {@link Long}. 
 * {@link #transform(Long)} adds one to the input, and {@link #transformBack(Long)}
 * subtracts one from the input.
 * 
 * Hence this transforms from zero-based to one-based indices, and back again
 * from one-based to zero-based. 
 */
public class ZeroToOneBaseTransformerLong implements BiTransformer<Long, Long> {

	@Override
	public Long transformBack(Long t) {
		return t - 1;
	}

	@Override
	public Long transform(Long s) {
		return s + 1;
	}
}
