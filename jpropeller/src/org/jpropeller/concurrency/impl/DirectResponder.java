package org.jpropeller.concurrency.impl;

import org.jpropeller.concurrency.Responder;

/**
 * This immediately services all requests - simplest
 * implementation.
 */
public class DirectResponder implements Responder {

	/**
	 * The {@link Runnable} to actually perform a response
	 */
	private Runnable responseRunnable;
	
	/**
	 * Create a {@link DirectResponder}
	 * @param responseRunnable
	 * 		The runnable to actually perform a response 
	 */
	public DirectResponder(Runnable responseRunnable) {
		this.responseRunnable = responseRunnable;
	}
	
	@Override
	public synchronized void request() {
		responseRunnable.run();
	}
	
}
