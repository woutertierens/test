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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.Callable;

import org.jpropeller.collection.CSet;
import org.jpropeller.collection.SetDelta;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeSystem;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.ChangeableFeatures;
import org.jpropeller.properties.change.impl.ChangeableFeaturesDefault;
import org.jpropeller.properties.change.impl.InternalChangeImplementation;
import org.jpropeller.properties.change.impl.SetChangeDefault;
import org.jpropeller.system.Props;

/**
 * An {@link CSetDefault} wraps an underlying {@link Set}
 * implementation, delegating actual storage of elements to this 
 * core {@link Set}, and adding tracking of the elements so that events
 * are generated as required for {@link CSet} compliance.
 * 
 * Any {@link Set} can be wrapped, however {@link SortedSet} behaviour
 * is NOT exposed by an {@link CSetDefault} - the set will still
 * be sorted when iterated, but it is not possible to access 
 * {@link SortedSet#subSet(Object, Object)}, etc.
 * 
 * As an implementation note, this {@link Changeable} does NOT 
 * use {@link ChangeSystem#prepareRead(Changeable)} or 
 * {@link ChangeSystem#concludeRead(Changeable)}, since it 
 * knows that it does NOT read the values of any other 
 * {@link Changeable}s, or modify its own state, when it is 
 * read. (Reading state of other {@link Changeable}s without 
 * preparing is prohibited, since it can lead to these {@link Changeable}s
 * reading inconsistent state, regenerating cache at the wrong time, etc.) 
 * The {@link Set} that is wrapped is synchronised so that it is 
 * not possible to have synchronisation problems
 * within the {@link Set} itself.
 * 
 * Finally, note that an object where {@link #equals(Object)} returns
 * false for comparison to itself (that is, where a.equals(a) is false)
 * will not work with the change notification of this {@link Set} - this
 * is probably a minor point since such an object would not work with the
 * basic behaviour of {@link Set} in any case.
 * 
 * TODO assess whether it is possible to use {@link ChangeSystem#prepareRead(Changeable)} anyway.
 * 
 * @param <E>
 * 		The type of element in the set
 */
public class CSetDefault<E> implements CSet<E> {

	//Standard code block for a bean
	ChangeableFeatures features;
	
	@Override
	public ChangeableFeatures features() {
		return features;
	}

	//The Set we delegate to for actual storage, etc.
	private Set<E> core;	
	
	/**
	 * Create a new {@link CSetDefault} based on a new {@link HashSet}
	 */
	public CSetDefault() {
		this(new HashSet<E>());
	}

	/**
	 * Create a new {@link CSetDefault} based on a new {@link HashSet}
	 * @param capacity
	 * 		The initial capacity of the {@link HashSet}
	 */
	public CSetDefault(int capacity) {
		this(new HashSet<E>(capacity));
	}

	/**
	 * Create a new {@link CSetDefault} based on a given core
	 * {@link Set}. The core {@link Set} provides the actual storage and
	 * implementation of {@link Set} methods - this wrapper just
	 * intercepts method calls to keep track of the {@link Set}
	 * contents to implement {@link CSet}, for example 
	 * by propagating changes when the {@link Set} is altered, etc.
	 * 
	 * NOTE: you must NOT modify the core {@link Set} after using it
	 * to create an {@link CSetDefault}, otherwise you will stop the
	 * {@link CSetDefault} functioning as a compliant {@link CSet}.
	 * It is safest not to retain a reference to the core {@link Set} at all,
	 * e.g.
	 * <code>ObservableSet<String> observableSet = new ObservableSetDefault(new HashSet<String>());</code>
	 * 
	 * @param core
	 * 		This {@link CSetDefault} will delegate to a synchronised wrapper
	 * of the specified {@link Set}. It is best not to specify a synchronised {@link Set},
	 * since it will just be synchronised again.
	 * 		If this is a null, an empty synchronised {@link HashSet} will be used instead	
	 */
	public CSetDefault(Set<E> core) {
		super();
		
		//If core is null, use an empty HashSet
		if (core == null) core = new HashSet<E>();
		
		this.core = Collections.synchronizedSet(core);
		
		features = new ChangeableFeaturesDefault(new InternalChangeImplementation() {
			@Override
			public Change internalChange(Changeable changed, Change change,
					List<Changeable> initial, Map<Changeable, Change> changes) {
				return handleInternalChange(changed, change, initial, changes);
			}
		}, this);
		
		//Set up initial tracking of contents of provided list
		trackAll();
	}

	private Change handleInternalChange(Changeable changed, Change change,
			List<Changeable> initial, Map<Changeable, Change> changes) {

		//TODO: we could check here whether we contain at least one of the changed
		//changeables - if not this indicates a tracking error
		
		//Any element in set may have changed, but size of set has not changed
		SetDelta setDelta = SetDeltaDefault.newEntireSetAlteration(this);

		//Any propChange we receive is due to contents changing
		//in a deep way, so return a deep change
		return new SetChangeDefault(
				false,	//Never an initial change - the change is caused by one of the changeables we contain changing 
				true, 	//Always the same instances - the instances this set contains can only be changed
						//by the methods of the Set itself.
				setDelta);
	}
	
	/**
	 * Start tracking an element
	 * @param e
	 * 		The element to listen to
	 */
	private void track(E e) {
		if (e instanceof Changeable) {
			((Changeable)e).features().addChangeableListener(this);
		}
	}

