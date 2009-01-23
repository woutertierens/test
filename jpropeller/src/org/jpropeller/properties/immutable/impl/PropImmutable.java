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
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.jpropeller.info.PropInfo;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.ChangeableFeatures;
import org.jpropeller.properties.change.Immutable;
import org.jpropeller.properties.change.impl.ChangeableFeaturesDefault;
import org.jpropeller.properties.change.impl.InternalChangeImplementation;
import org.jpropeller.system.Props;
import org.jpropeller.ui.ImmutableIcon;
import org.jpropeller.util.GeneralUtils;

/**
 * A {@link Prop} which can only have a value of an
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
public class PropImmutable<T> implements Prop<T> {

	private final static Logger logger = GeneralUtils.logger(PropImmutable.class);
	
	T value;
	PropName<? extends Prop<T>, T> name;
	ChangeableFeatures features;
	
	/**
	 * Create a prop
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The initial value of the prop
	 */
	PropImmutable(PropName<? extends Prop<T>, T> name, T value) {
		this.value = value;
		this.name = name;
		features = new ChangeableFeaturesDefault(new InternalChangeImplementation() {
			@Override
			public Change internalChange(Changeable changed, Change change,
					List<Changeable> initial, Map<Changeable, Change> changes) {
				//Nothing to do - we only ever contain a primitive value, and this cannot change, so
				//we never expect to get an internalChange
				logger.warning("UNPOSSIBLE: internalChange called on a PropPrimitive - this should never happen");
				return null;
			}
		}, this); 
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
	public PropName<? extends Prop<T>, T> getName() {
		return name;
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
	public static PropImmutable<Double> create(String name, Double value) {
		return new PropImmutable<Double>(PropName.create(name, Double.class), value);
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
	public static PropImmutable<Integer> create(String name, Integer value) {
		return new PropImmutable<Integer>(PropName.create(name, Integer.class), value);
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
	public static PropImmutable<String> create(String name, String value) {
		return new PropImmutable<String>(PropName.create(name, String.class), value);
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
	public static PropImmutable<Float> create(String name, Float value) {
		return new PropImmutable<Float>(PropName.create(name, Float.class), value);
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
	public static PropImmutable<Byte> create(String name, Byte value) {
		return new PropImmutable<Byte>(PropName.create(name, Byte.class), value);
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
	public static PropImmutable<Short> create(String name, Short value) {
		return new PropImmutable<Short>(PropName.create(name, Short.class), value);
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
	public static PropImmutable<Long> create(String name, Long value) {
		return new PropImmutable<Long>(PropName.create(name, Long.class), value);
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
	public static PropImmutable<Boolean> create(String name, Boolean value) {
		return new PropImmutable<Boolean>(PropName.create(name, Boolean.class), value);
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
	public static PropImmutable<ImmutableIcon> create(String name, ImmutableIcon value) {
		return new PropImmutable<ImmutableIcon>(PropName.create(name, ImmutableIcon.class), value);
	}
	
	/**
	 * Create a prop
	 * @param name
	 * 		The name of the prop
	 * @param clazz 
	 * @param value
	 * 		The initial value of the prop
	 * @return
	 * 		The new prop
	 * @param <S>
	 * 		The type of value in the {@link Prop} 
	 */
	public static <S extends Immutable> PropImmutable<S> create(String name, Class<S> clazz, S value) {
		return new PropImmutable<S>(PropName.create(name, clazz), value);
	}
	
	/**
	 * Create a prop
	 * @param name
	 * 		The name of the prop
	 * @param clazz 
	 * @param value
	 * 		The initial value of the prop
	 * @return
	 * 		The new prop
	 * @param <S>
	 * 		The type of value in the {@link Prop} 
	 */
	public static <S extends Enum<S>> PropImmutable<S> create(String name, Class<S> clazz, S value) {
		return new PropImmutable<S>(PropName.create(name, clazz), value);
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
	public static PropImmutable<Color> create(String name, Color value) {
		return new PropImmutable<Color>(PropName.create(name, Color.class), value);
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
	public static PropImmutable<DateTime> create(String name, DateTime value) {
		return new PropImmutable<DateTime>(PropName.create(name, DateTime.class), value);
	}
	
	
}
