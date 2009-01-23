package org.jpropeller.view.update;

/**
 * Anything that can be updated
 * @author shingoki
 */
public interface Updatable {

	/**
	 * Update the object to reflect any changes
	 * since the last {@link #update()}
	 * 
	 * The thread from which this is called depends on the {@link UpdateManager} - the
	 * default {@link UpdateManager} calls {@link #update()} from the Swing thread (EDT)
	 */
	public void update();

	/**
	 * Dispose of the Updatable. This tells it to stop listening to properties,
	 * deregister with {@link UpdateManager} if appropriate, etc.
	 * so that it can be garbage collected. The {@link Updatable} will no longer function
	 * properly after this is called - it should no longer be used.
	 */
	public void dispose();

}