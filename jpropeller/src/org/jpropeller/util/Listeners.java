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
	

//Another weak implementation, currently can't be used. For example, 
//task executors are sometimes only kept alive by the fact they are 
//listening to something.
//WEAK implementation
//	 * References to the listeners are weak, so will not prevent
//	 * the listeners from being garbage collected.
//
//	private final static Integer ONE = 1;
//
//	private final IdentityMap<L, Integer> counts;
//
//	/**
//	 * Create {@link Listeners}
//	 */
//	public Listeners() {
//		counts = IdentityMap.<L, Integer>weak();
//	}
//	
//	/**
//	 * Add a listener
//	 * @param listener
//	 */
//	public void add(L listener) {
//
//		Integer count = counts.get(listener);
//		
//		if (count == null) {
//			counts.put(listener, ONE);
//		} else {
//			counts.put(listener, count+1);
//		}
//	}
//	
//	/**
//	 * Remove a listener
//	 * @param listener
//	 * @return		True if the listener had been added (as expected),
//	 * 				or false if the listener was not registered, and
//	 * 				so had either not been added, or removed more times
//	 * 				than added.
//	 */
//	public boolean remove(L listener) {
//		
//		//Current count
//		Integer count = counts.get(listener);
//		
//		//If there is no current count, return false to indicate
//		//that a listener was removed which was not added
//		if (count == null) {
//			return false;
//		}
//		
//		//If count is non-positive, this is an error
//		if (count <= 0) {
//			throw new CCollectionRuntimeException("Non-positive count for listener '" + listener + "'");
//			
//		//If this is the last reference, then remove the
//		//mapping and return false, since the element is no
//		//longer in the list
//		} else if (count == 1) {
//			counts.remove(listener);
//			
//		//If there are further references, then just decrement the count
//		} else {
//			counts.put(listener, count-1);				
//		}
//		
//		return true;
//	}
//
//	@Override
//	public Iterator<L> iterator() {
//		return counts.iterator();
//	}
//	
//	public static void main(String[] args) {
//		Listeners<String> l = new Listeners<String>();
//		l.add("bob");
//		System.out.println("Added");
//		for (String s : l) {
//			System.out.println(s);
//		}
//		l.remove("bob");
//		System.out.println("Removed");
//		for (String s : l) {
//			System.out.println(s);
//		}
//
//	}
	
//	//TODO if this causes any problems, replace it with a ReferenceCounter
//	private WeakReferenceCounter<L> listeners;
//	
//	/**
//	 * Add a listener
//	 * @param listener
//	 */
//	public void add(L listener) {
//		listeners().addReference(listener);
//	}
//
//	/**
//	 * Remove a listener
//	 * @param listener
//	 * @return		True if the listener had been added (as expected),
//	 * 				or false if the listener was not registered, and
//	 * 				so had either not been added, or removed more times
//	 * 				than added.
//	 */
//	public boolean remove(L listener) {
//		return listeners().removeReferenceUnchecked(listener);
//	}
//
//	@Override
//	public Iterator<L> iterator() {
//		return listeners().iterator();
//	}
//	
//	private WeakReferenceCounter<L> listeners() {
//		if (listeners == null) listeners = new WeakReferenceCounter<L>();
//		return listeners;
//	}
}
