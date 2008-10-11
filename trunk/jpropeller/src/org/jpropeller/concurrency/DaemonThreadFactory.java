package org.jpropeller.concurrency;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * A {@link ThreadFactory} that will provide threads from
 * another factory, after having set them to be Daemon threads,
 * i.e. threads that will not prevent termination of an application.
 * 
 * These threads can be used, for example, for execution of non-critical
 * tasks that may be terminated at any point, for example for cosmetic
 * UI updates, etc.
 */
public class DaemonThreadFactory implements ThreadFactory {

	ThreadFactory delegate;
	int priority;
	
	/**
	 * Make a new factory producing threads from
	 * {@link Executors#defaultThreadFactory()} with
	 * priority {@link Thread#NORM_PRIORITY}
	 */
	public DaemonThreadFactory() {
		this(Executors.defaultThreadFactory());
	}
	
	/**
	 * Make a new factory
	 * @param delegate
	 * 		This delegate is used to provide threads - these are
	 * then just set to be daemon threads before being returned
	 */
	public DaemonThreadFactory(ThreadFactory delegate) {
		this(delegate, Thread.NORM_PRIORITY);
	}

	/**
	 * Make a new factory
	 * @param delegate
	 * 		This delegate is used to provide threads - these are
	 * then just set to be daemon threads with specified priority
	 * before being returned
	 * @param priority
	 * 		The priority to which to set threads before returning
	 * them
	 */
	public DaemonThreadFactory(ThreadFactory delegate, int priority) {
		super();
		this.delegate = delegate;
		this.priority = priority;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread thread = delegate.newThread(r);
		thread.setDaemon(true);
		thread.setPriority(priority);
		return thread;
	}
}
