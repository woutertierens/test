package org.jpropeller.collection.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Counts references to objects using the actual identity of the referenced objects.
 * Can be iterated to give referents (objects that are referenced).
 * The referents are held by weak references only, so they can still
 * be garbage collected - in this case they will no longer be iterated, and will
 * have a reference count of 0 after collection.
 * Cannot count references to "null", since this is not meaningful or compatible with
 * weak references.
 * @param <T>		The type of counted object 
 */
public class WeakReferenceCounter<T> implements Iterable<T> {
	
	private final static Integer ONE = 1;
	Map<WeakIdentityReference<T>, Integer> referenceCounts;
	
	/**
	 * Make a reference counter
	 */
	public WeakReferenceCounter() {
		referenceCounts = new HashMap<WeakIdentityReference<T>, Integer>();
	}
	
	/**
	 * Clear all references
	 */
	public void clear() {
		referenceCounts.clear();
	}
	
	/**
	 * Add a new reference to an element - this increments the reference
	 * count for the element (or creates a reference count of 1 if there
	 * is none there before)
	 * 
	 * Returns a boolean value to indicate whether element was previously
	 * in list
	 * 
	 * @param element
	 * 		The element to which to add a reference
	 * @return
	 * 		True if the element was already present in the list (if it
	 * had a reference count already), false otherwise.
	 */
	public boolean addReference(T element) {

		WeakIdentityReference<T> reference = new WeakIdentityReference<T>(element);
		
		//Current count
		Integer count = referenceCounts.get(reference);
		
		//If there is no current count, set to 1 
		//and return false since element was not previously
		//in list
		if (count == null) {
			referenceCounts.put(reference, ONE);
			return false;
			
		//There is a current count, increment and return true
		} else {
			referenceCounts.put(reference, count+1);
			return true;
		}
	}
	
	/**
	 * Remove a current reference to an element - this decrements the reference
	 * count for the element, and removes the reference count from the map
	 * if this reduces the reference count to 0
	 * 
	 * Returns a boolean value to indicate whether element was actually present,
	 * false if the remove had no effect since element was not present anyway.
	 * 
	 * @param element	The element for which to remove a reference
	 * @return			True if the element had a reference count BEFORE 
	 * 					attempting to remove (as expected)
	 * 
	 * @throws CCollectionRuntimeException	If there is a negative reference count for the
	 * 										element, which should never occur
	 */
	public boolean removeReferenceUnchecked(T element) {
		
		WeakIdentityReference<T> reference = new WeakIdentityReference<T>(element);

		//Current count
		Integer count = referenceCounts.get(reference);

		//Null count indicates an error
		if (count == null) {
			return false;
			
		//There is a current count, decrement
		} else {
			
			//If count is non-positive, this is an error
			if (count <= 0) {
				throw new CCollectionRuntimeException("Non-positive reference count for element '" + element + "'");
				
			//If this is the last reference, then remove the
			//mapping
			} else if (count == 1) {
				referenceCounts.remove(reference);
				//element was in list
				return true;	
				
			//If there are further references, then just decrement the count
			} else {
				referenceCounts.put(reference, count-1);
				//element was in list
				return true;
			}
		}
	}
	
	/**
	 * Iterator over the referents - the objects that currently have a reference count
	 * @return An iterator of referents 
	 */
	@Override
	public Iterator<T> iterator() {
		//We just unpack the identity references
		final Iterator<WeakIdentityReference<T>> it = referenceCounts.keySet().iterator();
		return new UnpackingIterator<T>(it);
	}
	
	/**
	 * Unpacks values from {@link WeakIdentityReference}s, and filters
	 * out unpacked null values (this occurs when the values are
	 * garbage collected).
	 * It also removes elements that refer to null, since they serve
	 * no purpose.
	 *
	 * @param <T>	The type of element in iterator
	 */
	private static class UnpackingIterator<T> implements Iterator<T> {

		private final Iterator<WeakIdentityReference<T>> it;
		boolean hasNext = false;
		T next = null;
		
		private UnpackingIterator(Iterator<WeakIdentityReference<T>> it) {
			this.it = it;
			seekNext();
		}
		
		@Override
		public boolean hasNext() {
			return hasNext;
		}

		@Override
		public T next() {
			T toReturn = next;
			seekNext();
			return toReturn;
		}

		private void seekNext() {
			boolean found = false;
			
			//Keep reading from underlying iterator until
			//we find the next non-null element, or run out 
			//of elements in underlying iterator
			while (!found && it.hasNext()) {
				next = it.next().get();
				if (next != null) {
					found = true;
				//If the reference is to null, has been garbage collected
				//so we must try next, and we should also clear out the
				//useless reference from the map
				} else {
					it.remove();
				}
			}
			
			//We have another element if we found one
			hasNext = found;
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException("Cannot remove from WeakReferenceCounter's iterator");
		}
		
	}
}
