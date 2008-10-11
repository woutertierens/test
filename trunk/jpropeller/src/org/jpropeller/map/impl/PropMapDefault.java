/*
 *  $Id: PropMapDefault.java,v 1.1 2008/03/24 11:20:20 shingoki Exp $
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
package org.jpropeller.map.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jpropeller.bean.Bean;
import org.jpropeller.map.PropMap;
import org.jpropeller.map.PropMapMutable;
import org.jpropeller.name.GenericPropName;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.event.PropEvent;
import org.jpropeller.properties.event.PropEventDispatch;
import org.jpropeller.properties.event.PropEventDispatcher;
import org.jpropeller.properties.event.PropInternalListener;
import org.jpropeller.properties.event.PropListener;
import org.jpropeller.properties.event.impl.PropEventDispatchDefault;
import org.jpropeller.properties.event.impl.PropEventDispatcherSwing;

/**
 * A default implementation of {@link PropMap}
 * 
 * @author shingoki
 */
public class PropMapDefault implements PropMapMutable {
	
	/**
	 * Store all currently active events
	 */
	static Set<PropEvent<?>> activeEvents = new HashSet<PropEvent<?>>(500);
	
	/**
	 * Store all {@link PropEventDispatch} instances that are waiting to
	 * be delivered to the view layer
	 */
	static List<PropEventDispatch> dispatchQueue = new ArrayList<PropEventDispatch>(500);
	
	/**
	 * The dispatcher used to actually deliver from the dispatchQueue
	 * Defaults to a {@link PropEventDispatcherSwing} for safety, since
	 * Swing will be a common use of the props system.
	 */
	static PropEventDispatcher dispatcher = new PropEventDispatcherSwing();
	
	/**
	 * Change the dispatcher in use by {@link PropMapDefault}
	 * This will block while any events are being propagated
	 * @param newDispatcher
	 * 		The new dispatcher, to be used from the next event propagation
	 */
	public static void setDispatcher(PropEventDispatcher newDispatcher) {
		synchronized (activeEvents) {
			dispatcher = newDispatcher;			
		}
	}
	
	List<PropInternalListener> internalListeners;
	List<PropListener> listeners;
	Map<GenericPropName<?, ?>, GeneralProp<?>> map;
	List<GeneralProp<?>> list;
	List<GeneralProp<?>> umList;
	Bean bean;
	
	/**
	 * Make a {@link PropMap} for a given bean
	 * @param bean
	 * 		The bean by which this {@link PropMap} will be used
	 */
	protected PropMapDefault(Bean bean) {
		internalListeners = new ArrayList<PropInternalListener>(5);
		listeners = new ArrayList<PropListener>(5);
		map = new HashMap<GenericPropName<?, ?>, GeneralProp<?>>(10);
		list = new ArrayList<GeneralProp<?>>(10);
		umList = Collections.unmodifiableList(list);
		this.bean = bean;
	}
		
	/**
	 * Add a prop to the {@link PropMap}
	 * @param <P> 
	 * 		The type of prop
	 * @param <S>
	 * 		Type of data in the prop
	 * @param prop
	 * 		The prop
	 * @return
	 * 		The same prop (for chaining)
	 */
	public <P extends GeneralProp<S>, S> P add(P prop){

		//Sync on the prop and ourselves
		synchronized (prop) {
			synchronized (this) {
				
				GeneralProp<S> existingProp = getUnsafe(prop.getName());
				if (existingProp != null) {
					throw new IllegalArgumentException("Cannot add property '" + prop + "' since there is already a prop '" + existingProp + "' with the same name, '" + prop.getName() + "'");
				}
				
				//We need to fail if the prop already belongs to a PropMap
				if (prop.props() != null) {
					throw new IllegalArgumentException("Cannot add property '" + prop + "' since it already belongs to PropMap '" + prop.props());
				}

				//For safety, check that the info of the propname matches the info of the prop.
				//If this is the case then we know that the PropName type P matches the type of
				//the Prop (at least in terms of single/list/map and default/editable)
				if (prop.getInfo() != prop.getName().getPropInfo()) {
					throw new IllegalArgumentException(
							"Cannot add property '" + prop + 
							"' since its info " + prop.getInfo() + 
							" doesn't match its name's info " + prop.getName().getPropInfo());
				}
				
				//Note that we use the prop's own name as its key - hence the
				//parametric type of the Prop value always matches the
				//parametric type of the PropName key - this makes the
				//cast we perform in getProp(name) safe
				map.put(prop.getName(), prop);
				list.add(prop);
				
				//Set ourself as PropMap for the prop
				prop.setPropMap(this);
				
				return prop;
			}
		}
	}
		
