/*
 *	$Id: ListBeanAddFactory.java,v 1.3 2008-05-19 11:57:25 cvs Exp $
 */
package org.jpropeller.view.list.impl;

/**
 *	Interface for factories used by ListBeanEditor to provide
 *	instances to add to themselves
 * @param <E> 
 * 		The type of element in the list
 */
public interface ListBeanAddFactory<E> {

	/**
	 * Create an instance which can be added to the list in the
	 * list editor.
	 * @param editor The editor requesting the list, may be used as parent
	 * component for dialogs, etc.
	 * @return A new instance, type E, to be added to the list, or null
	 * if the process is cancelled, etc.
	 */
	public E createInstanceToAdd(ListBeanEditor<? super E> editor);
	
}
