package org.jpropeller.view.update;

import org.jpropeller.view.View;

/**
 * A {@link View} that must have its updates triggered externally
 * The view will not update unless this is called, but just store
 * any required updates until they can be carried out.
 * 
 * Generally implementors of this interface will automatically 
 * register themselves with the system {@link UpdateManager}
 *
 * @param <M>
 * 		The type of model
 */
public interface UpdatableView<M> extends View<M> {

	/**
	 * Update the display of the view to reflect any changes
	 * since the last {@link #update()}
	 * 
	 * The thread from which this is called depends on the {@link UpdateManager} - the
	 * default {@link UpdateManager} calls {@link #update()} from the Swing thread (EDT)
	 * 
	 */
	public void update();

	/**
	 * Dispose of the view. This tells it to stop listening to properties, etc.
	 * so that it can be garbage collected. The view will no longer function
	 * properly after this is called - it should no longer be in any user interface.
	 */
	public void dispose();
	
}
