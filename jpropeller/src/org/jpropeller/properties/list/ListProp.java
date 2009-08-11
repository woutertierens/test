package org.jpropeller.properties.list;

import java.util.List;

import org.jpropeller.collection.CList;
import org.jpropeller.properties.Prop;

/**
 * A {@link ListProp} that can be edited, by
 * changing the list value it contains, and also by convenience
 * methods to edit the contents of the current list value itself.
 *
 * @param <L>
 * 		The type of values in lists held by the property 
 */
public interface ListProp<L> extends Prop<CList<L>>, Iterable<L>{
	
	/**
	 * Get the property value at an index
	 * {@link List#get(int)}
	 * @param index
	 * 		property index
	 * @return
	 * 		the value
	 */
	public L get(int index);

	/**
	 * Get the number of elements indexed by this property.
	 * {@link List#size()}
	 * @return
	 * 		The number of elements.
	 */
	public int size();

	/**
	 * Set a particular property
	 * {@link List#set(int, Object)}
	 * @param index
	 * 		The index of the property to set
	 * @param value
	 * 		The new value
	 * @return
	 * 		The old value
	 */
	public L set(int index, L value);
	
	/**
	 * Add a new property to the end of the list
	 * {@link List#add(Object)}
	 * @param value
	 * 		The value to add
	 */
	public void add(L value);
	
	/**
	 * Clear all property values, then replace them with the new contents,
	 * in the order returned by the iterable. This is done as an "atomic"
	 * change, so only one large change will occur
	 * @param newContents
	 * 		The new contents
	 */
	public void replace(Iterable<L> newContents);
	
}