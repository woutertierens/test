/*
 *  $Id: MutablePropPrimitive.java,v 1.1 2008/03/24 11:19:49 shingoki Exp $
 *
 *  Copyright (c) 2008 shingoki
 *
 *  This file is part of jpropeller, see http://jpropeller.sourceforge.net
 *
 *    jpropeller is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    jpropeller is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with jpropeller; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.jpropeller.concurrency.impl;

import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jpropeller.concurrency.Responder;
import org.jpropeller.util.GeneralUtils;

/**
 * A utility that handles the logic of running a background
 * process in response to a series of requests. 
 * This response process is defined by the requirements 
 * that:
 * 		1) Every request must eventually be followed by a response. Requests
 * are synchronised so they must occur sequentially, to allow this to
 * be meaningful.
 * 		2) No more than one response is in process at any time
 * 		3) The response runs a {@link Runnable}, logging any
 * exceptions, and exceptions will not prevent future responses
 * from running.
 * 
 */
public class BackgroundResponder implements Responder {

	private final static Logger logger = GeneralUtils.logger(BackgroundResponder.class);
	
	Object responseLock = new Object();
	boolean responseRequired = false;
	boolean currentlyRunning = false;

	Runnable responseRunnable;
	Executor executor;

	/**
	 * Create a new {@link BackgroundResponder}
	 * @param responseRunnable
	 * 		The runnable that performs a response
	 * 		Must be suitable to be run at any time, in a new thread, being
	 * called repeatedly although not more than once concurrently - it is obviously
	 * best if this has no state.
	 * @param executor
	 * 		The {@link Executor} to use to actually run the responseRunnable
	 */
	public BackgroundResponder(Runnable responseRunnable, Executor executor) {
		super();
		this.executor = executor;
		this.responseRunnable = responseRunnable;
	}

	@Override
	public void request() {
		synchronized (responseLock) {
			
			//If we are running a current thread, then just register that we need a response (again) 
			//when it finishes
			if (currentlyRunning) {
				//Would cancel the current thread here if we could
				
				//This will be checked when the current thread finishes.
				//The responseLock ensures that the current thread is not actually
				//finishing as we do this
				responseRequired = true;	
				
			//If we are not running, then we can service the update immediately
			} else {
				launchNewResponse();
			}
		}
	}
	
	/**
	 * Start a new response
	 */
	private final void launchNewResponse() {
		synchronized (responseLock) {
			//Request is dealt with for now
			responseRequired = false;
			
			//Run the runnable
			executor.execute(wrapperRunnable);
			currentlyRunning = true;
		}
	}
	
	//Runnable to execute the responseRunnable, then handle triggering another response if required, etc.
	private final Runnable wrapperRunnable = new Runnable() {
		
		@Override
		public void run() {
			try {
				
				//Run the actual response target we were given
				responseRunnable.run();
				
			//We can't deal meaningfully with response errors, except by logging
			} catch (Exception e) {
				//TODO we could let a listener know about this
				logger.log(Level.WARNING, "Exception in response task", e);
				
			//Make sure we always handle the response requirements, etc.
			} finally {
				synchronized (responseLock) {
					//We are finished, so we are not currently running any more
					currentlyRunning = false;
					
					//If we need another response, start another one straight away
					if (responseRequired) {
						responseRequired = false;
						launchNewResponse();
					}
					
				}
			}			
		}
	};

}
