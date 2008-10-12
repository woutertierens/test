package org.jpropeller.transformer;

import org.jpropeller.bean.Bean;
import org.jpropeller.path.BeanPath;
import org.jpropeller.properties.Prop;

/**
 * The type of {@link Transformer} needed for a "via" step in a {@link BeanPath}.
 * All but the last step in the path are via steps.
 * @param <S>
 * 		The type of bean transformed from 
 * @param <T> 
 *		The type of bean transformed to 
 */
public interface BeanPathVia<S extends Bean, T extends Bean> extends Transformer<S, Prop<T>>{
}
