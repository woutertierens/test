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

import org.jpropeller.info.PropInfo;
import org.jpropeller.name.GenericPropName;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.GenericProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.ChangeableFeatures;
import org.jpropeller.properties.change.impl.ChangeDefault;
import org.jpropeller.properties.change.impl.ChangeableFeaturesDefault;
import org.jpropeller.properties.change.impl.InternalChangeImplementation;
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
public class GenericChangeablePropDefault<T extends Changeable> implements GenericProp<T> {

	T value;
	GenericPropName<? extends GenericProp<T>, T> name;
	ChangeableFeatures features;
	
	/**
	 * Create a prop
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The initial value of the prop
	 */
	protected GenericChangeablePropDefault(GenericPropName<? extends GenericProp<T>, T> name, T value) {
		this.value = value;
		this.name = name;

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
	public GenericPropName<? extends GenericProp<T>, T> getName() {
		return name;
	}

	@Override
	public PropInfo getInfo() {
		return PropInfo.DEFAULT;
	}

	@Override
	public String toString() {
		return "Changeable Prop '" + getName().getString() + "' = '" + get() + "'";
	}
	
	/**
	 * Make a new {@link GenericChangeablePropDefault}
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
	public static <S extends Changeable> GenericChangeablePropDefault<S> create(String name, Class<S> clazz, S value) {
		return new GenericChangeablePropDefault<S>(PropName.create(name, clazz), value);
	}

}
