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

import org.jpropeller.collection.ObservableList;
import org.jpropeller.collection.ObservableMap;
import org.jpropeller.info.PropInfo;
import org.jpropeller.name.GenericPropName;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.GenericEditableProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.impl.ChangeDefault;
import org.jpropeller.properties.changeable.GenericEditableChangeableProp;
import org.jpropeller.system.Props;

/**
 * A {@link EditableProp} which can only have a value which is
 * a {@link Changeable}
 *
 * Implements deep change notification
 *
 * @author shingoki
 *
 * @param <T>
 * 		The type of the Prop value
 */
public class GenericEditableChangeablePropDefault<T extends Changeable> extends GenericChangeablePropDefault<T> implements GenericEditableChangeableProp<T> {

	GenericPropName<? extends GenericEditableProp<T>, T> editableName;
	
	/**
	 * Create a prop
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The initial value of the prop
	 */
	protected GenericEditableChangeablePropDefault(GenericPropName<? extends GenericEditableProp<T>, T> name, T value) {
		super(name, value);
		this.editableName = name;
	}

	@Override
	public void set(T value) {
			
		T oldValue = this.value;
		
		Props.getPropSystem().getChangeSystem().prepareChange(this);
		try {
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

	@Override
	public GenericPropName<? extends GenericEditableProp<T>, T> getName() {
		return editableName;
	}

	@Override
	public PropInfo getInfo() {
		return PropInfo.EDITABLE;
	}

	@Override
	public String toString() {
		return "Editable Changeable Prop '" + getName().getString() + "' = '" + get() + "'";
	}
	
	/**
	 * Make a new {@link GenericEditableChangeableProp} containing an
	 * {@link ObservableList} value
	 * @param <S>
	 * 		The type of value in the {@link ObservableList} in the prop
	 * @param name
	 * 		The name of the Prop
	 * @param clazz
	 * 		The class of value in the {@link ObservableList} in the prop
	 * @param value
	 * 		The initial value of the Prop
	 * @return
	 * 		The {@link Prop}
	 */
	public static <S> GenericEditableChangeablePropDefault<ObservableList<S>> createObservableList(String name, Class<S> clazz, ObservableList<S> value) {
		return new GenericEditableChangeablePropDefault<ObservableList<S>>(GenericPropName.editableObservableList(name, clazz), value);
	}

	/**
	 * Make a new {@link GenericEditableChangeableProp} containing an
	 * {@link ObservableMap} value
	 * @param <K>
	 * 		The type of key in the {@link ObservableMap} in the prop 
	 * @param <S>
	 * 		The type of value in the {@link ObservableMap} in the prop
	 * @param name
	 * 		The name of the Prop
	 * @param clazz
	 * 		The class of value in the {@link ObservableMap} in the prop
	 * @param value
	 * 		The initial value of the Prop
	 * @return
	 * 		The {@link Prop}
	 */
	public static <K, S> GenericEditableChangeablePropDefault<ObservableMap<K, S>> createObservableMap(String name, Class<S> clazz, ObservableMap<K, S> value) {
		GenericPropName<GenericEditableProp<ObservableMap<K,S>>,ObservableMap<K,S>> propName = GenericPropName.editableObservableMap(name, clazz);
		return new GenericEditableChangeablePropDefault<ObservableMap<K,S>>(propName, value);
	}
}
