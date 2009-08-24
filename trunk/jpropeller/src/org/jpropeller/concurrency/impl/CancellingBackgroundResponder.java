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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jpropeller.concurrency.CancellableResponse;
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
 * 		3) The response is carried out by a {@link CancellableResponse}, logging any
 * exceptions, and exceptions will not prevent future responses
 * from running.
 * 		4) If a request is received while a response is running, that response
 * will be cancelled, and another response launched when it completes.
 */
public class CancellingBackgroundResponder implements Responder {

	private final static Logger logger = GeneralUtils.logger(CancellingBackgroundResponder.class);
	
	private final Object responseLock = new Object();
	private boolean responseRequired = false;
	private boolean currentlyRunning = false;

	private final CancellableResponse response;
	private final Executor executor;
	
	private final AtomicBoolean shouldCancel = new AtomicBoolean(false);

	/**
	 * Create a new {@link CancellingBackgroundResponder}
	 * @param response 	The response
	 * @param executor 	The {@link Executor} to use to actually run the responseRunnable
	 */
	public CancellingBackgroundResponder(CancellableResponse response, Executor executor) {
		super();
		this.executor = executor;
		this.response = response;
	}

	@Override
	public void request() {
		synchronized (responseLock) {
			
			//If we are running a current thread, then register that we need a response (again) 
			//when it finishes, and cancel it
			if (currentlyRunning) {
				//This will be checked when the current thread finishes.
				//The responseLock ensures that the current thread is not actually
				//finishing as we do this
				responseRequired = true;

				//Cancel current response invocation
				shouldCancel.set(true);

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
			
			//Make sure response doesn't cancel instantly
			shouldCancel.set(false);
			
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
				response.respond(shouldCancel);
				
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
