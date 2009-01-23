package org.jpropeller.concurrency;

/**
 * Anything that can be aborted.
 */
public interface Abortable {

	/**
	 * Abort the run as soon as possible (may not be instant)
	 * Implementations are not required to be able to abort early,
	 * but should if possible.
	 */
	public void abort();

}