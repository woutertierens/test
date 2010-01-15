package org.jpropeller.transformer;

import org.jpropeller.bean.Bean;
import org.jpropeller.path.BeanPath;
import org.jpropeller.properties.Prop;

/**
 * The type of {@link Transformer} required for the "to" step in a {@link BeanPath}.
 * This is the last step in a {@link BeanPath}.
 * 
 * @param <S>		The type of {@link Bean} transformed from
 * @param <T>		The type of value in the property transformed to
 */
public interface PathStep<S, T> extends Transformer<S, Prop<T>>{
}
