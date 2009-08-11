package org.jpropeller.properties.set;

import java.util.Set;

import org.jpropeller.collection.CSet;
import org.jpropeller.properties.Prop;

/**
 * A {@link Prop} containing a {@link CSet} value, and
 * having extra methods to access methods of that
 * {@link Set} directly
 *
 * @param <T>
 * 		Type of values in the {@link CSet}
 */
public interface SetProp<T> extends Prop<CSet<T>>, Iterable<T> {
	
	/**
	 * Get the number of elements in this property.
	 * {@link Set#size()}
	 * @return
	 * 		The number of elements.s
	 */
	public int size();
	
	/**
	 * Check whether a value is contained
	 * {@link Set#contains(Object)}
	 * @param element
	 * 		The element to check
	 * @return
	 * 		True if the set contains the element, false otherwise
	 */
	public boolean contains(Object element);
	
	/**
	 * Add an element
	 * {@link Set#add(Object)}
	 * @param e
	 * 		The element to add
	 * @return
	 * 		True if the element was added, false if the set
	 * is unaltered. 
	 */
	public boolean add(T e);

	/**
	 * Remove an element
	 * {@link Set#remove(Object)}
	 * @param e
	 * 		The element to remove
	 * @return
	 * 		True if the element was removed, false if the set
	 * is unaltered. 
	 */
	public boolean remove(Object e);

	/**
	 * Clear all property values, then replace them with the new contents,
	 * in the order returned by the iterable. This is done as an "atomic"
	 * change, so only one large change will occur
	 * @param newContents
	 * 		The new contents
	 */
	public void replace(Iterable<T> newContents);

}