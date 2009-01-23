package org.jpropeller.properties.change;

import java.util.List;
import java.util.Map;

/**
 * The features provided by a {@link Changeable} instance
 */
public interface ChangeableFeatures {

	/**
	 * Add a listener that will see changes to this {@link Changeable}
	 * as part of the model change propagation.
	 * @param listener
	 * 		The listener
	 */
	public void addChangeableListener(Changeable listener);

	/**
	 * Remove a listener
	 * @param listener
	 * 		The listener
	 */
	public void removeChangeableListener(Changeable listener);

	/**
	 * Get the current list of {@link Changeable} listeners
	 * @return
	 * 		Listener list
	 */
	public List<Changeable> changeableListenerList();
	
	/**
	 * Add a listener to be notified of changes to this {@link Changeable},
	 * after all changes are fully propagated
	 * @param listener
	 * 		The listener
	 */
	public void addListener(ChangeListener listener);

	/**
	 * Remove a listener
	 * @param listener
	 * 		The listener
	 */
	public void removeListener(ChangeListener listener);

	/**
	 * Get the current list of {@link ChangeListener}s
	 * @return
	 * 		Listener list
	 */
	public List<ChangeListener> listenerList();

	/**
	 * Notify this {@link Changeable} that another {@link Changeable} it is listening
	 * to has changed.
	 * <br />
	 * This {@link Changeable} may then respond by changing itself - if so it must
	 * return a new {@link Change} giving the change that has been made to this
	 * {@link Changeable} as a result of this most recent change. If no change occurs,
	 * null should be returned.
	 * <br />
	 * Note that this will be called each time any {@link Changeable} this listens
	 * to changes. Each time this method is called, 
	 * this {@link Changeable} must respond to the new change, returning 
	 * any{@link Change} that is caused by it. All such returned changes will be
	 * added together by the system. This method may also make use of the
	 * initial change that caused the change propagation, and/or the list of
	 * all changes that are currently part of the propagation.
	 * <br />
	 * One VERY important consideration - the changes map is only valid during
	 * this method - it may change later, and so if it needs to be retained, it
	 * should be copied. This is because the changes map may well be updated in
	 * future to reflect a new set of changes.
	 * @param changed
	 * 		The {@link Changeable} that has changed - {@link #internalChange(Changeable, Change, List, Map)}
	 * will be called once for each change (or extension of the change) on each 
	 * {@link Changeable} listened to
	 * @param change 
	 * 		The NEW {@link Change} that has occurred to the {@link Changeable}. This is the
	 * most recent change - when multiple changes occur on one {@link Changeable}, a single
	 * {@link Change} is extended to cover the entire change. However THIS change is just the
	 * most recent individual change. To get the entire change, look the {@link Changeable} up
	 * in the allChanges map.
	 * @param initial
	 * 		The changeables that changed first, to cause this set of changes, in order
	 * @param allChanges
	 * 		The changes, as a map from each changed {@link Changeable} to a 
	 * {@link Change} that encompasses the change that occurred to it. Note
	 * that each {@link Change} may be an OVERestimate - it may indicate that
	 * more may have changed than actually has, but will not be an UNDERestimate.
	 * @return
	 * 		The new {@link Change} caused in THIS {@link Changeable} by the change
	 * it has just been notified about, or null if no new {@link Change} occurs.
	 */
	public Change internalChange(Changeable changed, Change change, List<Changeable> initial, Map<Changeable, Change> allChanges);
	
}
