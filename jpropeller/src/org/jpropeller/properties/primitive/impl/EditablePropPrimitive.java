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

import org.jpropeller.info.PropInfo;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.event.PropEventOrigin;
import org.jpropeller.properties.event.impl.PropEventDefault;

/**
 * An {@link EditableProp} which can only have a value of an
 * immutable primitive(ish) type - that is:
 * 
 * Boolean
 * Short
 * Integer
 * Long
 * String
 * Float
 * Double
 *
 * All these types are immutable, so this prop only implements
 * shallow change notification
 *
 * @param <T>
 * 		The type of the {@link Prop} value
 */
public class EditablePropPrimitive<T> extends PropPrimitive<T> implements EditableProp<T> {

	PropName<EditableProp<T>, T> editableName;
	
	/**
	 * Create a prop
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The initial value of the prop
	 */
	public EditablePropPrimitive(PropName<EditableProp<T>, T> name, T value) {
		super(name, value);
		this.editableName = name;
	}

	@Override
	public void set(T value) {
		this.value = value;
		props().propChanged(new PropEventDefault<T>(this, PropEventOrigin.USER));
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
		return editableName;
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
