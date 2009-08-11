/*
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
package org.jpropeller.properties.constrained.impl;

import java.util.List;
import java.util.Map;

import org.jpropeller.constraint.Constraint;
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
 * A {@link Prop} which constrains its value when read,
 * based on a {@link Constraint} that calculates its value 
 * as needed based on the values of other {@link Changeable}s,
 * AND on any values set into the {@link ConstrainedProp} since
 * it was last read.
 * 
 * @param <T>		The type of the {@link Prop} value
 */
public class ConstrainedProp<T> implements Prop<T> {

	T value = null;
	boolean cacheValid = false;
	PropName<T> name;
	Constraint<T> constraint;
	ChangeableFeaturesDefault features;
	
	/**
	 * Create a prop
	 * @param name			The name of the prop
	 * @param constraint	The {@link Constraint} used for the prop value
	 */
	public ConstrainedProp(PropName<T> name, Constraint<T> constraint) {
		this.name = name;
		this.constraint = constraint;
		
		features = new ChangeableFeaturesDefault(new InternalChangeImplementation() {
			@Override
			public Change internalChange(Changeable changed, Change change,
					List<Changeable> initial, Map<Changeable, Change> changes) {
				
				//Mark the cache as invalid - it will need to be recalculated
				cacheValid = false;
				
				//Since we only listen to the actual contained value and the
				//values that affect the constraint, we don't need to filter 
				//here, we might always have a change (since the Constraint
				//can't filter changes - it is always reapplied if anything
				//changes)
				return ChangeDefault.instance(
						false,	//Change is consequence of source proper change 
						false	//Instance may well change - we don't know until we recalculate, 
								//but it is very likely (e.g. a new primitive wrapper value)
						);
			}
		}, this); 

		//Listen to each source changeable
		for (Changeable changeable : constraint.getSources()) {
			changeable.features().addChangeableListener(this);
		}
	}
	
	@Override
	public ChangeableFeatures features() {
		return features;
	}

	@Override
	public T get() {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		try {
			if (!cacheValid) {

				T oldValue = this.value;

				value = constraint.constrain(value);
				
				//If value changes, listen to new value
				adjustListeners(oldValue, value);
				
				cacheValid = true;
			}
			
			return value;
			
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
		}
	}

	@Override
	public String toString() {
		return "Constrained Prop '" + getName().getString() + "' = '" + get() + "'";
	}
	
	@Override
	public PropName<T> getName() {
		return name;
	}

	@Override
	public PropEditability getEditability() {
		return PropEditability.EDITABLE;
	}

	@Override
	public PropAccessType getAccessType() {
		return PropAccessType.SINGLE;
	}

	@Override
	public void set(T value) {
		
		Props.getPropSystem().getChangeSystem().prepareChange(this);
		try {
			T oldValue = this.value;
			
			this.value = value;
			
			//If value changes, listen to new value
			adjustListeners(oldValue, value);
			
			//We will need to recalculate constraint when read
			cacheValid = false;
			
			//Propagate the change we just made
			Props.getPropSystem().getChangeSystem().propagateChange(this, ChangeDefault.instance(
					true,	//Change IS initial 
					false	//Instance is NOT the same - new value set
					));
			
		//Always conclude change
		} finally {
			Props.getPropSystem().getChangeSystem().concludeChange(this);
		}
	}

	private void adjustListeners(T oldValue, T value) {
		if (oldValue != value) {
			//We need to stop listening to the old value, and start listening to the new one
			if (oldValue != null && oldValue instanceof Changeable) {
				((Changeable)oldValue).features().removeChangeableListener(this);
			}
			if (value != null && value instanceof Changeable) {
				((Changeable)value).features().addChangeableListener(this);
			}
		}
	}
	
}
