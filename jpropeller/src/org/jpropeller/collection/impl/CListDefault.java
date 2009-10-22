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
import java.util.Map;
import java.util.concurrent.Callable;

import org.jpropeller.collection.CList;
import org.jpropeller.collection.ListDelta;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.ChangeableFeatures;
import org.jpropeller.properties.change.impl.ChangeableFeaturesDefault;
import org.jpropeller.properties.change.impl.InternalChangeImplementation;
import org.jpropeller.properties.change.impl.ListChangeDefault;
import org.jpropeller.system.Props;

/**
 * An {@link CListDefault} wraps an underlying {@link List} 
 * implementation, delegating actual storage of elements to this 
 * core {@link List}, and adding tracking of the elements so that events
 * are generated as required for {@link CList} compliance.
 * 
 * @param <E>		The type of element in the list
 */
public class CListDefault<E> implements CList<E> {

	//Reference counter for elements in list
	private final ContentsTracking<E> tracking;
	
	//Standard code block for a bean
	private final ChangeableFeatures features;
	
	@Override
	public ChangeableFeatures features() {
		return features;
	}

	//The List we delegate to for actual storage, etc.
	private final List<E> core;	
	
	/**
	 * Create a new {@link CListDefault} based on a new {@link ArrayList}
	 */
	public CListDefault() {
		this(new ArrayList<E>());
	}

	/**
	 * Create a new ListBeanShell based on a new ArrayList
	 * @param capacity
	 * 		The initial capacity of the ArrayList
	 */
	public CListDefault(int capacity) {
		this(new ArrayList<E>(capacity));
	}

	/**
	 * Create a new {@link CListDefault} based on a given core
	 * {@link List}. The core {@link List} provides the actual storage and
	 * implementation of {@link List} methods - this wrapper just
	 * intercepts method calls to keep track of the {@link List}
	 * contents to implement {@link CList}, for example 
	 * by propagating changes when the list is altered, etc.
	 * 
	 * NOTE: you must NOT modify the core {@link List} after using it
	 * to create an {@link CListDefault}, otherwise you will stop the
	 * {@link CListDefault} functioning as a compliant {@link CList}.
	 * It is safest not to retain a reference to the core {@link List} at all,
	 * e.g.
	 * <code>
	 * 		ObservableList<String> observableList = 
	 * 			new ObservableListDefault(new LinkedList<String>());
	 * </code>
	 * 
	 * @param core	This {@link CListDefault} will delegate to 
	 * 				the specified {@link List}. 
	 * 				If this is a null, an empty {@link ArrayList} 
	 * 				will be used instead	
	 */
	public CListDefault(List<E> core) {
		super();
		
		//If core is null, use an empty ArrayList
		if (core == null) core = new ArrayList<E>();
		
		this.core = core;
		
		features = new ChangeableFeaturesDefault(new InternalChangeImplementation() {
			@Override
			public Change internalChange(Changeable changed, Change change,
					List<Changeable> initial, Map<Changeable, Change> changes) {
				return handleInternalChange(changed, change, initial, changes);
			}
		}, this);
		
		//This will help ensure we are always listening to our own contents
		tracking = new ContentsTracking<E>(this);
		
		//Set up initial tracking of contents of provided list
		retrackAll();
	}

	/**
	 * Make a new {@link CListDefault}, based on an unmodifiable
	 * {@link ArrayList} copy of the provided contents.
	 * @param <T>		The type of list data
	 * @param contents	The list contents
	 * @return			A new unmodifiable {@link CList}
	 */
	public static <T> CListDefault<T> create(Collection<T> contents) {
		return new CListDefault<T>(Collections.unmodifiableList(new ArrayList<T>(contents)));
	}
	
