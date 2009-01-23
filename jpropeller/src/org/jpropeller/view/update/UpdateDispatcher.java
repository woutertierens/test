package org.jpropeller.view.update;

import java.util.Collection;

import javax.swing.SwingUtilities;

/**
 * An {@link UpdateDispatcher} will accept {@link Updatable}s that need to
 * be updated, via calling the {@link #dispatch(Updatable)} method. 
 * It is guaranteed that if an {@link Updatable} is dispatched, it will be updated at some
 * point in the future, generally as soon as possible. If an {@link Updatable} is dispatched
 * again BEFORE it is updated, then that {@link Updatable} may only be updated once - that is,
 * one update may be used to service multiple dispatch requests.
 * 
 * An example implementation would be a Swing dispatcher that responded to
 * a dispatch by adding the {@link Updatable} to a set to be updated, and then using 
 * {@link SwingUtilities#invokeLater(Runnable)} to call the updates. In this case,
 * if more {@link Updatable}s are dispatched, or the same {@link Updatable} is dispatched 
 * again, while the swing update is pending, all these {@link Updatable}s will be updated at 
 * the same time, and the multiply-dispatched {@link Updatable}s will only be updated once.
 */
public interface UpdateDispatcher {

	/**
	 * Schedule an {@link Updatable} to be updated in the future
	 * @param updatable
	 * 		The {@link Updatable} to be updated
	 */
	public void dispatch(Updatable updatable);

	/**
	 * Schedule a collection of {@link Updatable}s to be updated in the future
	 * This method should be used in preference to 
	 * {@link #dispatch(Updatable)} where possible, since it may
	 * be more efficient
	 * @param updatables
	 * 		The {@link Updatable}s to be updated
	 */
	public void dispatch(Collection<Updatable> updatables);

}
