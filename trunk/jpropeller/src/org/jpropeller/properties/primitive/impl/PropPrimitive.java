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
import org.jpropeller.properties.Prop;

/**
 * A {@link Prop} which can only have a value of an
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
public class PropPrimitive<T> implements Prop<T> {

	PropMap propMap;
	T value;
	PropName<? extends Prop<T>, T> name;
	
	/**
	 * Create a prop
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The initial value of the prop
	 */
	public PropPrimitive(PropName<? extends Prop<T>, T> name, T value) {
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
	public T get() {
		return value;
	}

	@Override
	public Bean getBean() {
		return props().getBean();
	}

	@Override
	public PropName<? extends Prop<T>, T> getName() {
		return name;
	}

	@Override
	public PropMap props() {
		return propMap;
	}

	@Override
	public PropInfo getInfo() {
		return PropInfo.DEFAULT;
	}
	
	@Override
	public String toString() {
		return "Primitive Prop '" + getName().getString() + "' = '" + get() + "'";
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
	public static PropPrimitive<Double> create(String name, Double value) {
		return new PropPrimitive<Double>(PropName.create(name, Double.class), value);
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
	public static PropPrimitive<Integer> create(String name, Integer value) {
		return new PropPrimitive<Integer>(PropName.create(name, Integer.class), value);
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
	public static PropPrimitive<String> create(String name, String value) {
		return new PropPrimitive<String>(PropName.create(name, String.class), value);
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
	public static PropPrimitive<Float> create(String name, Float value) {
		return new PropPrimitive<Float>(PropName.create(name, Float.class), value);
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
	public static PropPrimitive<Byte> create(String name, Byte value) {
		return new PropPrimitive<Byte>(PropName.create(name, Byte.class), value);
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
	public static PropPrimitive<Short> create(String name, Short value) {
		return new PropPrimitive<Short>(PropName.create(name, Short.class), value);
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
	public static PropPrimitive<Long> create(String name, Long value) {
		return new PropPrimitive<Long>(PropName.create(name, Long.class), value);
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
	public static PropPrimitive<Boolean> create(String name, Boolean value) {
		return new PropPrimitive<Boolean>(PropName.create(name, Boolean.class), value);
	}
	
}
