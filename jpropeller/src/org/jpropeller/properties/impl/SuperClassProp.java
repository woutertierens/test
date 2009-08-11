/*
 *  $Id: MutablePropPrimitive.java,v 1.1 2008/03/24 11:19:49 shingoki Exp $
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
package org.jpropeller.properties.impl;

import java.util.List;
import java.util.Map;

import org.jpropeller.info.PropAccessType;
import org.jpropeller.info.PropEditability;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.ChangeableFeatures;
import org.jpropeller.properties.change.impl.ChangeDefault;
import org.jpropeller.properties.change.impl.ChangeableFeaturesDefault;
import org.jpropeller.properties.change.impl.InternalChangeImplementation;
import org.jpropeller.system.Props;

/**
 * A {@link Prop} which mirrors the value of another "core" prop.
 * The core prop must have a value type that extends the value type
 * of this {@link Prop}. Hence getting values is permitted, but setting
 * values is not.
 * 
 * This essentially allows implementations of an interface to have a more 
 * specific {@link Prop} type than given in the interface, but then 
 * also provide a mirror of that specific {@link Prop} as a more 
 * general {@link SuperClassProp} to implement the interface.
 * 
 * @param <T>
 * 		The type of the {@link Prop} value
 */
public class SuperClassProp<T> implements Prop<T> {

	final Prop<? extends T> core;
	PropName<T> name;
	ChangeableFeaturesDefault features;
	
	/**
	 * Create a {@link SuperClassProp}
	 * 
	 * @param name			The name of the prop
	 * @param core			The prop mirrored by this prop
	 */
	public SuperClassProp(PropName<T> name, final Prop<? extends T> core) {
		this.name = name;
		this.core = core;
		
		features = new ChangeableFeaturesDefault(new InternalChangeImplementation() {
			@Override
			public Change internalChange(Changeable changed, Change change,
					List<Changeable> initial, Map<Changeable, Change> changes) {
				
				boolean sameInstances = changes.get(core).sameInstances();
				
				//We may have a change whenever the mirrored prop has a change,
				return ChangeDefault.instance(
						false,	//Change is consequence of core change
						
						sameInstances	//We point to same instance if the 
										//core prop does
						);
			}
		}, this); 

		core.features().addChangeableListener(this);
	}
	
	@Override
	public ChangeableFeatures features() {
		return features;
	}

	@Override
	public T get() {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		try {
			//Get value from filtered prop
			return core.get();
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
		}
	}

	@Override
	public String toString() {
		return "Super-Class Prop '" + getName().getString() + "' = '" + get() + "'";
	}
	
	@Override
	public PropName<T> getName() {
		return name;
	}

	@Override
	public PropEditability getEditability() {
		return PropEditability.READ_ONLY;
	}

	@Override
	public PropAccessType getAccessType() {
		return PropAccessType.SINGLE;
	}

	@Override
	public void set(T value) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Cannot set value of Super-Class Prop");
	}
	
}
