package org.jpropeller.collection.impl;

import java.util.Map;

import org.jpropeller.properties.change.Changeable;

/**
 * Assist with the tracking of contents in a collection, by
 * adding/removing a listener as contents are added and removed, etc.
 * 
 * @param <E>
 * 		The type of element in the collection
 */
public class ContentsTracking<E> {
	
	ReferenceCounter<Object> refs = new ReferenceCounter<Object>();
	Changeable listener;
	
	/**
	 * Make contents tracking for a given listener and collection
	 * @param listener
	 * 		The listener - this is added to any tracked contents
	 */
	public ContentsTracking(Changeable listener) {
		this.listener = listener;
	}
	
	/**
	 * Start tracking an element - add it to reference counts,
	 * start listening to it if we haven't already, etc.
	 * @param e
	 * 		The element to track
	 */
	public void startTrackingElement(E e) {
		
		//Ignore nulls
		if (e == null) return;
		
		//Add reference for the new element
		boolean elementAlreadyPresent = refs.addReference(e);
		
		//TODO is instanceof here a performance issue?
		//If the element is new and changeable, we need to listen to it
		if (!elementAlreadyPresent) {
			if (e instanceof Changeable) {
				((Changeable)e).features().addChangeableListener(listener);
			}
		}
	}
	
	/**
	 * Stop tracking an element - remove it from reference counts,
	 * stop listening to it if its reference count drops to zero,
	 * etc.
	 * @param e
	 * 		The element to stop tracking
	 */
	public void stopTrackingElement(Object e) {
		
		//Ignore nulls
		if (e == null) return;
		
		//remove reference for the new element
		boolean elementStillPresent = refs.removeReference(e);
		
		//TODO is instanceof here a performance issue?
		//If the element is no longer in the list, and is changeable, 
		//we need to stop listening to it
		if (!elementStillPresent) {
			if (e instanceof Changeable) {
				((Changeable)e).features().removeChangeableListener(listener);
			}
		}
	}
	
	/**
	 * Stop tracking every element in the list, so we clear reference
	 * counts and stop listening to beans, etc.
	 */
	public void clearAllTracking() {
		
		//This method bypasses removeReference(e) and just
		//stops listening to any referenced element, then clears
		//counts
		
		//Stop listening to all elements that have a reference count
		for (Object e : refs.getReferenceCounts().keySet()) {
			//TODO is instanceof here a performance issue?
			//If the element is a changeable, we need to stop listening to it
			if (e instanceof Changeable) {
				((Changeable)e).features().removeChangeableListener(listener);
			}
		}
		
		//Clear all reference counts
		refs.clear();
	}
	
	/**
	 * Get the reference counts for tracked instances
	 * as a map from referenced object to number of references
	 * (number of times that object is in the collection)
	 * @return
	 * 		reference counts
	 */
	public Map<Object, Integer> getReferenceCounts() {
		return refs.getReferenceCounts();
	}
}
