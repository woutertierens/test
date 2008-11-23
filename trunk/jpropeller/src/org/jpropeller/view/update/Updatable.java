package org.jpropeller.view.update;

/**
 * An element that must have its updates triggered externally
 * The element will not update unless this is called, but just store
 * any required updates until they can be carried out.
 * 
 * Generally implementors of this interface will automatically 
 * register themselves with the system {@link UpdateManager}
 *
 */
public interface Updatable {

	/**
	 * Update the element to reflect any changes
	 * since the last {@link #updateNow()}
	 * 
	 * The thread from which this is called depends on the {@link UpdateManager} - the
	 * default {@link UpdateManager} calls {@link #updateNow()} from the Swing thread (EDT)
	 * 
	 */
	public void updateNow();

	/**
	 * Dispose of the element. This tells it to stop listening to properties, etc.
	 * so that it can be garbage collected. The element will no longer function
	 * properly after this is called - it should no longer be used or displayed in any 
	 * user interface
	 */
	public void disposeNow();
	
}
