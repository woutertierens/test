/*
 *  $Id: MutableProp.java,v 1.1 2008/03/24 11:19:35 shingoki Exp $
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
package org.jpropeller.properties;

import java.util.Collection;

import org.jpropeller.bean.Bean;
import org.jpropeller.bean.BeanFeatures;
import org.jpropeller.collection.CList;
import org.jpropeller.collection.CMap;
import org.jpropeller.collection.CSet;
import org.jpropeller.info.PropAccessType;
import org.jpropeller.info.PropEditability;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.exception.InvalidValueException;
import org.jpropeller.properties.exception.ReadOnlyException;
import org.jpropeller.properties.list.ListProp;
import org.jpropeller.properties.map.MapProp;
import org.jpropeller.properties.set.SetProp;

/**
 *
 * A {@link Prop} acts as an "enhanced" reference to a value. The enhancements 
 * include:
 * <ul>
 * <li>Implementing {@link Changeable}, so that a {@link Prop} can be monitored, and
 * changes detected, for example to allow binding to a UI, automatic calculations
 * based on {@link Prop} values, etc.</li>
 * <li>Specific {@link #get()} and {@link #set(Object)} methods, so that access to the
 * value can be controlled.</li>
 * <li>Use of a {@link PropName} to identify the {@link Prop} and provide information
 * about its class even when it contains a null value (this is a partial solution
 * to type erasure). This {@link PropName} can be used, for example, to look up
 * {@link Prop}s that are held in {@link Bean}s.</li>
 * <li>Information about the {@link PropAccessType} allows quicker casting to a {@link Prop}
 * subinterface with extra access for specific contents - currently {@link ListProp},
 * {@link MapProp} and {@link SetProp} for {@link CList}, {@link CMap}
 * and {@link CSet} respectively.</li>
 * <li>Information about the {@link PropEditability}, showing whether the {@link Prop}
 * will accept new values via the {@link #set(Object)} method.
 * </ul>
 * 
 * {@link Prop}s are mutable, but have no mutable state EXCEPT by having a new value
 * set, or by that value itself changing).
 * <p/>
 * 
 * Every {@link Prop} has a {@link #get()} method which will return the current value,
 * and a {@link #set(Object)} method which <i>may</i> modify the value. However each
 * {@link Prop} may choose to reject any given value as invalid, throwing an {@link InvalidValueException}.
 * In addition, some {@link Prop}s may be permanently read-only, and never accept a new
 * value - in this case they have {@link PropEditability} as {@link PropEditability#READ_ONLY},
 * and throw a {@link ReadOnlyException} when {@link #set(Object)} is called. 
 * <p/>
 * NOTE: It is not guaranteed that when {@link #set(Object)} is called, the {@link Prop}
 * value returned by {@link #get()} will then match the value specified - the {@link Prop}
 * is permitted to transform input values, for example a {@link Prop} containing Double values
 * may set itself to 0 when negative input values are provided - in this case negative values
 * are not invalid, but are not used directly.
 * <p/>
 * NOTE: It is suggested that {@link Prop}s that hold data have a constant behaviour with respect to
 * which values they will accept, and how they respond to accepting a given value. Since
 * they must have no state except the value they contain, they cannot change the way they
 * respond to new values. This is important for serialisation, etc.
 * <br/>
 * For example, it is valid for a data {@link Prop} to accept only positive numbers, or to
 * set itself to 0 when given a negative number. However, it is NOT valid for a {@link Prop}
 * to sometimes accept negative numbers, and at other times not. That is, the set of accepted
 * values and the responses to each value must be invariable. For long term persistence, the
 * behaviour of each {@link Prop} of a {@link Bean} must also stay the same between different
 * JVM sessions, etc. 
 * <br/>
 * {@link Prop}s which are entirely transient - just mirroring or processing the state of other 
 * {@link Prop}s - are permitted to have a varying response to new values, since they may be
 * mirroring different {@link Prop}s. This is permissible because they do not contain state
 * that needs to be persisted or used by undo/redo systems, etc.
 * <p/>
 * It would be possible to have a separate interface for read-only {@link Prop}s, lacking the
 * {@link #set(Object)} method. However in practice this leads to the need for duplication of
 * {@link Prop} implementations, confusion over use of these multiple implementations, and
 * an explosion of different interfaces (for example to represent normal and read-only versions
 * of each of list, set and map props).
 * <p/>
 * For these reasons, it has been decided to have only a single {@link Prop} interface, and have
 * invalid {@link #set(Object)} calls detected at runtime. This is exactly analogous to the 
 * behaviour of {@link Collection}s.
 * <p/>
 * JPropeller provides implementations of {@link Prop} covering most uses, and so the majority
 * of users will simply make use of existing {@link Prop}s. When implementing {@link Prop}s, it
 * is very important to consider the contract for {@link Changeable}, etc. In general, {@link Prop}s
 * are designed to be very carefully written once, but then to support being used with relatively
 * few restrictions - for example, {@link Prop} implementations are {@link Changeable}, and so
 * are synchronised internally and with each other. 
 * <p/>
 * <b>NOTE:</b> The {@link PropName} returned by {@link #getName()} must have class information matching the
 * {@link Prop} itself. 
 * If this is done wrongly, it should be caught by compliant implementations where possible.
 * 
 * @param <T>
 * 		The type of value in the prop
 */
public interface Prop<T> extends Changeable {
	
	/**
	 * Get the {@link PropEditability} of the {@link Prop}
	 * @return {@link PropEditability}
	 */
	public PropEditability getEditability();
	
	/**
	 * Get the {@link PropAccessType} of the {@link Prop}
	 * @return {@link PropAccessType}
	 */
	public PropAccessType getAccessType();
	
	/**
	 * The name of the prop
	 * This is used in the {@link BeanFeatures} to look up {@link Prop}s via {@link BeanFeatures#get(PropName)}
	 * @return
	 * 		Name of the prop
	 */
	public PropName<T> getName();
	
	/**
	 * Set the prop value
	 * @param value
	 * 		The new value of the prop
	 * @throws ReadOnlyException
	 * 		If the property cannot accept any value
	 * @throws InvalidValueException
	 * 		If the property will accept some values, but
	 * NOT the one given.  
	 */
	public void set(T value) throws ReadOnlyException, InvalidValueException;

	/**
	 * Get the prop value
	 * @return
	 * 		The value of the prop
	 */
	public T get();
	
}
