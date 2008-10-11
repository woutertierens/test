/*
 *  $Id: PropEvent.java,v 1.1 2008/03/24 11:19:34 shingoki Exp $
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
package org.jpropeller.properties.event;

import java.util.Set;

import org.jpropeller.info.PropAccessType;
import org.jpropeller.map.PropMap;
import org.jpropeller.properties.GeneralProp;

/**
 * Stores all details of a change to a property
 * The {@link PropMap} to which a Prop belongs will fire such an event
 * to all PropListeners whenever the value returned by a Prop
 * changes for any reason.
 * 
 * Note that a Prop value is considered to have changed if it
 * changes in:
 * 		A shallow way - that is the reference returned
 * points (or may point) to a different instance, for example a Prop<Double> is
 * changed from 2 to 3, OR;
 * 		A deep way - this only occurs when the value returned by
 * a Prop has Props itself, and one of those Props changes. For
 * example, if a Person has a Prop address (class Address), and 
 * that address Prop in turn has a streetName Prop (class String),
 * then changing the streetName will obviously change the address.
 * But since the Person has that address as a Prop, it has changed - 
 * for example, a view of it may need to be updated, or the Person
 * may need to be saved to disk. As a result, a deep property change
 * is fired by the person's {@link PropMap}. This references the original
 * (shallow) change that caused the deep change in several ways.
 * Obviously there may be a long chain of these changes where
 * Props are nested multiple layers deep as in a typical application. In
 * this case it is possible to iterate through the chain of PropEvents,
 * each iteration step moves one stage "back" towards the original
 * change that started everything. This original change is accessible
 * directly as the "rootEvent".
 * 
 * Shallow changes can be distinguished from deep ones by the deep property.
 *  
 * Note that it is important that events are correct in their "deepness" - 
 * if the instance returned by the get method(s) of a {@link GeneralProp} have
 * changed then the event MUST be shallow. Otherwise it may be marked deep. Note
 * however that it may be acceptable in some cases for a deep change to be
 * shown by a shallow {@link PropEvent}, for example when a calculated or
 * path based property changes. This is because some optimisations may react
 * differently to shallow changes, where they need to allow for a new instance,
 * as opposed to a deep change where the same instance is still present.
 *  
 * Finally, events are considered to have an "origin", which allows tools
 * to use them more intelligently. At present, there are only three origins: 
 * 		USER, indicating that the change came from "outside the system" - 
 * as the result of a direct edit by the user of the system of Props (not
 * necessarily a person); 
 * 
 * 		CONSEQUENCE, indicating the change came from "inside the system", 
 * as a consequence of another Prop change, OR;
 * 
 * 		INEVITABLE, indicating an automatic update due to an inevitable
 * occurrence such as time passing, a process completing, or an external
 * monitored device changing, etc.
 * 
 *  The main use of the origin of an event is to determine whether the change
 *  can be undone/redone - changes that have the USER as an origin can be undone/redone,
 *  whereas CONSEQUENCEs either do not need to be undone/redone (they will happen
 *  automatically), or cannot be undone/redone (they happened as a result of
 *  something outside of the control of the software system).
 * 
 * The event is Iterable - the Iterator will return THIS EVENT FIRST, followed by
 * the event that caused this event, then the event that caused THAT event, and
 * so on back to the rootEvent as the last event returned. For shallow events,
 * only this event will be returned.
 * 
 * @author shingoki
 *
 * @param <T>
 * 		The parametric type of the Prop to which the event refers
 */
public interface PropEvent<T> extends Iterable<PropEvent<?>> {

	/**
	 * The property that has changed
	 * @return
	 * 		prop
	 */
	public GeneralProp<T> getProp();
			
	/**
	 * The origin of the root event - whether the change was originally caused
	 * by a user editing the system, a consequent update, an inevitable change, etc. 
	 * @return
	 * 		origin
	 */
	public PropEventOrigin getRootOrigin();
	
	/**
	 * Whether the change is deep - if not, the Prop itself has changed. If
	 * the change is deep, a Prop in the tree of Props leading from its value
	 * has changed - please see javadocs for this interface for a more detailed
	 * description. 
	 * @return
	 * 		deep
	 */
	public boolean isDeep();

	/**
	 * The cause of this event, which is the next event in the chain of events - 
	 * please note that this is null for events where isDeep() returns false
	 * @return
	 * 		nextEvent
	 */
	public PropEvent<?> getCauseEvent();

	//FIXME initial event? First event?
	/**
	 * The root event in the chain of events - please note that this returns
	 * "this" for events where isDeep() returns false. That is, a shallow event
	 * is its own root event.
	 * @return
	 * 		rootEvent
	 */
	public PropEvent<?> getRootEvent();
	
	/**
	 * Get a set of Props that have been involved in this chain. For a deep
	 * event, returns a set containing the current prop and that props of all
	 * causes in the chain. For a shallow event, contains the current prop only.
	 * @return
	 * 		The props involved in the chain
	 */
	public Set<GeneralProp<?>> getPropsInChain();

	/**
	 * The type of prop to which the event applies event, can be used to quickly check type
	 * rather than using instanceof, before casting to
 	 * {@link PropEvent}, {@link ListPropEvent}, or {@link MapPropEvent}
	 * @return
	 * 		PropEvent type
	 */
	public PropAccessType getType();
}
