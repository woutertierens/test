package org.jpropeller.transformer;

/**
 * Allows for transformation backwards and forwards
 * between types - a Bidirectional {@link Transformer} 
 * 
 * This will often have the property that 
 * <code>
 * 	transformBack(transform(s)).equals(s)
 * </code>
 * is true, but this is not necessarily the case.
 *
 * @param <S>
 * 		The first type - forward transformation is from this type
 * @param <T>
 * 		The second type - forward transformation is to this type
 */
public interface BiTransformer<S, T>  extends Transformer<S, T> {

	/**
	 * Transform a value "back" from type T to type S
	 * @param t		Input to transformation
	 * @return		Output from transformation
	 */
	public S transformBack (T t);
	
}
