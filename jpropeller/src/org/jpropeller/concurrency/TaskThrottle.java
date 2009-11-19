package org.jpropeller.concurrency;

/**
 * A {@link TaskThrottle} is used by code that wishes to perform some
 * task without saturating the CPU. To do this, it inserts {@link Thread#sleep(long)}
 * calls to give (approximately) a certain percentage of time spent sleeping.
 * This can also take into account possible platform problems, for example only ever
 * trying to sleep for at 10ms increments.
 * 
 * This may seem like a slightly hacky way to reduce thread load, but in real world testing
 * on Linux, thread priorities etc. are essentially useless whereas using this approach
 * yields smooth UI interaction while background computationally intense tasks are running.
 * 
 * Note that there IS a bug in linux that prevents the thread priorities taking effect
 * unless running as root etc., but we have tried the workaround and it makes no difference.
 */
public class TaskThrottle {

	private static final int MIN_ACCUMULATED = -3000;
	private static final int MAX_ACCUMULATED = 3000;

	private final static long ELAPSED_LIMIT = 500;
	
	private boolean firstCall;
	private long lastCall;
	private long accumulatedRuntime;
	private double sleepRatio;
	private long minSleep;
	
	/**
	 * Make a {@link TaskThrottle} that will sleep
	 * for 10% of total time, minimum 1 ms 
	 */
	public TaskThrottle() {
		this(0.1, 1);
	}
	
	/**
	 * Create a {@link TaskThrottle}
	 * @param sleepRatio		The ratio of sleep time to total time. E.g. 0.5 will sleep
	 * 							for 0.5s per 1s total time.
	 * @param minSleep			The minimum number of milliseconds we will ever sleep for.
	 * 							Smaller required sleep times will just accumulate until
	 * 							a longer sleep is required
	 */
	public TaskThrottle(double sleepRatio, long minSleep) {
		this.sleepRatio = sleepRatio;
		this.minSleep = minSleep;
		firstCall = true;
	}
	
	/**
	 * Restart throttling - when no processing
	 * has been done for some time, this should be the first call
	 * to the {@link TaskThrottle}. Otherwise, it will carry out
	 * the required sleep for time elapsed since the last call. 
	 */
	public void restart() {
		firstCall = true;
		throttle();
	}
	
	/**
	 * Throttle - sleep if required.
	 * @return	The number of milliseconds slept, may be 0
	 */
	public long throttle() {
		//On first call just remember time and reset everything else
		if (firstCall) {
			accumulatedRuntime = 0;
			lastCall = System.currentTimeMillis();
			firstCall = false;
			return 0;
		}
		
		long current = System.currentTimeMillis();
		long elapsed = current - lastCall;
		
		//If elapsed is more than sensible limit, cap it
		if (elapsed > ELAPSED_LIMIT) elapsed = ELAPSED_LIMIT;
		
//		if (elapsed > 20) {
//			System.out.println("elapsed " + elapsed);
//		}
		
		accumulatedRuntime += elapsed;
		
		//Default to no time slept
		long slept = 0;
		
		//Calculate the required sleep time
		long required = (long)(((double)accumulatedRuntime) * sleepRatio); 
		
		//If we have enough time, do the sleep
		if (required >= minSleep) {
			try {
				Thread.sleep(required);
			} catch (InterruptedException e) {
				//We will check the actual sleep time after waking
			}
			
			slept = System.currentTimeMillis() - current;
			
			//System.out.print("Accumulated " + accumulatedRuntime + ", needed " + required + ", slept for " + slept + ", ");
			
			//Reduce accumulated RUN time by the amount we just slept for, allowing for ratio
			//If we overslept, this can be negative without causing a problem
			accumulatedRuntime -= (long)(((double)slept) / sleepRatio);
			//System.out.println("accumulated now " + accumulatedRuntime);
		}
		
		//Limit range of accumulated run time
		if (accumulatedRuntime < MIN_ACCUMULATED) {
			accumulatedRuntime = MIN_ACCUMULATED;
		}
		if (accumulatedRuntime > MAX_ACCUMULATED) {
			accumulatedRuntime = MAX_ACCUMULATED;
		}
		
		lastCall = current;
		return slept;
		
	}
	
}