	/**
	 * Stop tracking an element
	 * @param e
	 * 		The element to stop listening to
	 */
	private void untrack(Object e) {
		if (e instanceof Changeable) {
			((Changeable)e).features().removeChangeableListener(this);
		}
	}

	/**
	 * Track all elements
	 */
	private void trackAll() {
		for (E e : core) {
			track(e);
		}
	}

	/**
	 * Untrack all elements
	 */
	private void untrackAll() {
		for (E e : core) {
			untrack(e);
		}
	}

	/**
	 * Perform a change to the core in a way that preserves proper tracking,
	 * and then fires an event for a complete change to the core
	 * @param action
	 * 		The action to perform to the core - may manipulate the core in
	 * any way, but should not do anything else.
	 * 		The action must NOT throw a non-runtime exception - if it does, a 
	 * {@link CCollectionRuntimeException} will be thrown with the 
	 * exception as its cause.
	 * Core methods do not throw non-runtime exceptions, so the action should not
	 * either 
	 * 		Runtime exceptions from the action will just be thrown from this method,
	 * but only after tracking has been set up, and a prop event fired for a 
	 * complete change
	 * @return
	 * 		The return value of the action
	 * @throws CCollectionRuntimeException
	 * 		If the action throws a non-runtime exception
	 */
	private boolean trackAroundSetChange(Callable<Boolean> action) {
		int oldSize = size();
		
		//First prepare for change
		Props.getPropSystem().getChangeSystem().prepareChange(this);
		
		//Now make sure we will retrack all contents and commit/conclude the change, no matter what
		//happens - even if something causes an exception, including a runtime exception
		//in the action (which may happen after it makes some changes)
		try {
			
			//To start with, clear all references, and stop listening to all contents.
			//The action is assumed to be a major enough operation that we don't 
			//try to track it in detail
			untrackAll();
		
			//Try the action - if it fails with a runtime exception, repackage the exception
			try {
				return action.call();
			} catch (Exception e) {
				throw new CCollectionRuntimeException("Unexpected exception from a set action", e);
			}
		} finally {
			
			//Reinstate tracking on all elements
			trackAll();
			
			//Start a set change
			//showing we changed everything
			SetDelta delta = SetDeltaDefault.newCompleteChange(this, oldSize);
			Props.getPropSystem().getChangeSystem().propagateChange(this, new SetChangeDefault(
					true,	//Initial change 
					false, 	//Different instances
					delta));
			
			//Always conclude the change
			Props.getPropSystem().getChangeSystem().concludeChange(this);
		}
		
	}
	
	//#####################################################################
	//
	//	The following methods are delegated to the core set, but must be
	//	handled carefully to preserve ObservableSet compliance, event handling,
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
	
			track(e);
			
			//Start a set change
			//showing we added an element
			SetDelta delta = SetDeltaDefault.newAdd(this, 1);
			Props.getPropSystem().getChangeSystem().propagateChange(this, new SetChangeDefault(
					true,	//Initial change 
					false, 	//Different instances
					delta));
			
			//Success
			return true;
			
		//Always conclude
		} finally {
			Props.getPropSystem().getChangeSystem().concludeChange(this);
		}
	}
	

	public boolean addAll(final Collection<? extends E> c) {

		//TODO we could fire a more succinct change, since we know
		//that the change is just adding however many new elements
		//appear at the end of the set
		//Make a callable that will do the addAll
		Callable<Boolean> action = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return core.addAll(c);
			}
		};
		
		//Perform the action with tracking
		return trackAroundSetChange(action);
		
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
		trackAroundSetChange(action);
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
		trackAroundSetChange(action);
	}

	public boolean remove(Object o) {
		
		Props.getPropSystem().getChangeSystem().prepareChange(this);
		
		try {
			//Try to remove from core - if we get a runtime exception the 
			//element is not removed, so nothing to do
			boolean success = core.remove(o);
			
			//If we didn't actually remove the element, nothing to do
			if (!success) return false;
	
			untrack(o);
			
			//Start a list change
			//showing a single removal
			SetDelta delta = SetDeltaDefault.newRemoveChange(this, 1);
			Props.getPropSystem().getChangeSystem().propagateChange(this, new SetChangeDefault(
					true,	//Initial change 
					false, 	//Different instances
					delta));
	
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
		return trackAroundSetChange(action);
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
		return trackAroundSetChange(action);
	}


	//#####################################################################
	//
	//	The following methods are complex. They need to be delegated to
	//	the core list via wrapper objects to preserve ListBean compliance, 
	//	event handling, etc.
	//
	//#####################################################################

	public Iterator<E> iterator() {
		return new IteratorShell<E>(core.iterator());
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
			trackAroundSetChange(action);
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
	
	//#####################################################################
	//
	//	The following methods are just passed directly to the core, 
	//	since they have no interactions with the events/bean system
	//
	//#####################################################################
	
	@Override
	public boolean contains(Object o) {
		return core.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return core.containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		return core.isEmpty();
	}

	@Override
	public int size() {
		return core.size();
	}

	@Override
	public Object[] toArray() {
		return core.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return core.toArray(a);
	}	
	
	@Override
	public String toString() {
		return core.toString();
	}
	
}
