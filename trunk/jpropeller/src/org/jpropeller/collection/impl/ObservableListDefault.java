/*
 *  $Id: MutablePropBean.java,v 1.1 2008/03/24 11:19:49 shingoki Exp $
 *
 *  Copyright (c) 2008 shingoki
 *
 *  This file is part of jpropeller, see http://jpropeller.sourceforge.net
 *
 *    jpropeller is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    jpropeller is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with jpropeller; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.jpropeller.collection.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Callable;

import org.jpropeller.collection.ListChange;
import org.jpropeller.collection.ObservableList;
import org.jpropeller.map.ExtendedPropMap;
import org.jpropeller.map.PropMap;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.event.ListPropEvent;
import org.jpropeller.properties.event.PropEvent;
import org.jpropeller.properties.event.PropInternalListener;
import org.jpropeller.properties.event.impl.ListPropEventDefault;
import org.jpropeller.system.Props;

/**
 * A ListBeanShell wraps an underlying List implementation, delegating actual storage
 * of elements to this core list, and adding tracking of the elements so that events
 * are generated as required for ListBean compliance.
 * 
 * @author shingoki
 *
 * @param <E>
 * 		The type of element in the list
 */
public class ObservableListDefault<E> implements ObservableList<E>, PropInternalListener {

	//Reference counter for elements in list
	ContentsTracking<E> tracking;
	
	//Standard code block for a bean
	ExtendedPropMap propMap = Props.getPropSystem().createExtendedPropMap(this);
	@Override
	public PropMap props() {
		return propMap;
	}

	//Enable flag for extra checks on list integrity at runtime
	private boolean extraChecks = true;
	
	//Set up Props
	private ModificationsProp modificationsProp = new ModificationsProp(this);

	//The List we delegate to for actual storage, etc.
	private List<E> core;
	
	public Prop<Long> modifications() { return modificationsProp; };
	
	/**
	 * Create a new {@link ObservableListDefault} based on a new {@link ArrayList}
	 */
	public ObservableListDefault() {
		this(new ArrayList<E>());
	}

	/**
	 * Create a new ListBeanShell based on a new ArrayList
	 * @param capacity
	 * 		The initial capacity of the ArrayList
	 */
	public ObservableListDefault(int capacity) {
		this(new ArrayList<E>(capacity));
	}

	/**
	 * Create a new ListBeanShell based on a given core
	 * list. The core List provides the actual storage and
	 * implementation of List methods - this shell just
	 * intercepts method calls to keep track of the List
	 * contents to implement ListBean, for example 
	 * by firing the proper events on element changes, etc.
	 * 
	 * NOTE: you must NOT modify the core list after using it
	 * to create a ListBeanShell, otherwise you will stop the
	 * ListBeanShell functioning as a compliant ListBean. It is
	 * safest not to retain a reference to the core list at all,
	 * e.g.
	 * <code>ListBean<String> listBean = new ListBeanShell(new LinkedList<String>());</code>
	 * 
	 * @param core
	 * 		The list implementation this ListBean will delegate to.
	 * 		If this is a null, an empty {@link ArrayList} will be
	 * used instead	
	 */
	public ObservableListDefault(List<E> core) {
		super();
		
		//If core is null, use an empty ArrayList
		if (core == null) core = new ArrayList<E>();
		
		propMap.add(modificationsProp);
		this.core = core;
		
		tracking = new ContentsTracking<E>(this);
		
		//Set up initial tracking of contents of provided list
		retrackAll();
	}

	
	@Override
	public <T> void propInternalChanged(PropEvent<T> event) {

		//We respond to property changes by firing on a deep change event
		
		//Optionally check we actually contain the prop we received the event from
		if (extraChecks) {
			if (!tracking.getReferenceCounts().keySet().contains(event.getProp().getBean())) {
				throw new ObservableCollectionRuntimeException("Received a PropEvent from a bean not in the reference set. Prop: '" + event.getProp() + "'");
			}
		}
		
		//TODO we could try to find the element in the 
		//list and fire a change with a defined range at this point
		//Any element in list may have changed, but size of list has not changed
		ListChange listChange = ListChangeDefault.newEntireListAlteration(this);

		//Any propChange we receive is due to list contents changing
		//in a deep way, so increment the modifications prop and fire a deep list change
		ListPropEvent<Long> listEvent = new ListPropEventDefault<Long>(modificationsProp, event, listChange);
		modificationsProp.increment(listEvent);
	}
	
