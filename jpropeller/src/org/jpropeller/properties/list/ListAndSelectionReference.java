package org.jpropeller.properties.list;

import org.jpropeller.collection.ObservableList;
import org.jpropeller.properties.list.impl.ListSelectionReferenceProp;
import org.jpropeller.reference.Reference;

/**
 * A {@link Reference} to an {@link ObservableList}, which also has an additional
 * selection property. This property will track selection in the list when possible,
 * resetting to -1 when not possible (e.g. a new list is set)
 * @param <T>
 * 		The type of element in the list 
 */
public interface ListAndSelectionReference<T> extends Reference<ObservableList<T>>{

	/**
	 * The selection index in the list - can be set and got,
	 * and will move itself to track the same element in the list
	 * when possible.
	 * @return
	 * 		The selection index
	 */
	public ListSelectionReferenceProp<T> selection();

}