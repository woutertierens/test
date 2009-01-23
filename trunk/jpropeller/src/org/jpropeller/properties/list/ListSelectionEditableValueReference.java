package org.jpropeller.properties.list;

import org.jpropeller.collection.ObservableList;
import org.jpropeller.properties.EditableProp;

/**
 * Identical to {@link ListSelectionValueReference}, but has an {@link EditableProp}
 * for the {@link #selectedValue()}. This will only work with an {@link ObservableList} that
 * supports the {@link ObservableList#set(int, Object)} operation.
 * @param <T>
 * 		The type of element in the list 
 */
public interface ListSelectionEditableValueReference<T> extends
		ListSelectionValueReference<T> {

	public EditableProp<T> selectedValue();
	
}
