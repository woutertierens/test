package org.jpropeller.concurrency.impl;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jpropeller.concurrency.Responder;


/**
 * This coalesces repeated requests that occur at too 
 * low a time interval, while also ensuring that response occur 
 * with at least a maximum interval, when requested. To reduce 
 * latency, the first request after a sufficient interval serviced 
 * through immediately - subsequent requests after the
 * first may then be coalesced and given a single shared response.
 *
 */
public class CoalescingResponder implements Responder {

	/**
	 * Time of last request
	 */
	private long lastRequestTime = 0;
	
	/**
	 * When requests are seen less than this time in ms apart, they
	 * may be coalesced
	 */
	private long fusionInterval;
	
	/**
	 * {@link Executor} used to schedule regular responses
	 */
	private ScheduledExecutorService executor;
	
	/**
	 * The {@link Runnable} to actually perform a response
	 */
	private Runnable responseRunnable;
	
	/**
	 * True if a request is waiting for a response
	 */
	private boolean requestPending = false;
	
	/**
	 * Create an {@link CoalescingResponder} with default timings
	 * of 50ms fusion interval, and a regular response every
	 * 100ms if required
	 * This also starts the regular responder task
	 * @param responseRunnable
	 * 		The runnable to actually perform a response 
	 */
	public CoalescingResponder(Runnable responseRunnable) {
		this(50, 100, responseRunnable);
	}
	
	/**
	 * Create an {@link CoalescingResponder}
	 * This also starts the regular responder task
	 * 
	 * @param fusionInterval 
	 * When requests are seen less than this time in ms apart, they 
	 * may be coalesced
	 * 
	 * @param regularResponseInterval
	 * Responses will be sent out with at most this time between them,
	 * when requests are being seen
	 * 
	 * @param responseRunnable
	 * 		The runnable to actually perform a response 
	 */
	public CoalescingResponder(long fusionInterval, long regularResponseInterval, Runnable responseRunnable) {
		super();
		this.fusionInterval = fusionInterval;
		this.responseRunnable = responseRunnable;
		
		//Schedule a regular response at regularResponseInterval
		executor = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory());
		executor.scheduleAtFixedRate(
				new Runnable() {
					@Override
					public void run() {
						respond();
					}
				}, 
				regularResponseInterval, regularResponseInterval, TimeUnit.MILLISECONDS);
	}

	//FIXME the synchronization on this and respond() can deadlock, when a
	//request is made by a thread that is blocking a response, which can happen,
	//e.g. when concluding a change inside another change with the ChangeSystem - 
	//in this case we hold mainLock twice, and so when the inner change unlocks
	//it, it is still held. This has been fixed from the ChangeSystem, by
	//only requesting when mainLock is not held, but it would be good to make
	//deadlock impossible by allowing calls to request while responding - quite
	//hard to do though
	@Override
	public synchronized void request() {
		
		//Work out timing
		long time = System.currentTimeMillis();
		long interval = time - lastRequestTime;
		lastRequestTime = time;
		
		//Now have a pending request
		requestPending = true;
		
		//If we can't coalesce with the last request, then respond immediately
		//Otherwise, the regular responder will catch it and respond sometime soon
		if (interval > fusionInterval) {
			respond();
		}
		
	}

	/**
	 * Actually perform a response (if requested)
	 */
	private synchronized void respond() {
		if (requestPending) {
			responseRunnable.run();
			requestPending = false;
		}
	}
	
}
