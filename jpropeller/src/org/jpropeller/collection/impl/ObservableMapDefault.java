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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.jpropeller.collection.MapChange;
import org.jpropeller.collection.ObservableMap;
import org.jpropeller.map.ExtendedPropMap;
import org.jpropeller.map.PropMap;
import org.jpropeller.properties.UneditableProp;
import org.jpropeller.properties.event.MapPropEvent;
import org.jpropeller.properties.event.PropEvent;
import org.jpropeller.properties.event.PropInternalListener;
import org.jpropeller.properties.event.impl.MapPropEventDefault;
import org.jpropeller.system.Props;

/**
 * An {@link ObservableMapDefault} wraps an underlying {@link Map} implementation, 
 * delegating actual storage of elements to this core map, and adding 
 * tracking of the elements so that events are generated as required 
 * for {@link ObservableMapDefault} compliance.
 * 
 * @author shingoki
 * @param <K> 
 *		The type of key in the map.
 * @param <V>
 * 		The type of element in the map.
 */
public class ObservableMapDefault<K, V> implements ObservableMap<K, V>, PropInternalListener {

	//Reference counter for elements in list
	ContentsTracking<V> tracking;
	
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

	//The Map we delegate to for actual storage, etc.
	private Map<K, V> core;
	
	//Unmodifiable views of core sets
	Collection<V> umValues;
	Set<Entry<K, V>> umEntrySet;
	Set<K> umKeySet;
	
	public UneditableProp<Long> modifications() { return modificationsProp; };
	
	/**
	 * Create a new {@link ObservableMapDefault} based on a new {@link HashMap}
	 */
	public ObservableMapDefault() {
		this(new HashMap<K, V>());
	}

	/**
	 * Create a new {@link ObservableMapDefault} based on a new {@link HashMap}
	 * @param capacity
	 * 		The initial capacity of the {@link HashMap}
	 */
	public ObservableMapDefault(int capacity) {
		this(new HashMap<K, V>(capacity));
	}

	/**
	 * Create a new {@link ObservableMapDefault} based on a given core
	 * map. The core {@link Map} provides the actual storage and
	 * implementation of {@link Map} methods - this shell just
	 * intercepts method calls to keep track of the {@link Map}
	 * contents to implement {@link ObservableMap}, for example 
	 * by firing the proper events on element changes, etc.
	 * 
	 * NOTE: you must NOT modify the core {@link Map} after using it
	 * to create an {@link ObservableMapDefault}, otherwise you will stop the
	 * {@link ObservableMapDefault} functioning as a compliant {@link ObservableMap}.
	 * It is safest not to retain a reference to the core {@link Map} at all,
	 * e.g.
	 * <code>MapBean<String> map = new ObservableMap(new TreeMap<String, String>());</code>
	 * 
	 * @param core
	 * 		The {@link Map} implementation this {@link ObservableMapDefault} will delegate to	
	 */
	public ObservableMapDefault(Map<K, V> core) {
		super();
		
		//If core is null, use an empty HashMap
		if (core == null) core = new HashMap<K, V>();
		
		propMap.add(modificationsProp);
		this.core = core;
		
		//Make um (unmodifiable) views of core map
		umValues = Collections.unmodifiableCollection(core.values());
		umEntrySet = Collections.unmodifiableSet(core.entrySet());
		umKeySet = Collections.unmodifiableSet(core.keySet());
		
		tracking = new ContentsTracking<V>(this);		

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
		//map values and fire a change with a defined location at this point
		//Any element in map may have changed, but size of map has not changed
		MapChange<K> mapChange = MapChangeDefault.newEntireMapAlteration(this);
		
		//Any propChange we receive is due to map contents changing
		//in a deep way, so increment the modifications prop and fire a deep map change
		MapPropEvent<K, Long> mapEvent = new MapPropEventDefault<K, Long>(modificationsProp, event, mapChange);
		modificationsProp.increment(mapEvent);
	}

	/**
	 * Start tracking all elements in the list again. 
	 * clearAllTracking() is called, then all elements
	 * are passed to startTrackingElement(e);
	 */
	private void retrackAll() {
		//Stop tracking all elements
		tracking.clearAllTracking();
		
		//Track each value in the map, making sure that we start tracking
		//it once for EACH MAPPING that points to that value
		//Hence we don't iterate the value set, which contains values
		//mapped-to by multiple keys only once - we iterate the key set,
		//then follow each key to the value
		for (K k : core.keySet()) {
			tracking.startTrackingElement(core.get(k));
		}
	}
	
