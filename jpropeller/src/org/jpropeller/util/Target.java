package org.jpropeller.util;

/**
 * A simple interface for a target of instances of a given type
 *
 * @param <T>
 * 		The type of instance accepted by the {@link Target}
 */
public interface Target<T> {

	/**
	 * Pass an instance of T to the target
	 * @param instance	The instance
	 */
	public void put(T instance);
	
}
