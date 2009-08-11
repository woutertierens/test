package org.jpropeller.transformer.impl;

import org.jpropeller.transformer.Transformer;

/**
 * A {@link Transformer} that combines two other {@link Transformer}s, applying
 * both one then the other
 *
 * @param <R>
 * 		The type transformed from
 * @param <S>
 * 		The intermediate type - output from first transformer and
 * input to seconds transformer
 * @param <T>
 * 		The type transformed to
 */
public class CompositeTransformer<R, S, T> implements Transformer<R, T> {

	Transformer<R, S> first;
	Transformer<S, T> second;

	/**
	 * Make a {@link CompositeTransformer}
	 * @param first
	 * 		The first transform - applied first, this takes values of
	 * type R to type S
	 * @param second
	 * 		The second transform - applied second, this takes values of
	 * type S to type T
	 */
	private CompositeTransformer(Transformer<R, S> first,
			Transformer<S, T> second) {
		super();
		this.first = first;
		this.second = second;
	}

	/**
	 * Make a {@link CompositeTransformer}
	 * @param <R>
	 * 		The type transformed from
	 * @param <S>
	 * 		The intermediate type - output from first transformer and
	 * input to seconds transformer
	 * @param <T>
	 * 		The type transformed to
	 * @param first
	 * 		The first transform - applied first, this takes values of
	 * type R to type S
	 * @param second
	 * 		The second transform - applied second, this takes values of
	 * type S to type T
	 * @return
	 * 		A new {@link CompositeTransformer}
	 */
	public static <R, S, T> CompositeTransformer<R, S, T> create(Transformer<R, S> first,
			Transformer<S, T> second) {
		return new CompositeTransformer<R, S, T>(first, second);
	}
	
	@Override
	public T transform(R r) {
		return second.transform(first.transform(r));
	}

}