	/**
	 * Start tracking all elements in the list again. 
	 * clearAllTracking() is called, then all elements
	 * are passed to startTrackingElement(e);
	 */
	public void retrackAll() {
		//Stop tracking all elements
		tracking.clearAllTracking();
		
		//start tracking all elements in the collection
		for (E e : core) {
			tracking.startTrackingElement(e);
		}
	}
	
	/**
	 * Perform a change to the core list in a way that preserves proper tracking,
	 * and then fires an event for a complete change to the list
	 * @param action
	 * 		The action to perform to the list - may manipulate the core list in
	 * any way, but should not do anything else.
	 * 		The action must NOT throw a non-runtime exception - if it does, a 
	 * ListBeanRunTimeException will be thrown with the exception as its cause.
	 * List methods do not throw non-runtime exceptions, so the action should not
	 * either 
	 * 		Runtime exceptions from the action will just be thrown from this method,
	 * but only after tracking has been set up, and a prop event fired for a 
	 * complete list change
	 * @param oldSize
	 * 		The size of the list before the change (that is, at the time that 
	 * this method is called)
	 * @return
	 * 		The return value of the action
	 * @throws ObservableCollectionRuntimeException
	 * 		If the action throws a non-runtime exception
	 */
	private boolean trackAroundListChange(Callable<Boolean> action) {
		
		int oldSize = size();
		
		//To start with, clear all references, and stop listening to all contents.
		//The list action is assumed to be a major enough operation that we don't 
		//try to track it in detail
		tracking.clearAllTracking();
		
		//Do the actual list action
		try {
			try {
				return action.call();
			} catch (Exception e) {
				//We do not expect a non-runtime exception from the action, this is an error
				throw new ObservableCollectionRuntimeException("Unexpected exception from a list action", e);
			}
			
		//Always reinstate tracking and fire prop change after the change - it may complete, or it
		//may throw an exception after adding some elements 
		} finally {
			
			//Reinstate tracking on all elements
			retrackAll();
			
			//Increment the modification count, with a list change
			//showing we changed everything
			ListChange listChange = ListChangeDefault.newCompleteChange(this, oldSize);
			modificationsProp.incrementFromListChange(listChange);
		}
		
	}
	
	//#####################################################################
	//
	//	The following methods are delegated to the core list, but must be
	//	handled carefully to preserve ListBean compliance, event handling,
	//	etc.
	//
	//#####################################################################
	
	public boolean add(E e) {
		
		//Try to add to core - if we get a runtime exception the 
		//element is not added, so nothing to do
		boolean success = core.add(e);
		
		//If we didn't actually add the element, nothing to do
		if (!success) return false;

		tracking.startTrackingElement(e);
		
		//Increment the modification count, with a list change
		//showing we added an element to the end of the list
		ListChange listChange = ListChangeDefault.newAddChange(this);
		modificationsProp.incrementFromListChange(listChange);
		
		//Success
		return true;
	}
	
	public void add(int index, E e) {
		
		//Try to add to core - if we get a runtime exception the 
		//element is not added, so nothing to do
		core.add(index, e);
		
		tracking.startTrackingElement(e);
		
		//Increment the modification count, with a list change
		//showing we inserted an element
		ListChange listChange = ListChangeDefault.newAddChange(this, index);
		modificationsProp.incrementFromListChange(listChange);
		
	}

