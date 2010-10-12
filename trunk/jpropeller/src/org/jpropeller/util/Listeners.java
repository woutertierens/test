package org.jpropeller.util;

import java.util.Iterator;

import org.jpropeller.collection.impl.ReferenceCounter;

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
public class Listeners<L> implements Iterable<L> {

	private ReferenceCounter<L> listeners;
	
	/**
	 * Add a listener
	 * @param listener
	 */
	public void add(L listener) {
		listeners().addReference(listener);
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
		return listeners().removeReferenceUnchecked(listener);
	}

	@Override
	public Iterator<L> iterator() {
		return listeners().iterator();
	}
	
	private ReferenceCounter<L> listeners() {
		if (listeners == null) listeners = new ReferenceCounter<L>();
		return listeners;
	}

}
