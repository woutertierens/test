package org.jpropeller.transformer;

import org.jpropeller.properties.change.Immutable;

/**
 * A {@link Transformer} that is also {@link Immutable}. This
 * implies that it has no mutable state - it will always produce the
 * same output for the same input.
 *
 * @param <S>	The first type - transformation is from this type
 * @param <T>	The second type - transformation is to this type
 */
public interface ImmutableTransformer<S, T> extends Transformer<S, T>, Immutable {
}
