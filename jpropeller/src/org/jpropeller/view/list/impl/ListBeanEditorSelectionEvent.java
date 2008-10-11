package org.jpropeller.view.list.impl;

import java.util.EventObject;

/**
 * Event fired when a ListBeanEditor selection changes.
 * Note this is NOT a deep notification change, it is only
 * triggered by the instance selected in the list being changed,
 * although this can occur by the editor model being changed, 
 * the list component selection index being changed, or
 * an element of the list being added/removed, etc. But NO change
 * occurs if the instance selected in the list is just altered in
 * some "deep" way, that is, if one of its properties changes 
 * (or one of the properties of one of its properties changes, etc.) 
 * 
 * This is useful in combination with deep change notification, since
 * you can obviously listen to the current selection, and change the
 * instance you are listening to when a selection event occurs. In
 * addition it is useful when you are setting the currently selected
 * instance as a property of another bean - in this case, you want the
 * bean itself to notice deep changes, and you only want to change the
 * bean's property when an entire new instance is selected in the 
 * list editor, not whenever the selected instance has a deep change.
 * 
 * @author websterb
 *
 * @param <E>
 * 		The type of element in the list bean editor, also the 
 * 		parametric type of the editor
 */
public class ListBeanEditorSelectionEvent<E> extends EventObject {
	private static final long serialVersionUID = -1519211268527350392L;
	
	ListBeanEditor<E> editor;
	E newSelection;
	
	/**
	 * Create an event
	 * @param editor
	 * 		The editor whose selection has changed
	 * @param newSelection
	 * 		The newly selected element, may be null if no element is selected
	 */
	public ListBeanEditorSelectionEvent(ListBeanEditor<E> editor, E newSelection) {
		super(editor);
		this.editor = editor;
		this.newSelection = newSelection;
	}

	/**
	 * @return
	 * 		The editor whose selection has changed
	 */
	public ListBeanEditor<E> getEditor() {
		return editor;
	}

	/**
	 * @return
	 * 		The newly selected element, may be null if no element is selected
	 */
	public E getNewSelection() {
		return newSelection;
	}

}
