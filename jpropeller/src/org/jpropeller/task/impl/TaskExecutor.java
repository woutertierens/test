package org.jpropeller.task.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.jpropeller.concurrency.impl.BackgroundResponder;
import org.jpropeller.concurrency.impl.CancellingBackgroundResponder;
import org.jpropeller.concurrency.impl.ExecutorUtils;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.task.Task;

/**
 * Default implementation for execution of a {@link Task}.
 * 
 * Uses a {@link BackgroundResponder} to actually control when
 * (and in which thread) the task runs.
 * 
 * Handles listening to the {@link Task}s source {@link Changeable}s
 * to trigger a run of the task at some point after each change.
 */
public class TaskExecutor implements ChangeListener {

	private final CancellingBackgroundResponder responder;
	private final Task task;

	/**
	 * Create a {@link TaskExecutor} using the default {@link ExecutorService}
	 * with a small pool of threads
	 * @param task		The task to execute
	 */
	public TaskExecutor(Task task) {
		this(task, ExecutorUtils.getExecutorService());
	}
	
	/**
	 * Create a {@link TaskExecutor}
	 * @param task		The task to execute
	 * @param executor	The {@link ExecutorService} that actually runs tasks in the background
	 */
	public TaskExecutor(Task task, ExecutorService executor) {
		this.task = task;
		
		//Responder decides when to run the task
		this.responder = new CancellingBackgroundResponder(task, executor);
		
		//Listen to each source changeable of the task, from a view
		//level
		for (Changeable changeable : task.getSources()) {
			changeable.features().addListener(this);
		}

		//Start with a request
		responder.request();
	}

	/**
	 * The task we execute
	 * @return		{@link Task}
	 */
	public Task getTask() {
		return task;
	}

	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		//Whenever we see a change, request a response, this will (eventually)
		//run the task
		responder.request();
	}
	
	/**
	 * Dispose of the executor - this stops listening to the source {@link Changeable}s
	 * of the task, to allow the task and executor to be garbage collected.
	 */
	public void dispose() {
		for (Changeable changeable : task.getSources()) {
			changeable.features().removeListener(this);
		}
	}
	
}
