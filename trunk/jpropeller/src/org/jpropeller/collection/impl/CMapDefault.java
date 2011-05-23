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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.jpropeller.collection.CList;
import org.jpropeller.collection.CMap;
import org.jpropeller.collection.MapDelta;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.ChangeableFeatures;
import org.jpropeller.properties.change.impl.ChangeableFeaturesDefault;
import org.jpropeller.properties.change.impl.InternalChangeImplementation;
import org.jpropeller.properties.change.impl.MapChangeDefault;
import org.jpropeller.system.Props;

/**
 * An {@link CMapDefault} wraps an underlying {@link Map} implementation, 
 * delegating actual storage of elements to this core map, and adding 
 * tracking of the keys and values so that events are generated as required 
 * for {@link CMap} compliance.
 * 
 * @param <K> 		The type of key in the map.
 * @param <V>		The type of element in the map.
 */
public class CMapDefault<K, V> implements CMap<K, V> {

	//private final static Set<String> observedChangeables = new HashSet<String>();
	
	//Reference counter for values in map
	private final ContentsTracking<V> tracking;
	
	//Reference counter for keys in map
	private final ContentsTracking<K> keyTracking;

	//Standard code block for a bean
	private final ChangeableFeatures features;
	
	//True to track keys as well as values
	private final boolean trackKeys;
	
	@Override
	public ChangeableFeatures features() {
		return features;
	}

	//The Map we delegate to for actual storage, etc.
	private final Map<K, V> core;
	
	/**
	 * Create a new {@link CMapDefault} based on a new {@link HashMap}
	 */
	public CMapDefault() {
		this(new HashMap<K, V>());
	}
	
	/**
	 * Create a new {@link CMapDefault} based on a new {@link HashMap}
	 * @param capacity
	 * 		The initial capacity of the {@link HashMap}
	 */
	public CMapDefault(int capacity) {
		this(new HashMap<K, V>(capacity));
	}

	/**
	 * Make a new {@link CMapDefault}, based on an unmodifiable
	 * {@link HashMap} copy of the provided contents.
	 * @param <K>		The type of key
	 * @param <V>		The type of value
	 * @param contents	The list contents
	 * @return			A new unmodifiable {@link CList}
	 */
	public static <K, V> CMapDefault<K, V> create(Map<K, V> contents) {
		return new CMapDefault<K, V>(Collections.unmodifiableMap(new HashMap<K, V>(contents)));
	}
	
	/**
	 * Create a new {@link CMapDefault} based on a given core
	 * map. The core {@link Map} provides the actual storage and
	 * implementation of {@link Map} methods - this shell just
	 * intercepts method calls to keep track of the {@link Map}
	 * contents to implement {@link CMap}, for example 
	 * by firing the proper events on element changes, etc.
	 * 
	 * NOTE: you must NOT modify the core {@link Map} after using it
	 * to create an {@link CMapDefault}, otherwise you will stop the
	 * {@link CMapDefault} functioning as a compliant {@link CMap}.
	 * It is safest not to retain a reference to the core {@link Map} at all,
	 * e.g.
	 * <code>
	 * 		ObservableMap<String> map = 
	 * 			new ObservableMapDefault(new TreeMap<String, String>());
	 * </code>
	 * 
	 * @param core	This {@link CMapDefault} will delegate to the specified 
	 * 				{@link Map}. If this is null, a new {@link HashMap} will
	 * 				be used.
	 */
	public CMapDefault(Map<K, V> core) {
		super();
		
		//If core is null, use an empty HashMap
		if (core == null) core = new HashMap<K, V>();
		
		this.core = core;

		features = new ChangeableFeaturesDefault(new InternalChangeImplementation() {
			@Override
			public Change internalChange(Changeable changed, Change change,
					List<Changeable> initial, Map<Changeable, Change> changes) {
				return handleInternalChange(changed, change, initial, changes);
			}
		}, this);

		
		tracking = new ContentsTracking<V>(this);
		keyTracking = new ContentsTracking<K>(this);

		//FIXME make optional again
		this.trackKeys = true;
		
		//Set up initial tracking of contents of provided list
		retrackAll();
	}