	public boolean addAll(final Collection<? extends E> c) {

		//TODO we could fire a more succinct change, since we know
		//that the change is just adding however many new elements
		//appear at the end of the list
		//Make a callable that will do the addAll
		Callable<Boolean> action = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return core.addAll(c);
			}
		};
		
		//Perform the action with tracking
		return trackAroundListChange(action);
		
	}
	
	public boolean addAll(final int index, final Collection<? extends E> c) {

		//TODO we could fire a more succinct change, since we know
		//that the change is just adding however many new elements
		//appear to the insertion point
		//Make a callable that will do the addAll
		Callable<Boolean> action = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return core.addAll(index, c);
			}
		};
		
		//Perform the action with tracking
		return trackAroundListChange(action);
		
	}
	
	public void clear() {
		
		//Make a callable that will do the clear
		Callable<Boolean> action = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				core.clear();
				return true;
			}
		};

		//TODO we should throw a CLEAR instead of a complete change 
		//(as thrown by trackAroundListChange) after this, but it 
		//doesn't make too much difference
		
		//Perform the action with tracking
		trackAroundListChange(action);
	}

	@Override
	public void replace(final Iterable<E> newContents) {
		//Make a callable that will do the replace
		Callable<Boolean> action = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				core.clear();
				for (E e: newContents) {
					core.add(e);
				}
				return true;
			}
		};

		//Perform the action with tracking
		trackAroundListChange(action);
	}

	
	public E remove(int index) {

		//Try to remove from core - if we get a runtime exception the 
		//element is not removed, so nothing to do
		E removed = core.remove(index);
		
		tracking.stopTrackingElement(removed);
		
		//Increment the modification count, with a list change
		//showing the removal
		ListChange listChange = ListChangeDefault.newRemoveChange(this, index);
		modificationsProp.incrementFromListChange(listChange);
		
		return removed;
	}

	public boolean remove(Object o) {
		
		int oldSize = core.size();
		
		//Try to remove from core - if we get a runtime exception the 
		//element is not removed, so nothing to do
		boolean success = core.remove(o);
		
		//If we didn't actually remove the element, nothing to do
		if (!success) return false;

		tracking.stopTrackingElement(o);
		
		//Increment the modification count, with a list change
		//showing a complete list change
		ListChange listChange = ListChangeDefault.newCompleteChange(this, oldSize);
		modificationsProp.incrementFromListChange(listChange);
		
		//Success
		return true;
	}

	public boolean removeAll(final Collection<?> c) {
		//Make a callable that will do the removeAll
		Callable<Boolean> action = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return core.removeAll(c);
			}
		};
		
		//Perform the action with tracking
		return trackAroundListChange(action);
	}

	public boolean retainAll(final Collection<?> c) {
		//Make a callable that will do the retainAll
		Callable<Boolean> action = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return core.retainAll(c);
			}
		};
		
		//Perform the action with tracking
		return trackAroundListChange(action);
	}

	public E set(int index, E element) {
		
		//Try to set in core - if we get a runtime exception the 
		//element is not set, so nothing to do
		E oldValue = core.set(index, element);
		
		tracking.stopTrackingElement(oldValue);

		tracking.startTrackingElement(element);

		//Increment the modification count, with a list change
		//showing the set
		ListChange listChange = ListChangeDefault.newSingleElementAlteration(this, index);
		modificationsProp.incrementFromListChange(listChange);
		
		return oldValue;
	}

	//#####################################################################
	//
	//	The following methods are complex. They need to be delegated to
	//	the core list via wrapper objects to preserve ListBean compliance, 
	//	event handling, etc.
	//
	//#####################################################################

	
	public List<E> subList(int fromIndex, int toIndex) {
		//TODO The sublist can be made modifiable by:
		//	Implementing a wrapper sublist very similar to that in AbstractList, which passes
		//	all operations through to this underlying ListBean. This means that when the subList
		//	is used to alter the list, it is done by calling this underlying list bean, which means
		//	that events and tracking are still correct for this underlying list bean. We can call this
		//	a SublistShell
		//  WRAPPING this sublistshell in a new ListBeanShell - this may seem bizarre, but it
		//	is necessary so that the sublist will report deep changes, and will also report list
		//	changes since they will be reported by the sublist listbeanshell before the sublistshell
		//  passes them through to the main listbean, which will also fire its own events. This is a
		// 	lot of events, but is still necessary.
		//  The only flaw with this is that the underlying listbean should really report changes
		//  made via the sublist as consequent changes, otherwise both list and sublist will report
		//  user changes.
		//  All in all, it seems simpler just to return this unmodifiable sublist for now, since
		//  use of a sublist to edit a list is relatively rare AFAIK
		
		//  Get the sublist from core list, but render it unmodifiable before we use it. This avoids
		//  the sublist being used to make undetected changes to this list.
		return Collections.unmodifiableList(core.subList(fromIndex, toIndex));
	}
	
	public Iterator<E> iterator() {
		return new IteratorShell<E>(core.iterator());
	}

	public ListIterator<E> listIterator() {
		return new ListIteratorShell<E>(core.listIterator());
	}

	public ListIterator<E> listIterator(int index) {
		return new ListIteratorShell<E>(core.listIterator(index));
	}
	
	//#####################################################################
	//
	//	The following inner classes wrap iterators
	//
	//#####################################################################
	
	//TODO make the remove method more efficient, by somehow tracking the
	//index so we can fire smaller changes
	/**
	 *	Wraps an iterator, and makes main class fire property change (complete list
	 *	change) when remove is used.
	 */
	private class IteratorShell<T> implements Iterator<T>{
		
		//The wrapped iterator
		Iterator<T> it;
		
		/**
		 * Make a wrapper 
		 * @param it The iterator to wrap
		 */
		public IteratorShell(Iterator<T> it) {
			this.it = it;
		}

		//Method must ensure listbean compliance
		public void remove() {
			//Make a callable that will do the remove
			Callable<Boolean> action = new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					it.remove();
					return true;
				}
			};
			
			//Perform the action with tracking
			trackAroundListChange(action);
		}
		
		//Methods delegated directly to the wrapped iterator
		public boolean equals(Object obj) {
			return it.equals(obj);
		}
		public int hashCode() {
			return it.hashCode();
		}
		public boolean hasNext() {
			return it.hasNext();
		}
		public T next() {
			return it.next();
		}
		public String toString() {
			return it.toString();
		}
	}
	
	//TODO make the add/remove/set methods more efficient, by somehow tracking the
	//index so we can fire smaller changes
	/**
	 *	Wraps an iterator, and makes main class fire property change (complete list
	 *	change) when remove is used.
	 */
	private class ListIteratorShell<T> implements ListIterator<T> {
		ListIterator<T> it;

		/**
		 * Make a wrapper 
		 * @param it The iterator to wrap
		 */
		public ListIteratorShell(ListIterator<T> it) {
			super();
			this.it = it;
		}
		
		
		//Methods must ensure listbean compliance

		public void add(final T e) {
			//Make a callable that will do the remove
			Callable<Boolean> action = new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					it.add(e);
					return true;
				}
			};
			
			//Perform the action with tracking
			trackAroundListChange(action);
		}

		public void remove() {
			//Make a callable that will do the remove
			Callable<Boolean> action = new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					it.remove();
					return true;
				}
			};
			
			//Perform the action with tracking
			trackAroundListChange(action);
		}

		public void set(final T e) {
			//Make a callable that will do the remove
			Callable<Boolean> action = new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					it.set(e);
					return true;
				}
			};
			
			//Perform the action with tracking
			trackAroundListChange(action);
		}

		
		
		//Methods delegated directly to the wrapped iterator
		public boolean hasNext() {
			return it.hasNext();
		}
		public boolean hasPrevious() {
			return it.hasPrevious();
		}
		public T next() {
			return it.next();
		}
		public int nextIndex() {
			return it.nextIndex();
		}
		public T previous() {
			return it.previous();
		}
		public int previousIndex() {
			return it.previousIndex();
		}
		
	}
	//#####################################################################
	//
	//	The following methods are just passed directly to the core list, 
	//	since they have no interactions with the events/bean system
	//
	//#####################################################################
	
	public boolean contains(Object o) {
		return core.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return core.containsAll(c);
	}

	public boolean equals(Object o) {
		return core.equals(o);
	}

	public E get(int index) {
		return core.get(index);
	}

	public int hashCode() {
		return core.hashCode();
	}

	public int indexOf(Object o) {
		return core.indexOf(o);
	}

	public boolean isEmpty() {
		return core.isEmpty();
	}
	
	public int lastIndexOf(Object o) {
		return core.lastIndexOf(o);
	}

	public int size() {
		return core.size();
	}

	public Object[] toArray() {
		return core.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return core.toArray(a);
	}

	@Override
	public String toString() {
		return "Observable List of " + size() + " items";
	}
	
}