	/**
	 * Perform a change to the core map in a way that preserves proper tracking,
	 * and then fires an event for a complete change to the map
	 * @param action
	 * 		The action to perform to the map - may manipulate the core map in
	 * any way, but should not do anything else.
	 * 		The action must NOT throw a non-runtime exception - if it does, an
	 * {@link ObservableCollectionRuntimeException} will be thrown with the 
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
	 * @throws ObservableCollectionRuntimeException
	 * 		If the action throws a non-runtime exception
	 */
	private boolean trackAroundMapChange(Callable<Boolean> action) {
		
		int oldSize = size();
		
		//To start with, clear all references, and stop listening to all contents.
		//The action is assumed to be a major enough operation that we don't 
		//try to track it in detail
		tracking.clearAllTracking();
		
		//Do the actual action
		try {
			try {
				return action.call();
			} catch (Exception e) {
				//We do not expect a non-runtime exception from the action, this is an error
				throw new ObservableCollectionRuntimeException("Unexpected exception from a map action", e);
			}
			
		//Always reinstate tracking and fire prop change after the change - it may complete, or it
		//may throw an exception after adding some elements 
		} finally {
			
			//Reinstate tracking on all elements
			retrackAll();
			
			//Increment the modification count, with a list change
			//showing we changed everything
			MapChange<K> mapChange = MapChangeDefault.newCompleteChange(size(), oldSize);
			modificationsProp.incrementFromMapChange(mapChange);
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
		
		//Do we have an existing value? This affects the "old size" of the
		//map change we will fire
		boolean existingValue = core.containsKey(key);
		
		//Try to put in core - if we get a runtime exception the 
		//value is not put, so nothing to do
		V oldValue = core.put(key, value);
		
		//Note that null values are ignored for tracking, so no need
		//to check whether mapping was actually present
		tracking.stopTrackingElement(oldValue);

		tracking.startTrackingElement(value);

		//Increment the modification count, with a list change
		//showing the set
		MapChange<K> change;
		if (existingValue) {
			change = MapChangeDefault.newSingleKeyAlteration(this, key);
		} else {
			change = MapChangeDefault.newInsertionChange(this, key);			
		}
		modificationsProp.incrementFromMapChange(change);
		
		return oldValue;
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
	
	//Cast to K is safe, since we check that the key is in the map
	//before casting, and if it is in the map it is of type K
	@SuppressWarnings("unchecked")
	public V remove(Object key) {
		
		//If the key is not present, then nothing to do
		if (!core.containsKey(key)) {
			return null;
		}
		
		//The key IS present, so is of type K
		K k = (K)key;
		
		//Try to remove from core - if we get a runtime exception the 
		//value is not removed, so nothing to do
		V oldValue = core.remove(k);
		
		//Note that null values are ignored for tracking, so no need
		//to check whether mapping was actually present
		tracking.stopTrackingElement(oldValue);

		//Increment the modification count, with a list change
		//showing the removal
		MapChange<K> change = MapChangeDefault.newRemoveChange(this, k);
		modificationsProp.incrementFromMapChange(change);
		
		return oldValue;

	}

	//#####################################################################
	//
	//	The following methods require unmodifiable wrappers to prevent
	//	modifications that would not currently be noticed by the wrapper
	//
	//#####################################################################

	public Collection<V> values() {
		return umValues;
	}
	public Set<Entry<K, V>> entrySet() {
		return umEntrySet;
	}
	public Set<K> keySet() {
		return umKeySet;
	}
	
	//#####################################################################
	//
	//	The following methods are just passed directly to the core map, 
	//	since they have no interactions with the events/bean system
	//
	//#####################################################################
	
	public boolean equals(Object o) {
		return core.equals(o);
	}
	public V get(Object key) {
		return core.get(key);
	}
	public int hashCode() {
		return core.hashCode();
	}
	public boolean isEmpty() {
		return core.isEmpty();
	}
	public int size() {
		return core.size();
	}
	public boolean containsKey(Object key) {
		return core.containsKey(key);
	}
	public boolean containsValue(Object value) {
		return core.containsValue(value);
	}

}
