package org.jpropeller.util;

/**
 * Simple util class for timing things
 */
public class StopWatch {

	long startTime;
	
	/**
	 * Create and start a stopwatch
	 */
	public StopWatch() {
		startTime = System.currentTimeMillis();
	}
	
	/**
	 * Stop the stopwatch, and return time since it started, in seconds
	 * @return		Elapsed time in seconds
	 */
	public double stop() {
		long millis = System.currentTimeMillis() - startTime;
		return ((double)millis)/1000d;
	}
	
}
