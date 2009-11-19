package org.jpropeller.concurrency.impl;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jpropeller.properties.calculated.background.impl.BackgroundCalculatedProp;
import org.jpropeller.task.impl.TaskExecutor;

/**
 * Utility methods and instances for {@link Executor}s used
 * by jpropeller classes (notably {@link BackgroundCalculatedProp}
 * and {@link TaskExecutor}.
 */
public class ExecutorUtils {

	/**
	 * Private, static methods only
	 */
	private ExecutorUtils(){}

	//TODO work out optimal number of threads
	private final static ExecutorService DEFAULT_EXECUTOR_SERVICE = 
		Executors.newFixedThreadPool(4, new DaemonThreadFactory(Executors.defaultThreadFactory(), Thread.NORM_PRIORITY-1));

	/**
	 * Get the shared default {@link ExecutorService}
	 * @return	{@link ExecutorService}
	 */
	public final static ExecutorService getExecutorService() {
		return DEFAULT_EXECUTOR_SERVICE;
	}
	
}