	private Change handleInternalChange(Changeable changed, Change change,
			List<Changeable> initial, Map<Changeable, Change> changes) {

		//TODO: we could check here whether we contain at least one of the changed
		//beans - if not this indicates a tracking error
		
		//TODO we could try to find the element in the 
		//list and fire a change with a defined range at this point
		//Any element in list may have changed, but size of list has not changed
		ListDelta listChange = ListDeltaDefault.newEntireListAlteration(this);

		//Any propChange we receive is due to list contents changing
		//in a deep way, so return a deep list change
		return new ListChangeDefault(
				false,	//Never an initial change - the change is caused by one of the beans we contain changing 
				true, 	//Always the same instances - the instances this list contains can only be changed
						//by the methods of the List itself.
				listChange);
	}
	
	/**
	 * Start tracking all elements in the list again. 
	 * clearAllTracking() is called, then all elements
	 * are passed to startTrackingElement(e);
	 */
	private void retrackAll() {
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
	 * @throws CCollectionRuntimeException
	 * 		If the action throws a non-runtime exception
	 */
	private boolean trackAroundListChange(Callable<Boolean> action) {
		int oldSize = size();
		
		//First prepare for change
		Props.getPropSystem().getChangeSystem().prepareChange(this);
		
		//Now make sure we will retrack all contents and commit/conclude the change, no matter what
		//happens - even if something causes an exception, including a runtime exception
		//in the action (which may happen after it makes some changes)
		try {
			
			//To start with, clear all references, and stop listening to all contents.
			//The list action is assumed to be a major enough operation that we don't 
			//try to track it in detail
			tracking.clearAllTracking();
		
			//Try the action - if it fails with a runtime exception, repackage the exception
			try {
				return action.call();
			} catch (Exception e) {
				throw new CCollectionRuntimeException("Unexpected exception from a list action", e);
			}
		} finally {
			
			//Reinstate tracking on all elements
			retrackAll();
			
			//Start a list change
			//showing we changed everything
			ListDelta listChange = ListDeltaDefault.newCompleteChange(this, oldSize);
			Props.getPropSystem().getChangeSystem().propagateChange(this, new ListChangeDefault(
					true,	//Initial change 
					false, 	//Different instances
					listChange));
			
			//Always conclude the change
			Props.getPropSystem().getChangeSystem().concludeChange(this);
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
		
		//Prepare for change
		Props.getPropSystem().getChangeSystem().prepareChange(this);
		
		//We must now conclude the change no matter what happens
		try {
			
			//Try to add to core - if we get a runtime exception the 
			//element is not added, so nothing to do
			boolean success = core.add(e);
			
			//If we didn't actually add the element, nothing to do
			if (!success) return false;
	
			tracking.startTrackingElement(e);
			
			//Start a list change
			//showing we added an element to the end of the list
			ListDelta listChange = ListDeltaDefault.newAddChange(this);
			Props.getPropSystem().getChangeSystem().propagateChange(this, new ListChangeDefault(
					true,	//Initial change 
					false, 	//Different instances
					listChange));
			
			//Success
			return true;
			
		//Always conclude
		} finally {
			Props.getPropSystem().getChangeSystem().concludeChange(this);
		}
	}
	
	public void add(int index, E e) {
		
		Props.getPropSystem().getChangeSystem().prepareChange(this);
		
		try {
			//Try to add to core - if we get a runtime exception the 
			//element is not added, so nothing to do
			core.add(index, e);
			
			tracking.startTrackingElement(e);
			
			//Start a list change
			//showing we inserted an element
			ListDelta listChange = ListDeltaDefault.newAddChange(this, index);
			Props.getPropSystem().getChangeSystem().propagateChange(this, new ListChangeDefault(
					true,	//Initial change 
					false, 	//Different instances
					listChange));
			
		//Make sure we always conclude change
		} finally {
			Props.getPropSystem().getChangeSystem().concludeChange(this);
		}
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

		Props.getPropSystem().getChangeSystem().prepareChange(this);
		
		try {
			//Try to remove from core - if we get a runtime exception the 
			//element is not removed, so nothing to do
			E removed = core.remove(index);
			
			tracking.stopTrackingElement(removed);
			
			//Start a list change
			//showing the removal
			ListDelta listChange = ListDeltaDefault.newRemoveChange(this, index);
			Props.getPropSystem().getChangeSystem().propagateChange(this, new ListChangeDefault(
					true,	//Initial change 
					false, 	//Different instances
					listChange));
			
			return removed;
			
		//Always conclude the change
		} finally {
			Props.getPropSystem().getChangeSystem().concludeChange(this);
		}
	}

	public boolean remove(Object o) {
		
		Props.getPropSystem().getChangeSystem().prepareChange(this);

		int oldSize = core.size();
		
		try {
			//Try to remove from core - if we get a runtime exception the 
			//element is not removed, so nothing to do
			boolean success = core.remove(o);
			
			//If we didn't actually remove the element, nothing to do
			if (!success) return false;
	
			tracking.stopTrackingElement(o);
			
			//Start a list change
			//showing a complete list change
			ListDelta listChange = ListDeltaDefault.newCompleteChange(this, oldSize);
			Props.getPropSystem().getChangeSystem().propagateChange(this, new ListChangeDefault(
					true,	//Initial change 
					false, 	//Different instances
					listChange));
	
			//Success
			return true;
			
		//Always conclude the change
		} finally {
			Props.getPropSystem().getChangeSystem().concludeChange(this);
		}
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
		
		Props.getPropSystem().getChangeSystem().prepareChange(this);
		
		try {
			//Try to set in core - if we get a runtime exception the 
			//element is not set, so nothing to do
			E oldValue = core.set(index, element);
			
			tracking.stopTrackingElement(oldValue);
	
			tracking.startTrackingElement(element);
	
			//Start a list change
			//showing the set
			ListDelta listChange = ListDeltaDefault.newSingleElementAlteration(this, index);
			Props.getPropSystem().getChangeSystem().propagateChange(this, new ListChangeDefault(
					true,	//Initial change 
					false, 	//Different instances
					listChange));
			
			return oldValue;
			
		//Always conclude change
		} finally {
			Props.getPropSystem().getChangeSystem().concludeChange(this);
		}
		
	}

	//#####################################################################
	//
	//	The following methods are complex. They need to be delegated to
	//	the core list via wrapper objects to preserve ListBean compliance, 
	//	event handling, etc.
	//
	//#####################################################################

	
	public List<E> subList(int fromIndex, int toIndex) {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		try {
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
			
			//  Get the sublist from core list, but wrap it in a LockingList shell before
			//  returning. This renders it safe, by performing required locking and tracking
			//  Actually currently renders list unmodifiable as well to avoid need for tracking.
			return new ListShell(core.subList(fromIndex, toIndex));
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
		}
	}
	
	public Iterator<E> iterator() {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		try {
			return new IteratorShell<E>(core.iterator());
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
		}
	}

	public ListIterator<E> listIterator() {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		try {
			return new ListIteratorShell<E>(core.listIterator());
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
		}
	}

	public ListIterator<E> listIterator(int index) {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		try {
			return new ListIteratorShell<E>(core.listIterator(index));
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
		}
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
	 *	change) when remove is used, and adds appropriate locking.
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
			Props.getPropSystem().getChangeSystem().prepareRead(CListDefault.this);
			try {
				return it.equals(obj);
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CListDefault.this);
			}
		}
		public int hashCode() {
			Props.getPropSystem().getChangeSystem().prepareRead(CListDefault.this);
			try {
				return it.hashCode();
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CListDefault.this);
			}
		}
		public boolean hasNext() {
			Props.getPropSystem().getChangeSystem().prepareRead(CListDefault.this);
			try {
				return it.hasNext();
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CListDefault.this);
			}
		}
		public T next() {
			Props.getPropSystem().getChangeSystem().prepareRead(CListDefault.this);
			try {
				return it.next();
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CListDefault.this);
			}
		}
		public String toString() {
			Props.getPropSystem().getChangeSystem().prepareRead(CListDefault.this);
			try {
				return it.toString();
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CListDefault.this);
			}
		}
	}
	
	//TODO make the add/remove/set methods more efficient, by somehow tracking the
	//index so we can fire smaller changes
	/**
	 *	Wraps an iterator, and makes main class fire property change (complete list
	 *	change) when remove is used, and adds appropriate locking.
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
			Props.getPropSystem().getChangeSystem().prepareRead(CListDefault.this);
			try {
				return it.hasNext();
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CListDefault.this);
			}
		}
		public boolean hasPrevious() {
			Props.getPropSystem().getChangeSystem().prepareRead(CListDefault.this);
			try {
				return it.hasPrevious();
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CListDefault.this);
			}
		}
		public T next() {
			Props.getPropSystem().getChangeSystem().prepareRead(CListDefault.this);
			try {
				return it.next();
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CListDefault.this);
			}
		}
		public int nextIndex() {
			Props.getPropSystem().getChangeSystem().prepareRead(CListDefault.this);
			try {
				return it.nextIndex();
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CListDefault.this);
			}
		}
		public T previous() {
			Props.getPropSystem().getChangeSystem().prepareRead(CListDefault.this);
			try {
				return it.previous();
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CListDefault.this);
			}
		}
		public int previousIndex() {
			Props.getPropSystem().getChangeSystem().prepareRead(CListDefault.this);
			try {
				return it.previousIndex();
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CListDefault.this);
			}
		}
		
	}
	
	/**
	 * Wraps a list to make it unmodifiable (so tracking is preserved),
	 * and also performs appropriate locking on reading.
	 * Passes on required properties to sublists and iterators.
	 */
	private class ListShell implements List<E> {
		private final List<E> coreList;
		private ListShell(List<E> lockedList) {
			this.coreList = Collections.unmodifiableList(lockedList);
		}
		
		//These methods are complex - we need to make the returned iterators/sublists
		//also perform locking
		
		//Iterators use the same shell as the parent list
		public Iterator<E> iterator() {
			Props.getPropSystem().getChangeSystem().prepareRead(CListDefault.this);
			try {
				return new IteratorShell<E>(coreList.iterator());
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CListDefault.this);
			}
		}
		public ListIterator<E> listIterator() {
			Props.getPropSystem().getChangeSystem().prepareRead(CListDefault.this);
			try {
				return new ListIteratorShell<E>(coreList.listIterator());
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CListDefault.this);
			}
		}
		public ListIterator<E> listIterator(int index) {
			Props.getPropSystem().getChangeSystem().prepareRead(CListDefault.this);
			try {
				return new ListIteratorShell<E>(coreList.listIterator(index));
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CListDefault.this);
			}
		}
		
		//Make another locking list around sublist, so it gives the same behaviour.
		//Note that the wrapping isn't recursive - there should just be one LockedList
		//wrapper around each list, even when getting subList().subList().subList() etc.
		public List<E> subList(int fromIndex, int toIndex) {
			Props.getPropSystem().getChangeSystem().prepareRead(CListDefault.this);
			try {
				return new ListShell(coreList.subList(fromIndex, toIndex));
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CListDefault.this);
			}
		}
		
		
		//These methods are all simple - just lock around them
		public boolean contains(Object o) {
			Props.getPropSystem().getChangeSystem().prepareRead(CListDefault.this);
			try {
				return coreList.contains(o);
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CListDefault.this);
			}
		}
		public boolean containsAll(Collection<?> c) {
			Props.getPropSystem().getChangeSystem().prepareRead(CListDefault.this);
			try {
				return coreList.containsAll(c);
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CListDefault.this);
			}
		}
		public boolean equals(Object o) {
			Props.getPropSystem().getChangeSystem().prepareRead(CListDefault.this);
			try {
				return coreList.equals(o);
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CListDefault.this);
			}
		}
		public E get(int index) {
			Props.getPropSystem().getChangeSystem().prepareRead(CListDefault.this);
			try {
				return coreList.get(index);
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CListDefault.this);
			}
		}
		public int hashCode() {
			Props.getPropSystem().getChangeSystem().prepareRead(CListDefault.this);
			try {
				return coreList.hashCode();
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CListDefault.this);
			}
		}
		public int indexOf(Object o) {
			Props.getPropSystem().getChangeSystem().prepareRead(CListDefault.this);
			try {
				return coreList.indexOf(o);
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CListDefault.this);
			}
		}
		public boolean isEmpty() {
			Props.getPropSystem().getChangeSystem().prepareRead(CListDefault.this);
			try {
				return coreList.isEmpty();
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CListDefault.this);
			}
		}
		public int lastIndexOf(Object o) {
			Props.getPropSystem().getChangeSystem().prepareRead(CListDefault.this);
			try {
				return coreList.lastIndexOf(o);
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CListDefault.this);
			}
		}
		public int size() {
			Props.getPropSystem().getChangeSystem().prepareRead(CListDefault.this);
			try {
				return coreList.size();
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CListDefault.this);
			}
		}
		public Object[] toArray() {
			Props.getPropSystem().getChangeSystem().prepareRead(CListDefault.this);
			try {
				return coreList.toArray();
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CListDefault.this);
			}
		}
		public <T> T[] toArray(T[] a) {
			Props.getPropSystem().getChangeSystem().prepareRead(CListDefault.this);
			try {
				return coreList.toArray(a);
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CListDefault.this);
			}
		}
		
		//Methods that modify the list will all fail due to unmodifiable view
		public boolean add(E e) {
			return coreList.add(e);
		}
		public void add(int index, E element) {
			coreList.add(index, element);
		}
		public boolean addAll(Collection<? extends E> c) {
			return coreList.addAll(c);
		}
		public boolean addAll(int index, Collection<? extends E> c) {
			return coreList.addAll(index, c);
		}
		public void clear() {
			coreList.clear();
		}
		public E remove(int index) {
			return coreList.remove(index);
		}
		public boolean remove(Object o) {
			return coreList.remove(o);
		}
		public boolean removeAll(Collection<?> c) {
			return coreList.removeAll(c);
		}
		public boolean retainAll(Collection<?> c) {
			return coreList.retainAll(c);
		}
		public E set(int index, E element) {
			return coreList.set(index, element);
		}
		
	}
	
	//#####################################################################
	//
	//	The following methods are just passed directly to the core list, 
	//	since they have no interactions with the events/bean system
	//
	//#####################################################################
	
	public boolean contains(Object o) {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		try {
			return core.contains(o);
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
		}
	}

	public boolean containsAll(Collection<?> c) {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		try {
			return core.containsAll(c);
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
		}
	}

	public boolean equals(Object o) {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		try {
			return core.equals(o);
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
		}
	}

	public E get(int index) {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		try {
			return core.get(index);
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
		}
	}

	public int hashCode() {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		try {
			return core.hashCode();
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
		}
	}

	public int indexOf(Object o) {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		try {
			return core.indexOf(o);
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
		}
	}

	public boolean isEmpty() {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		try {
			return core.isEmpty();
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
		}
	}
	
	public int lastIndexOf(Object o) {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		try {
			return core.lastIndexOf(o);
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
		}
	}

	public int size() {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		try {
			return core.size();
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
		}
	}

	public Object[] toArray() {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		try {
			return core.toArray();
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
		}
	}

	public <T> T[] toArray(T[] a) {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		try {
			return core.toArray(a);
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
		}
	}

	@Override
	public String toString() {
		return "Observable List of " + size() + " items";
	}
	
}