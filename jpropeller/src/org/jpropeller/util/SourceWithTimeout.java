package org.jpropeller.util;

/**
 * A {@link Source} that can be queried with a timeout.
 *
 * @param <T>
 * 		The type of instance provided by the source
 */
public interface SourceWithTimeout<T> extends Source<T> {

	/**
	 * Get the next instance of T from the source, waiting at
	 * most a certain time.
	 * @param timeoutMillis		The maximum time in milliseconds for
	 * 							which to wait for a new instance
	 * @return		The next instance
	 * @throws NoInstanceAvailableException	If there
	 * 				is no suitable instance available, and
	 * 				none is expected to be available.
	 * @throws NoInstanceAvailableWithinTimeoutException	If there is 
	 * 				no suitable instance available within timeout, but
	 * 				an instance may become available given more time.
	 */
	public T get(long timeoutMillis) throws NoInstanceAvailableException, NoInstanceAvailableWithinTimeoutException;
	
}
