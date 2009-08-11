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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jpropeller.bean.Bean;
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
import org.jpropeller.properties.values.ValueProcessor;
import org.jpropeller.properties.values.impl.ReadOnlyProcessor;
import org.jpropeller.system.Props;

/**
 * A {@link Prop} which functions as a view of another
 * prop, mirroring its value and changes, but has its own value processor.
 * The most common use of this is to make an unmodifiable view of a 
 * modifiable prop. This is very similar to the use of 
 * {@link Collections#unmodifiableCollection(java.util.Collection)}
 * to make a modifiable {@link Collection} unmodifiable.
 * The normal use case for this is when a {@link Bean} wants to be able
 * to change a prop value internally, but not allow users to do this.
 * 
 * @param <T>		The type of the {@link Prop} value
 */
public class ViewProp<T> implements Prop<T> {

	private final Prop<T> viewed;
	private final ChangeableFeaturesDefault features;
	private final ValueProcessor<T> processor;
	private final PropName<T> name;

	/**
	 * Make an unmodifiable (read only) view of a given {@link Prop}
	 * @param newName		The new string name for the {@link ViewProp}
	 * @param viewed		The {@link Prop} we are viewing
	 * @param <T>			The type of value in the {@link Prop}
	 * 
	 * @return				A read-only view of the {@link Prop}
	 */
	public final static <T> ViewProp<T> unmodifiableProp(String newName, Prop<T> viewed) {
		return new ViewProp<T>(viewed.getName().renamed(newName), viewed, ReadOnlyProcessor.<T>get());
	}
	
	/**
	 * Create a prop
	 * @param name			The name for this prop 
	 * @param viewed		The viewed prop
	 * @param processor		The {@link ValueProcessor} used when a value is set
	 */
	public ViewProp(PropName<T> name, Prop<T> viewed, ValueProcessor<T> processor) {
		this.name = name;
		this.viewed = viewed;
		this.processor = processor;
		
		features = new ChangeableFeaturesDefault(new InternalChangeImplementation() {
			@Override
			public Change internalChange(Changeable changed, Change change,
					List<Changeable> initial, Map<Changeable, Change> changes) {
				
				//Since we only listen to the viewed prop, we always have a
				//change when we see one
				boolean sameInstances = changes.get(ViewProp.this.viewed).sameInstances();
				return ChangeDefault.instance(
						false,			//Change is consequence of viewed prop change 
						sameInstances	//Instance changes if viewed prop's does
						);
			}
		}, this); 

		//Listen to viewed bean
		viewed.features().addChangeableListener(this);
	}
	
	@Override
	public ChangeableFeatures features() {
		return features;
	}

	@Override
	public T get() {
		return viewed.get();
	}

	@Override
	public String toString() {
		return "View Prop '" + getName().getString() + "' = '" + get() + "'";
	}
	
	@Override
	public PropName<T> getName() {
		return name;
	}

	@Override
	public void set(T rawNewValue) {
		
		//If this fails, exception is thrown as desired
		T value = processor.process(rawNewValue);
		
		Props.getPropSystem().getChangeSystem().prepareChange(this);
		
		try {
			viewed.set(value);
			
			//Note we don't propagate the change - we know that the viewed prop will propagate it for us,
			//then we will notice it and report that we have changed too. This avoids having two "initial"
			//changes from one actual change
			
		} finally {
			Props.getPropSystem().getChangeSystem().concludeChange(this);
		}
	}
	
	@Override
	public PropEditability getEditability() {
		return processor.getEditability();
	}

	@Override
	public PropAccessType getAccessType() {
		return PropAccessType.SINGLE;
	}
}
