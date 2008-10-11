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
package org.jpropeller.properties.primitive.impl;

import org.jpropeller.bean.Bean;
import org.jpropeller.info.PropInfo;
import org.jpropeller.map.PropMap;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.event.PropEventOrigin;
import org.jpropeller.properties.event.impl.PropEventDefault;

/**
 * A EditableProp which can only have a value of a primitive wrapper
 * type - that is:
 * 
 * Boolean
 * Short
 * Integer
 * Long
 * String
 * Float
 * Double
 *
 * All these wrapper types are immutable, so this prop only implements
 * shallow change notification
 *
 * @author shingoki
 *
 * @param <T>
 * 		The type of the Prop value
 */
public class EditablePropPrimitive<T> implements EditableProp<T> {

	PropMap propMap;
	T value;
	PropName<EditableProp<T>, T> name;
	
	/**
	 * Create a prop
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The initial value of the prop
	 */
	public EditablePropPrimitive(PropName<EditableProp<T>, T> name, T value) {
		this.value = value;
		this.name = name;
	}

	@Override
	public void setPropMap(PropMap set) {
		if (propMap != null) throw new IllegalArgumentException("Prop '" + this + "' already has its PropMap set to '" + propMap + "'");
		if (set == null) throw new IllegalArgumentException("PropMap must be non-null");
		this.propMap = set;
	}

	@Override
	public void set(T value) {
		this.value = value;
		props().propChanged(new PropEventDefault<T>(this, PropEventOrigin.USER));
	}

	@Override
	public T get() {
		return value;
	}

	@Override
	public Bean getBean() {
		return props().getBean();
	}

	@Override
	public PropMap props() {
		return propMap;
	}

	@Override
	public PropInfo getInfo() {
		return PropInfo.EDITABLE;
	}

	@Override
	public String toString() {
		return "Editable Primitive Prop '" + getName().getString() + "' = '" + get() + "'";
	}

	@Override
	public PropName<EditableProp<T>, T> getName() {
		return name;
	}
	
	//Horrible repeated code to get around some of the horrible effects of type erasure.
	//Basically just an overloaded version of create for each primitive wrapper type,
	//so that when creating primitive props, we don't need to pass in the class,
	//it is done for us
	
	/**
	 * Create a prop
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The initial value of the prop
	 * @return
	 * 		The new prop
	 */
	public static EditablePropPrimitive<Double> create(String name, Double value) {
		return new EditablePropPrimitive<Double>(PropName.editable(name, Double.class), value);
	}

	/**
	 * Create a prop
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The initial value of the prop
	 * @return
	 * 		The new prop
	 */
	public static EditablePropPrimitive<Integer> create(String name, Integer value) {
		return new EditablePropPrimitive<Integer>(PropName.editable(name, Integer.class), value);
	}

	/**
	 * Create a prop
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The initial value of the prop
	 * @return
	 * 		The new prop
	 */
	public static EditablePropPrimitive<String> create(String name, String value) {
		return new EditablePropPrimitive<String>(PropName.editable(name, String.class), value);
	}

	/**
	 * Create a prop
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The initial value of the prop
	 * @return
	 * 		The new prop
	 */
	public static EditablePropPrimitive<Float> create(String name, Float value) {
		return new EditablePropPrimitive<Float>(PropName.editable(name, Float.class), value);
	}

	/**
	 * Create a prop
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The initial value of the prop
	 * @return
	 * 		The new prop
	 */
	public static EditablePropPrimitive<Byte> create(String name, Byte value) {
		return new EditablePropPrimitive<Byte>(PropName.editable(name, Byte.class), value);
	}

	/**
	 * Create a prop
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The initial value of the prop
	 * @return
	 * 		The new prop
	 */
	public static EditablePropPrimitive<Short> create(String name, Short value) {
		return new EditablePropPrimitive<Short>(PropName.editable(name, Short.class), value);
	}

	/**
	 * Create a prop
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The initial value of the prop
	 * @return
	 * 		The new prop
	 */
	public static EditablePropPrimitive<Long> create(String name, Long value) {
		return new EditablePropPrimitive<Long>(PropName.editable(name, Long.class), value);
	}

	/**
	 * Create a prop
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The initial value of the prop
	 * @return
	 * 		The new prop
	 */
	public static EditablePropPrimitive<Boolean> create(String name, Boolean value) {
		return new EditablePropPrimitive<Boolean>(PropName.editable(name, Boolean.class), value);
	}
	
}
