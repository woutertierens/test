package org.jpropeller.util;

/**
 * A simple interface for a source of instances of a given type
 *
 * @param <T>
 * 		The type of instance provided by the source
 */
public interface Source<T> {

	/**
	 * Get the next instance of T from the source
	 * @return		The next instance
	 * @throws NoInstanceAvailableException	If there
	 * 				is no suitable instance available. 
	 */
	public T get() throws NoInstanceAvailableException;
	
}
