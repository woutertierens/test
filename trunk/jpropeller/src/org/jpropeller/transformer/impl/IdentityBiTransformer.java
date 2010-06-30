package org.jpropeller.transformer.impl;

import org.jpropeller.transformer.BiTransformer;

/**
 * {@link BiTransformer} that just transforms an instance of T
 * to itself, and back to itself again - in other words, does nothing.
 *
 * @param <T>	The transformed type
 */
public class IdentityBiTransformer<T> implements BiTransformer<T, T> {

	@Override
	public T transformBack(T t) {
		return t;
	}

	@Override
	public T transform(T s) {
		return s;
	}

}
