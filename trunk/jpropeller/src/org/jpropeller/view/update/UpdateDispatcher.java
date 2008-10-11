package org.jpropeller.view.update;

import java.util.Collection;

import javax.swing.SwingUtilities;

/**
 * An {@link UpdateDispatcher} will accept {@link UpdatableView}s that need to
 * be updated, via calling the {@link #dispatch(UpdatableView)} method. 
 * It is guaranteed that if a view is dispatched, it will be updated at some
 * point in the future, generally as soon as possible. If a view is dispatched
 * again BEFORE it is updated, then that view may only be updated once - that is,
 * one update may be used to service multiple dispatch requests.
 * 
 * An example implementation would be a Swing dispatcher that responded to
 * a dispatch by adding the view to a set to be updated, and then using 
 * {@link SwingUtilities#invokeLater(Runnable)} to call the updates. In this case,
 * if more views are dispatched, or the same view is dispatched again, while the 
 * swing update is pending, all these views will be updated at the same time, and the
 * multiply-dispatched views will only be updated once.
 */
public interface UpdateDispatcher {

	/**
	 * Schedule a view to be updated in the future
	 * @param view
	 * 		The view to be updated
	 */
	public void dispatch(UpdatableView<?> view);

	/**
	 * Schedule a collection of views to be updated in the future
	 * This method should be used in preference to 
	 * {@link #dispatch(UpdatableView)} where possible, since it may
	 * be more efficient
	 * @param views
	 * 		The views to be updated
	 */
	public void dispatch(Collection<UpdatableView<?>> views);

}
