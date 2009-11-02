package org.jpropeller.transformer;

/**
 * A {@link SimpleTransformer} that is also a {@link BiTransformer}
 *
 * @param <T>		The source and target type
 */
public interface SimpleBiTransformer<T> extends SimpleTransformer<T>, BiTransformer<T, T> {
}
