package org.jpropeller.view.list.impl;

import org.jpropeller.collection.ObservableList;

/**
 * Filters elements which are proposed for addition to an {@link ObservableList},
 * possibly modifying them, and throwing an exception if an element
 * should not be added
 * @author websterb
 *
 * @param <E>
 * 		The type of element in the {@link ObservableList}
 */
public interface ObservableListAddFilter<E> {

	/**
	 * Filter the given element.
	 * The element is proposed for addition to a ListBean, but has not
	 * yet been added.
	 * Implementations may modify the element, for example to rename it
	 * to avoid conflicts with existing list elements, etc.
	 * If the element is not suitable for addition to the list (in the
	 * opinion of the filter) then an exception is thrown. It is up
	 * to the user of the filter whether to respect this opinion. 
	 * @param element
	 * 		The element proposed for addition
	 * @param list
	 * 		The list to which the element will be added
	 * @param index
	 * 		The position at which the element will be added
	 * @throws ListBeanAddException
	 * 		If this filter does not accept the addition of the element
	 * to the list. The exception carries an objection in a user-readable
	 * format, via {@link ListBeanAddException#getObjection()}
	 */
	public void filterElement(E element, ObservableList<E> list, int index) throws ListBeanAddException;
	
}