	//Note that the suppressed warning is safe, since we know that we
	//always have the type P and parametric type <S> of values in the map matching
	//the type of keys, due to the code and types in addProp(prop)
	@SuppressWarnings("unchecked")
	@Override
	public <P extends GeneralProp<S>, S> P get(PropName<P, S> name) {
		return (P) map.get(name);
	}
	
	//Note that suppressed warning is NOT entirely safe. We know that we have 
	//retrieved a value that shares its BASIC class with P, but it may not
	//match in terms of its parametric types. This is in contrast to the safe
	//cast by get(PropName)
	@SuppressWarnings("unchecked")
	@Override
	public <P extends GeneralProp<S>, S> P getUnsafe(GenericPropName<P, S> name) {
		return (P) map.get(name);
	}

	
	@Override
	public List<GeneralProp<?>> getList() {
		return umList;
	}

	@Override
	public void propChanged(PropEvent<?> event) {
		
		//Propagate events internally first, then to
		//view listeners. 
		synchronized (activeEvents) {
			
			//Check whether we are already in the chain for the event - if so, it has cycled and should not
			//be passed on
			if (event.getPropsInChain().contains(this)) {
				//logger.finest("Cycle detected at prop " + this);
				return;
			}
			
			//Try to add event to active events, sever error if it is already there
			boolean added = activeEvents.add(event);
			if (!added) {
				//logger.severe("Found event already in activeEvents when about to fire: " + event);
			}
			
			//Perform internal propagation
			fireInternalEvent(event);
			
			//If we added the event to active events, then remove it
			if (added) {
				activeEvents.remove(event);
			}

			//Add event to queue to be fired to view layer
			queue(event);

			//If we just removed the last event, then it is safe to
			//fire all queued events to layers
			if (activeEvents.isEmpty()) {
				flushQueue();
			}
		}
		
	}

	/**
	 * Add this event to queue for dispatch. When the
	 * queue is flushed, it will fire all events to the view layer.
	 */
	private void queue(PropEvent<?> event) {
		synchronized (dispatchQueue) {
			for (PropListener listener : listeners) {
				PropEventDispatchDefault dispatch = 
					new PropEventDispatchDefault(event, listener);
				dispatchQueue.add(dispatch);
			}

		}
	}
	
	/**
	 * Flush the queue for dispatch.
	 */
	private void flushQueue() {
		synchronized (dispatchQueue) {
			dispatcher.dispatch(dispatchQueue);
			dispatchQueue.clear();
		}
	}
	
	@Override
	public void addListener(PropListener listener) {
		//Don't change listeners while events are active
		synchronized (activeEvents) {
			listeners.add(listener);
		}
	}

	@Override
	public void removeListener(PropListener listener) {
		//Don't change listeners while events are active
		synchronized (activeEvents) {
			listeners.remove(listener);
		}
	}
	
	/**
	 * Fire an event to all internal listeners
	 * @param event
	 * 		The event to fire
	 */
	private void fireInternalEvent(PropEvent<?> event) {
		for (PropInternalListener listener : internalListeners) {
			listener.propInternalChanged(event);
		}
	}
	
	@Override
	public void addInternalListener(PropInternalListener listener) {
			internalListeners.add(listener);			
	}

	@Override
	public void removeInternalListener(PropInternalListener listener) {
			internalListeners.remove(listener);
	}
	
	@Override
	public Bean getBean() {
		return bean;
	}
	
	@Override
	public Iterator<GeneralProp<?>> iterator() {
		return getList().iterator();
	}
	
}
