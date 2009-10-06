package org.jpropeller.task.impl;

import java.util.List;
import java.util.Map;

import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.ChangeableFeatures;
import org.jpropeller.properties.change.impl.ChangeableFeaturesDefault;
import org.jpropeller.properties.change.impl.InternalChangeImplementation;
import org.jpropeller.system.Props;
import org.jpropeller.task.Task;

/**
 * This will execute the given task synchronously whenever one if
 * its sources changes.
 */
public class SynchronousTaskExecutor implements Changeable {

	private Task task;
	
	private final ChangeableFeatures features = new ChangeableFeaturesDefault(new InternalChangeImplementation() {
		@Override
		public Change internalChange(Changeable changed, Change change,
				List<Changeable> initial, Map<Changeable, Change> changes) {
			//Add the task to be run after propagation
			Props.getPropSystem().getChangeSystem().addTask(task);
			
			//We haven't changed ourselves - we have no state
			return null;
		}
	}, this);
	
	/**
	 * Create a {@link SynchronousTaskExecutor}
	 * This will execute the given task synchronously whenever one if
	 * its sources changes.
	 * @param task	The task to execute
	 */
	public SynchronousTaskExecutor(Task task) {
		this.task = task;
		
		//Listen to each source changeable of the task, from a view
		//level
		for (Changeable changeable : task.getSources()) {
			changeable.features().addChangeableListener(this);
		}
		
		//Start updated
		Props.getPropSystem().getChangeSystem().addTask(task);
	}
	
	@Override
	public ChangeableFeatures features() {
		return features;
	}
	
	/**
	 * Stop the executor running, by removing it as a listener from task sources
	 */
	public void dispose() {
		//Dispose the executor, so that it will not run any more
		for (Changeable changeable : task.getSources()) {
			changeable.features().removeChangeableListener(this);
		}
		task = null;
	}

}
