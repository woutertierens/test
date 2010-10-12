package org.jpropeller.util;

import java.util.Iterator;

/**
 * Class for handling a set of listeners.
 * This tracks the number of times a listener is added,
 * so that it will not be removed from the set until it has been
 * removed an equal number of times.
 * 
 * 
 * Listeners can be iterated over.
 * 
 * @param <L> 	The type of listener
 */
public class WeakListeners<L> implements Iterable<L> {

//Implementation using Counter rather than ReferenceCounter, giving a weak reference to
//all listeners, so they may be garbage collected. This is the best overall behaviour, since
//it prevents memory leaks where objects are retained in memory just because they listen for
//changes. This is normally invalid, since there is little reason to deliver change notifications
//to instances that are otherwise unreachable:
//
// 1) 	Props and other Changeable instances are not useful when they are no longer reachable.
//		Say B listens to A to update itself - if B is no longer reachable except as a listener to
//		A, its state can never be read, so there is no point in keeping B around. Thus A should not
//		have a strong reference to B just because B is a listener. There might be a more complex situation
//		where there exists some Changeable C which listens to B and where C IS still reachable. In this case
//		we might worry that C will stop updating because it does not see changes to A via B. However this does
//		not occur - if C updates based on changes to B, it ALSO retains a reference to B so that it can read
//		it, and this reference will keep B alive as long as C needs it.
//
//	2)	Views are also stored using Listeners instances. They are in the same situation - they are only useful
//		while they are visible somehow, and to be visible they must have references to them OTHER than listener
//		references. This removes the need for the current dispose methods, which may be removed in a 
//		future version.
//
//	3)	Tasks are a trickier problem. Using current practice, it is assumed that it is enough to create a task
//		and assign it to an executor, and that the task will be retained by the fact that it is listening to
//		its sources. However this is NOT true if listeners are only retained by a weak reference. Hence any
//		uses of tasks must now ensure that one or more suitable objects retain a reference to them. Normally it is clear
//		what instance(s) should retain this reference - the instances that are affected/updated by the task
//		executing. So if a Task T updates objects A and B, both of those objects should be made to retain a reference
//		to T. This ensures that as long as A or B are reachable, T will also be reachable and so will not be GCed. This
//		allows T to exist for exactly as long as it is needed to keep A and B updated.
	
	private WeakReferenceCounter<L> listeners;
	
	/**
	 * Add a listener
	 * @param listener
	 */
	public void add(L listener) {
		listeners().add(listener);
	}

	/**
	 * Remove a listener
	 * @param listener
	 * @return		True if the listener had been added (as expected),
	 * 				or false if the listener was not registered, and
	 * 				so had either not been added, or removed more times
	 * 				than added.
	 */
	public boolean remove(L listener) {
		//Listener was present if new count is 0 or more.
		return listeners().remove(listener) >= 0;
	}

	@Override
	public Iterator<L> iterator() {
		return listeners().iterator();
	}
	
	private WeakReferenceCounter<L> listeners() {
		if (listeners == null) listeners = new WeakReferenceCounter<L>();
		return listeners;
	}

}
