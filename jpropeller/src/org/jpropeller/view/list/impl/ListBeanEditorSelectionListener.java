package org.jpropeller.view.list.impl;

/**
 * Listens for selection changes in (a particular parametric type of)
 * list bean editor
 * 
 * Please see the ListBeanEditorSelectionEvent class for a fuller 
 * explanation of what a selection event represents.
 * 
 * @author websterb
 *
 * @param <E>
 */
public interface ListBeanEditorSelectionListener<E> {

	/**
	 * Called when an editor selection changes
	 * This covers only shallow selection change - the user selects
	 * a different item in the list, or the selected item is deleted,
	 * or the entire list changes somehow, NOT deep changes in the
	 * selection, where the same object is selected, but one of its
	 * properties changes (or one of it's properties' properties changes, etc.).
	 * Please see {@link ListBeanEditorSelectionEvent} for more details.
	 * @param event
	 * 		Carries details of the change
	 */
	public void selectionChanged(ListBeanEditorSelectionEvent<E> event);
	
}
