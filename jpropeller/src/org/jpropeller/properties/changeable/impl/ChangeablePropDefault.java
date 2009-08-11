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
package org.jpropeller.properties.changeable.impl;

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
import org.jpropeller.properties.changeable.ChangeableProp;
import org.jpropeller.properties.values.ValueProcessor;
import org.jpropeller.properties.values.impl.AcceptProcessor;
import org.jpropeller.properties.values.impl.ReadOnlyProcessor;
import org.jpropeller.system.Props;

/**
 * A {@link Prop} which can only have a value which is 
 * a {@link Changeable}
 *
 * Implements deep change notification
 *
 * @param <T>
 * 		The type of the {@link Prop} value
 */
public class ChangeablePropDefault<T extends Changeable> implements ChangeableProp<T> {

	protected T value;
	protected PropName<T> name;
	protected ChangeableFeatures features;
	protected ValueProcessor<T> processor;
	
	/**
	 * Create a prop
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The initial value of the prop
	 * @param processor
	 * 		The {@link ValueProcessor} to check set values
	 */
	protected ChangeablePropDefault(PropName<T> name, T value, ValueProcessor<T> processor) {
		this.value = value;
		this.name = name;
		this.processor = processor;
		
		features = new ChangeableFeaturesDefault(new InternalChangeImplementation() {
			@Override
			public Change internalChange(Changeable changed, Change change,
					List<Changeable> initial, Map<Changeable, Change> changes) {
				//We only listen to our current (Changeable) value, when it has a change we have a consequent change
				return ChangeDefault.instance(
						false,	//Not initial 
						true	//Does preserve same instance - we still have the same Changeable value, but it has had a change itself
						);
			}
		}, this); 
		
		//Make sure we listen to our initial value
		if (value != null) {
			value.features().addChangeableListener(this);
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
			return value;
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
		}
	}

	@Override
	public PropName<T> getName() {
		return name;
	}

	@Override
	public PropEditability getEditability() {
		return processor.getEditability();
	}

	@Override
	public PropAccessType getAccessType() {
		return PropAccessType.SINGLE;
	}

	@Override
	public String toString() {
		return "Changeable Prop '" + getName().getString() + "' = '" + get() + "'";
	}
	
	@Override
	public void set(T rawNewValue) {
		//Note - if this fails, the set will just fail with the same exception, as required
		T value = processor.process(rawNewValue);
		
		Props.getPropSystem().getChangeSystem().prepareChange(this);
		try {
			T oldValue = this.value;
			
			this.value = value;
			
			//If value changes, listen to new value
			if (oldValue != value) {
				//We need to stop listening to the PropMap of the old value, and start listening to the new one
				if (oldValue != null) {
					oldValue.features().removeChangeableListener(this);
				}
				if (value != null) {
					value.features().addChangeableListener(this);
				}
			}
			
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

	
	/**
	 * Make a new read-only {@link ChangeablePropDefault} 
	 * @param <S>
	 * 		The type of {@link Changeable} in the prop
	 * @param name
	 * 		The name of the Prop
	 * @param clazz
	 * 		The class of the Prop 
	 * @param value
	 * 		The initial value of the Prop
	 * @return
	 * 		The {@link Prop}
	 */
	public static <S extends Changeable> ChangeablePropDefault<S> create(String name, Class<S> clazz, S value) {
		return new ChangeablePropDefault<S>(PropName.create(name, clazz), value, ReadOnlyProcessor.<S>get());
	}
	
	/**
	 * Make a new {@link ChangeablePropDefault} accepting 
	 * any value
	 * @param <S>
	 * 		The type of {@link Changeable} in the prop
	 * @param name
	 * 		The name of the Prop
	 * @param clazz
	 * 		The class of the Prop 
	 * @param value
	 * 		The initial value of the Prop
	 * @return
	 * 		The Prop
	 */
	public static <S extends Changeable> ChangeablePropDefault<S> editable(String name, Class<S> clazz, S value) {
		return new ChangeablePropDefault<S>(PropName.create(name, clazz), value, AcceptProcessor.<S>get());
	}
}
