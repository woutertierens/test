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
package org.jpropeller.properties.immutable.impl;

import java.awt.Color;

import org.joda.time.DateTime;
import org.jpropeller.info.PropInfo;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Immutable;
import org.jpropeller.properties.change.impl.ChangeDefault;
import org.jpropeller.system.Props;
import org.jpropeller.ui.ImmutableIcon;

/**
 * An {@link EditableProp} which can only have a value of an
 * immutable type, including for example primitive wrappers - that is:
 * 
 * Boolean
 * Short
 * Integer
 * Long
 * String
 * Float
 * Double
 * Color
 * DateTime
 * Immutable
 * ImmutableIcon
 *
 * All these types must all be immutable (and preferably implement {@link Immutable}, although this
 * is impossible for the primitive wrapper types like {@link Double}), so this prop only implements
 * shallow change notification
 *
 * @param <T>
 * 		The type of the {@link Prop} value
 */
public class EditablePropImmutable<T> extends PropImmutable<T> implements EditableProp<T> {

	PropName<EditableProp<T>, T> editableName;
	
	/**
	 * Create a prop
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The initial value of the prop
	 */
	private EditablePropImmutable(PropName<EditableProp<T>, T> name, T value) {
		super(name, value);
		this.editableName = name;
	}

	@Override
	public void set(T value) {
		Props.getPropSystem().getChangeSystem().prepareChange(this);
		
		try {
			this.value = value;
			
			//OPTIMISE should we cache the propagator somehow?
			//Propagate the change we just made
			Props.getPropSystem().getChangeSystem().propagateChange(this, ChangeDefault.instance(
					true,	//Change IS initial 
					false	//Instance is NOT the same - new value set
					));
		} finally {
			Props.getPropSystem().getChangeSystem().concludeChange(this);
		}
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
	public static EditablePropImmutable<Double> create(String name, Double value) {
		return new EditablePropImmutable<Double>(PropName.editable(name, Double.class), value);
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
	public static EditablePropImmutable<Integer> create(String name, Integer value) {
		return new EditablePropImmutable<Integer>(PropName.editable(name, Integer.class), value);
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
	public static EditablePropImmutable<String> create(String name, String value) {
		return new EditablePropImmutable<String>(PropName.editable(name, String.class), value);
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
	public static EditablePropImmutable<Float> create(String name, Float value) {
		return new EditablePropImmutable<Float>(PropName.editable(name, Float.class), value);
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
	public static EditablePropImmutable<Byte> create(String name, Byte value) {
		return new EditablePropImmutable<Byte>(PropName.editable(name, Byte.class), value);
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
	public static EditablePropImmutable<Short> create(String name, Short value) {
		return new EditablePropImmutable<Short>(PropName.editable(name, Short.class), value);
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
	public static EditablePropImmutable<Long> create(String name, Long value) {
		return new EditablePropImmutable<Long>(PropName.editable(name, Long.class), value);
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
	public static EditablePropImmutable<Boolean> create(String name, Boolean value) {
		return new EditablePropImmutable<Boolean>(PropName.editable(name, Boolean.class), value);
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
	public static EditablePropImmutable<ImmutableIcon> create(String name, ImmutableIcon value) {
		return new EditablePropImmutable<ImmutableIcon>(PropName.editable(name, ImmutableIcon.class), value);
	}
	
	/**
	 * Create a prop
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The initial value of the prop
	 * @return
	 * 		The new prop
	 * @param <S>
	 * 		The type of value in the {@link Prop} 
	 */
	public static <S extends Immutable> EditablePropImmutable<S> create(String name, Class<S> clazz, S value) {
		return new EditablePropImmutable<S>(PropName.editable(name, clazz), value);
	}
	
	/**
	 * Create a prop
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The initial value of the prop
	 * @return
	 * 		The new prop
	 * @param <S>
	 * 		The type of value in the {@link Prop} 
	 */
	public static <S extends Enum<S>> EditablePropImmutable<S> create(String name, Class<S> clazz, S value) {
		return new EditablePropImmutable<S>(PropName.editable(name, clazz), value);
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
	public static EditablePropImmutable<Color> create(String name, Color value) {
		return new EditablePropImmutable<Color>(PropName.editable(name, Color.class), value);
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
	public static EditablePropImmutable<DateTime> create(String name, DateTime value) {
		return new EditablePropImmutable<DateTime>(PropName.editable(name, DateTime.class), value);
	}
	
}
