package org.jpropeller.collection.impl;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Counts references to objects using the actual identity of the referenced objects.
 * Can be iterated to give referees
 * @param <T>		The type of counted object 
 */
public class ReferenceCounter<T> implements Iterable<T> {
	
	private final static Integer ONE = 1;
	Map<T, Integer> referenceCounts;
	Map<T, Integer> umReferenceCounts;

	/**
	 * Make a reference counter
	 */
	public ReferenceCounter() {
		referenceCounts = new IdentityHashMap<T, Integer>();
		umReferenceCounts = Collections.unmodifiableMap(referenceCounts);		
	}
	
	/**
	 * Get the reference counts as a map from referenced object (instance)
	 * to number of references
	 * @return
	 * 		reference counts
	 */
	public Map<T, Integer> getReferenceCounts() {
		return umReferenceCounts;
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

		//Current count
		Integer count = referenceCounts.get(element);
		
		//If there is no current count, set to 1 
		//and return false since element was not previously
		//in list
		if (count == null) {
			referenceCounts.put(element, ONE);
			return false;
			
		//There is a current count, increment and return true
		} else {
			referenceCounts.put(element, count+1);
			return true;
		}
	}
	
	/**
	 * Remove a current reference to an element - this decrements the reference
	 * count for the element, and removes the reference count from the map
	 * if this reduces the reference count to 0
	 * 
	 * Returns a boolean value to indicate whether element is still present in
	 * the reference counter after the removal (that is, if the removal is not of the last
	 * reference to the element)
	 * 
	 * @param element
	 * 		The element for which to remove a reference
	 * @return
	 * 		True if the element is still present after removing the reference,
	 * (if it is not of the last copy of the element). False otherwise (if the
	 * element is no longer present in the list)
	 * 
	 * @throws CCollectionRuntimeException	If there is no reference to the element.
	 */
	public boolean removeReference(T element) {
		
		//Current count
		Integer count = referenceCounts.get(element);
		
		//If there is no current count, we have an error
		if (count == null) {
			throw new CCollectionRuntimeException("Attempted to remove a reference to an element not present in the reference map: '" + element + "'");
			
		//There is a current count, decrement
		} else {
			
			//If count is non-positive, this is an error
			if (count <= 0) {
				throw new CCollectionRuntimeException("Non-positive reference count for element '" + element + "'");
				
			//If this is the last reference, then remove the
			//mapping and return false, since the element is no
			//longer in the list
			} else if (count == 1) {
				referenceCounts.remove(element);
				return false;
				
			//If there are further references, then just decrement the count
			} else {
				referenceCounts.put(element, count-1);				
				return true;
			}
		}
	}

	/**
	 * Remove a current reference to an element - this decrements the reference
	 * count for the element, and removes the reference count from the map
	 * if this reduces the reference count to 0
	 * 
	 * This version does NOT throw an exception if there is no current reference to
	 * be removed, it just does nothing
	 * 
	 * The return is also different - it indicates whether the element had a reference
	 * count BEFORE attempting to remove. 
	 * 
	 * @param element
	 * 		The element for which to remove a reference
	 * @return
	 * 		True if the element is was had a reference count BEFORE attempting to remove.
	 */
	public boolean removeReferenceUnchecked(T element) {
		
		//Current count
		Integer count = referenceCounts.get(element);
		
		//If there is no current count, do nothing
		if (count == null) {
			return false;
			
		//There is a current count, decrement
		} else {
			
			//If count is non-positive, this is an error
			if (count <= 0) {
				throw new CCollectionRuntimeException("Non-positive reference count for element '" + element + "'");
				
			//If this is the last reference, then remove the
			//mapping and return true, since the element was
			//present
			} else if (count == 1) {
				referenceCounts.remove(element);
				return true;
				
			//If there are further references, then just decrement the count
			} else {
				referenceCounts.put(element, count-1);				
				return true;
			}
		}
	}
	
	/**
	 * Iterator over the referees - the objects that currently have a reference count
	 * @return An iterator of referees 
	 */
	@Override
	public Iterator<T> iterator() {
		return umReferenceCounts.keySet().iterator();
	}
}
