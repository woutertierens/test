package org.jpropeller.properties.list.selection;

import org.jpropeller.properties.Prop;

/**
 * A {@link ListAndSelectionReference} that also has a property
 * giving the currently selected element of the list (or null if
 * there is no valid selection)
 * @param <T>
 * 		The type of element in the list 
 */
public interface ListAndSelectionAndValueReference<T> extends
		ListAndSelectionReference<T> {

	/**
	 * The selected element of the list - that is, the element at
	 * the index from {@link #selection()}, in the list from
	 * {@link #value()}
	 * @return
	 * 		The selected element
	 */
	public Prop<T> selectedValue();
	
}
