package org.jpropeller.transformer;

/**
 * A {@link Transformer} where the source and target
 * types are the same
 *
 * @param <T>	The source and target type
 */
public interface SimpleTransformer<T> extends Transformer<T, T> {
}
