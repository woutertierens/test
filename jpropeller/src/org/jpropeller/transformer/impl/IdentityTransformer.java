package org.jpropeller.transformer.impl;

import org.jpropeller.transformer.Transformer;

/**
 * {@link Transformer} that just transforms an instance of T
 * to itself - in other words, does nothing.
 *
 * @param <T>	The transformed type
 */
public class IdentityTransformer<T> implements Transformer<T, T> {

	@Override
	public T transform(T s) {
		return s;
	}

}