	private Change handleInternalChange(Changeable changed, Change change,
			List<Changeable> initial, Map<Changeable, Change> changes) {

		//TODO: we could check here whether we contain at least one of the changed
		//beans - if not this indicates a tracking error
		
		//TODO we could try to find the element in the 
		//map values and fire a change with a defined location at this point
		//Any element in map may have changed, but size of map has not changed
		MapDelta mapChange = MapDeltaDefault.newEntireMapAlteration(this);
		
		//Any propChange we receive is due to map contents changing
		//in a deep way, so return a deep map change
		return new MapChangeDefault(
				false,	//Never an initial change - the change is caused by one of the beans we contain changing 
				true, 	//Always the same instances - the instances this map contains can only be changed
						//by the methods of the Map itself.
				mapChange);		
	}

	/**
	 * Clear all key and value tracking
	 */
	private void clearAllTracking() {
		tracking.clearAllTracking();
		
		if (trackKeys) {
			keyTracking.clearAllTracking();
		}
	}
	
	/**
	 * Start tracking all elements in the list again. 
	 * clearAllTracking() is called, then all elements
	 * are passed to startTrackingElement(e);
	 */
	private void retrackAll() {
		//Stop tracking all elements
		clearAllTracking();
		
		//Track each value in the map, making sure that we start tracking
		//it once for EACH MAPPING that points to that value
		//Hence we don't iterate the value set, which contains values
		//mapped-to by multiple keys only once - we iterate the key set,
		//then follow each key to the value
		//Track keys for each time they are used as a key, by definition
		//this can only be once, since only a terrible key implementation
		//will be unequal to itself.
		for (K k : core.keySet()) {
			tracking.startTrackingElement(core.get(k));
			if (trackKeys) {
				checkKey(k);
				keyTracking.startTrackingElement(k);
			}
		}
	}


	private void checkKey(K k) {
/*		if (k instanceof Changeable) {
			synchronized (observedChangeables) {
				String className = k.getClass().toString();
				if (observedChangeables.add(className)) {
					System.out.println("NEW KEY CLASS: " + className);
				}
			}
			
		}*/
	}
	
	/**
	 * Perform a change to the core map in a way that preserves proper tracking,
	 * and then fires an event for a complete change to the map
	 * @param action
	 * 		The action to perform to the map - may manipulate the core map in
	 * any way, but should not do anything else.
	 * 		The action must NOT throw a non-runtime exception - if it does, an
	 * {@link CCollectionRuntimeException} will be thrown with the 
	 * exception as its cause.
	 * Map methods do not throw non-runtime exceptions, so the action should not
	 * either. 
	 * 		Runtime exceptions from the action will just be thrown from this method,
	 * but only after tracking has been set up, and a prop event fired for a 
	 * complete list change
	 * @param oldSize
	 * 		The size of the map before the change (that is, at the time that 
	 * this method is called)
	 * @return
	 * 		The return value of the action
	 * @throws CCollectionRuntimeException
	 * 		If the action throws a non-runtime exception
	 */
	private boolean trackAroundMapChange(Callable<Boolean> action) {
		
		int oldSize = -1;
		Props.getPropSystem().getChangeSystem().prepareChange(this);
		try {

			 oldSize = size();

			//To start with, clear all references, and stop listening to all contents.
			//The action is assumed to be a major enough operation that we don't 
			//try to track it in detail
			clearAllTracking();
			
			try {
				return action.call();
			} catch (Exception e) {
				//We do not expect a non-runtime exception from the action, this is an error
				throw new CCollectionRuntimeException("Unexpected exception from a map action", e);
			}
			
		//Always reinstate tracking and conclude change - the action may complete, or it
		//may throw an exception after adding some elements 
		} finally {
			
			//Reinstate tracking on all elements
			retrackAll();
			
			//Start a list change
			//showing we changed everything
			MapDelta mapChange = MapDeltaDefault.newCompleteChange(size(), oldSize);
			Props.getPropSystem().getChangeSystem().propagateChange(this, new MapChangeDefault(
					true,	//Initial change 
					false, 	//Different instances
					mapChange));
			
			Props.getPropSystem().getChangeSystem().concludeChange(this);
		}
		
	}

	
	//#####################################################################
	//
	//	The following methods modify the set, and require tracking of elements,
	//	etc.
	//
	//#####################################################################
	
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
		//(as thrown by trackAroundMapChange) after this, but it 
		//doesn't make too much difference
		
