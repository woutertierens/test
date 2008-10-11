/*
 *  $Id: PropEventDefault.java,v 1.1 2008/03/24 11:20:03 shingoki Exp $
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
package org.jpropeller.properties.event.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.jpropeller.info.PropAccessType;
import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.event.PropEvent;
import org.jpropeller.properties.event.PropEventOrigin;

/**
 * A default implementation of a {@link PropEvent}
 * @author shingoki
 *
 * @param <T>
 * 		The type of value in the prop having had the event
 */
public class PropEventDefault<T> implements PropEvent<T> {

	GeneralProp<T> prop;
	PropEventOrigin rootOrigin;

	boolean deep;
	PropEvent<?> causeEvent;
	PropEvent<?> rootEvent;

	/**
	 * The set of props in the chain for this event
	 */
	private Set<GeneralProp<?>> propsInChain;
	
	
	/**
	 * Create a shallow PropEvent. This indicates that
	 * the prop has had set(value) called directly,
	 * and so the event is NOT the result of a deep
	 * change.
	 * 
	 * deep will be set to false, and causeEvent/rootEvent
	 * set to null
	 * 
	 * @param prop
	 * 		The property that has changed
	 * @param rootOrigin
	 * 		The root origin of the change - user, consequence, etc.
	 */
	public PropEventDefault(GeneralProp<T> prop,
			PropEventOrigin rootOrigin) {
		super();
		this.prop = prop;
		this.rootOrigin = rootOrigin;
		
		//Not a deep change, no cause event in the chain
		this.deep = false;
		this.causeEvent = null;
		
		//The event is its own root event.
		this.rootEvent = this;
		
		//Start a new set
		propsInChain = new HashSet<GeneralProp<?>>(1);
		propsInChain.add(prop);
	}

	/**
	 * Create a deep change. This is a change that occurs as a 
	 * result of another change to a child of the prop, or a child of
	 * a child of the prop, etc.
	 * Only the event that caused this event is required to construct
	 * the other "deep" properties of the event
	 * Note that since the prop itself has not changed in a shallow way,
	 * the old and new values will both be set to the current value of the
	 * prop
	 * @param prop
	 * 		The property that has changed
	 * @param causeEvent
	 * 		The event that caused this event
	 */
	public PropEventDefault(GeneralProp<T> prop, PropEvent<?> causeEvent) {
		super();
		this.prop = prop;
		
		//We can get the root origin from the next event
		this.rootOrigin = causeEvent.getRootOrigin();
		
		//Change is deep, going to next cause event in the change
		this.deep = true;
		this.causeEvent = causeEvent;
		
		//Our root event is taken from the cause event
		this.rootEvent = causeEvent.getRootEvent();
		
		//Base new set on causeEvent's set, adding the current prop
		propsInChain = new HashSet<GeneralProp<?>>(causeEvent.getPropsInChain().size() + 1);
		propsInChain.addAll(getCauseEvent().getPropsInChain());
		propsInChain.add(prop);
	}

	@Override
	public String toString() {
		if (!isDeep()) {
			return "Change to '" + prop.getBean() + "'.'" + prop.getName() + "', root origin " + getRootOrigin(); 
		} else {
			return "Deep change to '" + prop.getBean() + "'.'" + prop.getName() + "', root origin " + getRootOrigin() + ", root event (" + getRootEvent() + ")";
		}
	}

	public Iterator<PropEvent<?>> iterator() {
		
		Iterator<PropEvent<?>> it = new Iterator<PropEvent<?>>() {
		
			//Start from this event
			PropEvent<?> nextEvent = PropEventDefault.this;
			
			@Override
			public void remove() {
				//Nothing we can do - this is not backed by any editable data
				throw new UnsupportedOperationException("Cannot remove events from chain");
			}
		
			@Override
			public PropEvent<?> next() {
				
				//Out of elements
				if (nextEvent == null) {
					throw new NoSuchElementException();
				}
				
				//Will return next event
				PropEvent<?> toReturn = nextEvent;
				
				//If current next event is deep, we can iterate to its
				//cause event - otherwise we are done
				if (nextEvent.isDeep()) {
					nextEvent = nextEvent.getCauseEvent();
				} else {
					nextEvent = null;
				}
				
				return toReturn;
			}
		
			@Override
			public boolean hasNext() {
				return (nextEvent != null);
			}
		
		};
		
		return it;
	}

	
	public boolean isDeep() {
		return deep;
	}
	public PropEventOrigin getRootOrigin() {
		return rootOrigin;
	}
	public GeneralProp<T> getProp() {
		return prop;
	}
	public PropEvent<?> getCauseEvent() {
		return causeEvent;
	}
	public PropEvent<?> getRootEvent() {
		return rootEvent;
	}

	@Override
	public Set<GeneralProp<?>> getPropsInChain() {
		return propsInChain;
	}

	@Override
	public PropAccessType getType() {
		return PropAccessType.SINGLE;
	}
	
}
