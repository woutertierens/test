package org.jpropeller.task.impl;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jpropeller.collection.impl.IdentityHashSet;
import org.jpropeller.concurrency.CancellableResponse;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.task.Task;

/**
 * Simple util to assist in building {@link Task}s
 * 
 * Used as:
 * <code>
 * 		BuildTask.on(a, b, c).withResponse(add);
 * </code>
 * 
 * This sets up a {@link Task} based on the {@link CancellableResponse}
 * "add", which is executed using data in {@link Changeable}s a, b and c.
 */
public class BuildTask {

	private final Set<Changeable> srcSet;
	
	/**
	 * 					Creates a new calculation on a list of sources.
	 * @param sources	The list of sources that will be watched.
	 */
	private BuildTask(Changeable ... sources) {
		Set<Changeable> srcSetM = new IdentityHashSet<Changeable>();
		for(Changeable ch : sources) {
			srcSetM.add(ch);
		}
		srcSet = Collections.unmodifiableSet(srcSetM);		
	}

	/**
	 * Make a builder for a task operating on given sources.
	 * Calling {@link #withResponse(CancellableResponse)} on this
	 * builder will produce a {@link Task}
	 * @param sources		The sources of data for the task
	 * @return				A {@link BuildTask}
	 */
	public static BuildTask on(Changeable... sources) {
		return new BuildTask(sources);
	}
	
	/**
	 * Produce a {@link Task}, with the sources provided to
	 * {@link #on(Changeable...)}, and the {@link CancellableResponse}
	 * provided to this method.
	 * @param response		The {@link CancellableResponse} for the {@link Task}
	 * @return				The {@link Task}
	 */
	public Task withResponse(final CancellableResponse response) {
		return new Task() {
			@Override
			public void respond(AtomicBoolean shouldCancel) {
				response.respond(shouldCancel);
			}
			@Override
			public Set<? extends Changeable> getSources() {
				return srcSet;
			}
		};
	}
	
}