		//Perform the action with tracking
		trackAroundMapChange(action);
		
	}
	
	public V put(K key, V value) {
	
		Props.getPropSystem().getChangeSystem().prepareChange(this);
		
		try {
			//Do we have an existing value? This affects the "old size" of the
			//map change we will fire
			boolean existingValue = core.containsKey(key);

			//If we have an existing key, use the exact same instance
			if (existingValue) {
				key = findExactExistingKey(key);
			}
			
			//Try to put in core - if we get a runtime exception the 
			//value is not put, so nothing to do
			V oldValue = core.put(key, value);
			
			//Note that null values are ignored for tracking, so no need
			//to check whether mapping was actually present
			tracking.stopTrackingElement(oldValue);
	
			tracking.startTrackingElement(value);

			//If this is a new key, start tracking it
			if (trackKeys) {
				if (!existingValue) {
					checkKey(key);
					keyTracking.startTrackingElement(key);
				}
			}
			
			//Start a map change
			//showing the put
			MapDelta change;
			if (existingValue) {
				change = MapDeltaDefault.newSingleKeyAlteration(this, key);
			} else {
				change = MapDeltaDefault.newInsertionChange(this, key);			
			}
			Props.getPropSystem().getChangeSystem().propagateChange(this, new MapChangeDefault(
					true,	//Initial change 
					false, 	//Different instances
					change));
	
			return oldValue;
			
		//Make sure we always conclude the change
		} finally {
			Props.getPropSystem().getChangeSystem().concludeChange(this);
		}
	}
	
	public void putAll(final Map<? extends K, ? extends V> m) {

		//Make a callable that will do the addAll
		Callable<Boolean> action = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				core.putAll(m);
				return true;
			}
		};
		
		//Perform the action with tracking
		trackAroundMapChange(action);
	}
	
	@Override
	public void replace(final Map<? extends K, ? extends V> newContents) {
		//Make a callable that will do the clear and addAll
		Callable<Boolean> action = new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				core.clear();
				core.putAll(newContents);
				return true;
			}
		};
		
		//Perform the action with tracking
		trackAroundMapChange(action);
	}

	//This is unfortunate but necessary... When we are tracking keys,
	//we want to add and remove key instances from tracking, rather than
	//using non-identical key instances that are equal. This function gets
	//the actual key instance in the map that is equal() to the specified key k,
	//or null if not found.
	private K findExactExistingKey(K k) {
		for (K checkKey : core.keySet()) {
			if (k.equals(checkKey)) {
				return checkKey;
			}
		}
		return null;
	}
	
	//Cast to K is safe, since we check that the key is in the map
	//before casting, and if it is in the map it is of type K
	@SuppressWarnings("unchecked")
	public V remove(Object key) {
		
		Props.getPropSystem().getChangeSystem().prepareChange(this);
		try {
			
			//If the key is not present, then nothing to do
			if (!core.containsKey(key)) {
				return null;
			}

			//The key IS present, so is of type K
			K k = (K)key;

			//We want to work with the ACTUAL key instance in the map,
			//but specified key may just be equal(). For example, if
			//we stop tracking k, we need the exact instance.
			k = findExactExistingKey(k);
			
			//Try to remove from core - if we get a runtime exception the 
			//value is not removed, so nothing to do
			V oldValue = core.remove(k);
			
			//Note that null values are ignored for tracking, so no need
			//to check whether mapping was actually present
			tracking.stopTrackingElement(oldValue);

			//Stop tracking key
			if (trackKeys) {
				keyTracking.stopTrackingElement(k);
			}
			
			//Start a map change
			//showing the removal
			MapDelta change = MapDeltaDefault.newRemoveChange(this, k);
			Props.getPropSystem().getChangeSystem().propagateChange(this, new MapChangeDefault(
					true,	//Initial change 
					false, 	//Different instances
					change));
			
			return oldValue;
	
		//Always conclude change
		} finally {
			Props.getPropSystem().getChangeSystem().concludeChange(this);
		}
	}

	//FIXME continue key alterations here
	
	//#####################################################################
	//
	//	The following methods require wrappers to prevent
	//	modifications that would not currently be noticed by the wrapper,
	//	and to enforce locking.
	//
	//#####################################################################

	public Collection<V> values() {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		try {		
			return new CollectionShell<V>(core.values());
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
		}
	}
	public Set<Entry<K, V>> entrySet() {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		try {
			return new SetShell<Entry<K,V>>(core.entrySet());
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
		}
	}
	public Set<K> keySet() {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		try {
			return new SetShell<K>(core.keySet());
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
		}
	}
	
	/**
	 *	Wraps an iterator, and makes main class fire property change (complete list
	 *	change) when remove is used, and adds appropriate locking.
	 */
	private class IteratorShell<T> implements Iterator<T>{
		
		//The wrapped iterator
		private final Iterator<T> it;
		
		/**
		 * Make a wrapper 
		 * @param it The iterator to wrap
		 */
		private IteratorShell(Iterator<T> it) {
			this.it = it;
		}

		//Method must ensure listbean compliance
		public void remove() {
//TODO this could work, using code similar to that below, if the wrapped
//iterator supports remove.
//			//Make a callable that will do the remove
//			Callable<Boolean> action = new Callable<Boolean>() {
//				@Override
//				public Boolean call() throws Exception {
//					it.remove();
//					return true;
//				}
//			};
//			
//			//Perform the action with tracking
//			trackAroundListChange(action);
			throw new UnsupportedOperationException("Cannot remove from CMapDefault-derived iterators");
		}
		
		//Methods delegated directly to the wrapped iterator
		public boolean equals(Object obj) {
			Props.getPropSystem().getChangeSystem().prepareRead(CMapDefault.this);
			try {
				return it.equals(obj);
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CMapDefault.this);
			}
		}
		public int hashCode() {
			Props.getPropSystem().getChangeSystem().prepareRead(CMapDefault.this);
			try {
				return it.hashCode();
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CMapDefault.this);
			}
		}
		public boolean hasNext() {
			Props.getPropSystem().getChangeSystem().prepareRead(CMapDefault.this);
			try {
				return it.hasNext();
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CMapDefault.this);
			}
		}
		public T next() {
			Props.getPropSystem().getChangeSystem().prepareRead(CMapDefault.this);
			try {
				return it.next();
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CMapDefault.this);
			}
		}
		public String toString() {
			Props.getPropSystem().getChangeSystem().prepareRead(CMapDefault.this);
			try {
				return it.toString();
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CMapDefault.this);
			}
		}
	}

	/**
	 * Wrap a collection to make it unmodifiable, and to respect locking, 
	 * and to pass on these restrictions to iterators on it
	 */
	private class CollectionShell<T> implements Collection<T> {
		private final Collection<T> coreCollection;

		private CollectionShell(Collection<T> coreList) {
			this.coreCollection = Collections.unmodifiableCollection(coreList);
		}

		private CollectionShell(Collection<T> coreList, boolean alreadyUnmodifiable) {
			this.coreCollection = alreadyUnmodifiable ? coreList : Collections.unmodifiableCollection(coreList);
		}

		//This is complex - must wrap the core iterator to pass on
		//locking
		public Iterator<T> iterator() {
			Props.getPropSystem().getChangeSystem().prepareRead(CMapDefault.this);
			try {
				return new IteratorShell<T>(coreCollection.iterator());
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CMapDefault.this);
			}
		}
		
		//These methods are simple - just lock around reading
		public boolean contains(Object o) {
			Props.getPropSystem().getChangeSystem().prepareRead(CMapDefault.this);
			try {
				return coreCollection.contains(o);
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CMapDefault.this);
			}
		}
		public boolean containsAll(Collection<?> c) {
			Props.getPropSystem().getChangeSystem().prepareRead(CMapDefault.this);
			try {
				return coreCollection.containsAll(c);
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CMapDefault.this);
			}
		}
		public boolean equals(Object o) {
			Props.getPropSystem().getChangeSystem().prepareRead(CMapDefault.this);
			try {
				return coreCollection.equals(o);
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CMapDefault.this);
			}
		}
		public int hashCode() {
			Props.getPropSystem().getChangeSystem().prepareRead(CMapDefault.this);
			try {
				return coreCollection.hashCode();
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CMapDefault.this);
			}
		}
		public boolean isEmpty() {
			Props.getPropSystem().getChangeSystem().prepareRead(CMapDefault.this);
			try {
				return coreCollection.isEmpty();
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CMapDefault.this);
			}
		}
		public int size() {
			Props.getPropSystem().getChangeSystem().prepareRead(CMapDefault.this);
			try {
				return coreCollection.size();
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CMapDefault.this);
			}
		}
		public Object[] toArray() {
			Props.getPropSystem().getChangeSystem().prepareRead(CMapDefault.this);
			try {
				return coreCollection.toArray();
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CMapDefault.this);
			}
		}
		public <S> S[] toArray(S[] a) {
			Props.getPropSystem().getChangeSystem().prepareRead(CMapDefault.this);
			try {
				return coreCollection.toArray(a);
			} finally {
				Props.getPropSystem().getChangeSystem().concludeRead(CMapDefault.this);
			}
		}	
		
		//Methods that modify the collection will all fail due to unmodifiable view
		//TODO: Note we could actually support these without a huge effort by tracking around
		//changes
		public boolean remove(Object o) {
			return coreCollection.remove(o);
		}
		public boolean removeAll(Collection<?> c) {
			return coreCollection.removeAll(c);
		}
		public boolean retainAll(Collection<?> c) {
			return coreCollection.retainAll(c);
		}
		public boolean add(T e) {
			return coreCollection.add(e);
		}
		public boolean addAll(Collection<? extends T> c) {
			return coreCollection.addAll(c);
		}
		public void clear() {
			coreCollection.clear();
		}
		
	}
	
	/**
	 * Wrap a Set to make it unmodifiable, and to respect locking, 
	 * and to pass on these restrictions to iterators on it
	 * Note: This is pretty much identical to CollectionShell, since
	 * Set is the same as Collection, apart from contracts.
	 */
	private class SetShell<T> extends CollectionShell<T> implements Set<T>{
		private SetShell(Set<T> coreSet) {
			super(Collections.unmodifiableSet(coreSet), 
					true);	//already unmodifiable - super won't wrap again
		}
	}

	
	//#####################################################################
	//
	//	The following methods are passed to the core map, but may read
	//	Changeable state so are read-locked.
	//
	//#####################################################################
	
	public boolean equals(Object o) {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		try {
			return core.equals(o);
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
		}
	}
	public V get(Object key) {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		try {
			return core.get(key);
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
	public boolean isEmpty() {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		try {
			return core.isEmpty();
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
	public boolean containsKey(Object key) { 
		//	since they have no interactions with the events/b
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		try {
			return core.containsKey(key);
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
		}
	}
	public boolean containsValue(Object value) {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		try {
			return core.containsValue(value);
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
		}
	}

}
