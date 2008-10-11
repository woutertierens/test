package org.jpropeller.transformer;

/**
 * Allows for transformation from one type to another
 *
 * @param <S>
 * 		The first type - transformation is from this type
 * @param <T>
 * 		The second type - transformation is to this type
 */
public interface Transformer<S, T> {

	/**
	 * Transform a value of type A to one of type B
	 * @param s
	 * 		Input to transformation
	 * @return
	 * 		Output from transformation
	 */
	public T transform (S s);
	
}
