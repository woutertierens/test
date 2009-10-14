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
import org.jpropeller.info.PropAccessType;
import org.jpropeller.info.PropEditability;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.ChangeableFeatures;
import org.jpropeller.properties.change.Immutable;
import org.jpropeller.properties.change.impl.ChangeDefault;
import org.jpropeller.properties.change.impl.ChangeableFeaturesDefault;
import org.jpropeller.properties.change.impl.InternalChangeImplementation;
import org.jpropeller.properties.values.ValueProcessor;
import org.jpropeller.properties.values.impl.AcceptProcessor;
import org.jpropeller.properties.values.impl.ReadOnlyProcessor;
import org.jpropeller.system.Props;
import org.jpropeller.ui.impl.ImmutableIcon;
import org.jpropeller.util.GeneralUtils;

/**
 * A {@link Prop} which can only have a value of an
 * immutable type, including for example primitive wrappers - that is:
 * 
 * 
 * String
 * 
 * Boolean
 * 
 * Byte
 * Short
 * Integer
 * Long
 * 
 * Float
 * Double
 * 
 * Color
 * 
 * DateTime
 * 
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
	PropName<T> name;
	ChangeableFeatures features;
	ValueProcessor<T> processor;
	
	/**
	 * Create a prop
	 * @param name * String

	 * 		The name of the prop
	 * @param value
	 * 		The initial value of the prop
	 */
	PropImmutable(PropName<T> name, T value, ValueProcessor<T> processor) {
		this.value = value;
		this.name = name;
		this.processor = processor;
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
	public void set(T rawNewValue) {
		
		//This will throw exceptions on failure as desired
		T value = processor.process(rawNewValue);
		
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
		return "Prop containing immutable, '" + getName().getString() + "' = '" + get() + "'";
	}

	//Horrible repeated code to get around some of the horrible effects of type erasure.
	//Basically just an overloaded version of create for each primitive wrapper type,
	//so that when creating primitive props, we don't need to pass in the class,
	//it is done for us
	
	/**
	 * Create a prop
	 * @param clazz 
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The initial value of the prop
	 * @return
	 * 		The new prop
	 * @param <S>
	 * 		The type of value in the {@link Prop} 
	 */
	public static <S extends Immutable> PropImmutable<S> create(Class<S> clazz, String name, S value) {
		return new PropImmutable<S>(PropName.create(clazz, name), value, ReadOnlyProcessor.<S>get());
	}

	/**
	 * Create a prop
	 * @param clazz 
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The initial value of the prop
	 * @return
	 * 		The new prop
	 * @param <S>
	 * 		The type of value in the {@link Prop} 
	 */
	public static <S extends Enum<S>> PropImmutable<S> create(Class<S> clazz, String name, S value) {
		return new PropImmutable<S>(PropName.create(clazz, name), value, ReadOnlyProcessor.<S>get());
	}
	
	
	/**
	 * Create a prop
	 * @param clazz
	 * 		The {@link Class} of {@link Enum} 
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The initial value of the prop
	 * @return
	 * 		The new prop
	 * @param <S>
	 * 		The type of value in the {@link Prop} 
	 */
	public static <S extends Immutable> PropImmutable<S> editable(Class<S> clazz, String name, S value) {
		return new PropImmutable<S>(PropName.create(clazz, name), value, AcceptProcessor.<S>get());
	}
	
	/**
	 * Create a prop
	 * @param clazz
	 * 		The {@link Class} of {@link Enum} 
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The initial value of the prop
	 * @return
	 * 		The new prop
	 * @param <S>
	 * 		The type of value in the {@link Prop} 
	 */
	public static <S extends Enum<S>> PropImmutable<S> editable(Class<S> clazz, String name, S value) {
		return new PropImmutable<S>(PropName.create(clazz, name), value, AcceptProcessor.<S>get());
	}
	

	
	
	//#########################################################################
	//###																	###
	//###  Auto-generated factory methods for Props with immutable content	###
	//###																	###
	//#########################################################################
	
	/**
	 * Make a new read-only String {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static PropImmutable<String> create(String name, String value){
		return new PropImmutable<String>(PropName.create(String.class, name), value, ReadOnlyProcessor.<String>get());
	}
	/**
	 * Make a new String {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @param processor	The processor used when new values are set
	 * @return			The new {@link Prop}
	 */
	public final static PropImmutable<String> create(String name, String value, ValueProcessor<String> processor){
		return new PropImmutable<String>(PropName.create(String.class, name), value, processor);
	}
	/**
	 * Make a new editable String {@link Prop}, accepting all values
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static PropImmutable<String> editable(String name, String value){
		return new PropImmutable<String>(PropName.create(String.class, name), value, AcceptProcessor.<String>get());
	}
	/**
	 * Make a new read-only Boolean {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static PropImmutable<Boolean> create(String name, Boolean value){
		return new PropImmutable<Boolean>(PropName.create(Boolean.class, name), value, ReadOnlyProcessor.<Boolean>get());
	}
	/**
	 * Make a new Boolean {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @param processor	The processor used when new values are set
	 * @return			The new {@link Prop}
	 */
	public final static PropImmutable<Boolean> create(String name, Boolean value, ValueProcessor<Boolean> processor){
		return new PropImmutable<Boolean>(PropName.create(Boolean.class, name), value, processor);
	}
	/**
	 * Make a new editable Boolean {@link Prop}, accepting all values
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static PropImmutable<Boolean> editable(String name, Boolean value){
		return new PropImmutable<Boolean>(PropName.create(Boolean.class, name), value, AcceptProcessor.<Boolean>get());
	}

	/**
	 * Create a prop
	 * @param clazz 
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The initial value of the prop
	 * @return
	 * 		The new prop
	 * @param <S>
	 * 		The type of value in the {@link Prop} 
	 */
	public static <S> PropImmutable<S> create(Class<S> clazz, String name, S value) {
		return new PropImmutable<S>(PropName.create(clazz, name), value, ReadOnlyProcessor.<S>get());
	}
	
	/**
	 * Make a new read-only Byte {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static PropImmutable<Byte> create(String name, Byte value){
		return new PropImmutable<Byte>(PropName.create(Byte.class, name), value, ReadOnlyProcessor.<Byte>get());
	}
	/**
	 * Make a new Byte {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @param processor	The processor used when new values are set
	 * @return			The new {@link Prop}
	 */
	public final static PropImmutable<Byte> create(String name, Byte value, ValueProcessor<Byte> processor){
		return new PropImmutable<Byte>(PropName.create(Byte.class, name), value, processor);
	}
	/**
	 * Make a new editable Byte {@link Prop}, accepting all values
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static PropImmutable<Byte> editable(String name, Byte value){
		return new PropImmutable<Byte>(PropName.create(Byte.class, name), value, AcceptProcessor.<Byte>get());
	}
	/**
	 * Make a new read-only Short {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static PropImmutable<Short> create(String name, Short value){
		return new PropImmutable<Short>(PropName.create(Short.class, name), value, ReadOnlyProcessor.<Short>get());
	}
	/**
	 * Make a new Short {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @param processor	The processor used when new values are set
	 * @return			The new {@link Prop}
	 */
	public final static PropImmutable<Short> create(String name, Short value, ValueProcessor<Short> processor){
		return new PropImmutable<Short>(PropName.create(Short.class, name), value, processor);
	}
	/**
	 * Make a new editable Short {@link Prop}, accepting all values
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static PropImmutable<Short> editable(String name, Short value){
		return new PropImmutable<Short>(PropName.create(Short.class, name), value, AcceptProcessor.<Short>get());
	}
	/**
	 * Make a new read-only Integer {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static PropImmutable<Integer> create(String name, Integer value){
		return new PropImmutable<Integer>(PropName.create(Integer.class, name), value, ReadOnlyProcessor.<Integer>get());
	}
	/**
	 * Make a new Integer {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @param processor	The processor used when new values are set
	 * @return			The new {@link Prop}
	 */
	public final static PropImmutable<Integer> create(String name, Integer value, ValueProcessor<Integer> processor){
		return new PropImmutable<Integer>(PropName.create(Integer.class, name), value, processor);
	}
	/**
	 * Make a new editable Integer {@link Prop}, accepting all values
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static PropImmutable<Integer> editable(String name, Integer value){
		return new PropImmutable<Integer>(PropName.create(Integer.class, name), value, AcceptProcessor.<Integer>get());
	}
	/**
	 * Make a new read-only Long {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static PropImmutable<Long> create(String name, Long value){
		return new PropImmutable<Long>(PropName.create(Long.class, name), value, ReadOnlyProcessor.<Long>get());
	}
	/**
	 * Make a new Long {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @param processor	The processor used when new values are set
	 * @return			The new {@link Prop}
	 */
	public final static PropImmutable<Long> create(String name, Long value, ValueProcessor<Long> processor){
		return new PropImmutable<Long>(PropName.create(Long.class, name), value, processor);
	}
	/**
	 * Make a new editable Long {@link Prop}, accepting all values
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static PropImmutable<Long> editable(String name, Long value){
		return new PropImmutable<Long>(PropName.create(Long.class, name), value, AcceptProcessor.<Long>get());
	}
	/**
	 * Make a new read-only Float {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static PropImmutable<Float> create(String name, Float value){
		return new PropImmutable<Float>(PropName.create(Float.class, name), value, ReadOnlyProcessor.<Float>get());
	}
	/**
	 * Make a new Float {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @param processor	The processor used when new values are set
	 * @return			The new {@link Prop}
	 */
	public final static PropImmutable<Float> create(String name, Float value, ValueProcessor<Float> processor){
		return new PropImmutable<Float>(PropName.create(Float.class, name), value, processor);
	}
	/**
	 * Make a new editable Float {@link Prop}, accepting all values
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static PropImmutable<Float> editable(String name, Float value){
		return new PropImmutable<Float>(PropName.create(Float.class, name), value, AcceptProcessor.<Float>get());
	}
	/**
	 * Make a new read-only Double {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static PropImmutable<Double> create(String name, Double value){
		return new PropImmutable<Double>(PropName.create(Double.class, name), value, ReadOnlyProcessor.<Double>get());
	}
	/**
	 * Make a new Double {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @param processor	The processor used when new values are set
	 * @return			The new {@link Prop}
	 */
	public final static PropImmutable<Double> create(String name, Double value, ValueProcessor<Double> processor){
		return new PropImmutable<Double>(PropName.create(Double.class, name), value, processor);
	}
	/**
	 * Make a new editable Double {@link Prop}, accepting all values
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static PropImmutable<Double> editable(String name, Double value){
		return new PropImmutable<Double>(PropName.create(Double.class, name), value, AcceptProcessor.<Double>get());
	}
	/**
	 * Make a new read-only DateTime {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static PropImmutable<DateTime> create(String name, DateTime value){
		return new PropImmutable<DateTime>(PropName.create(DateTime.class, name), value, ReadOnlyProcessor.<DateTime>get());
	}
	/**
	 * Make a new DateTime {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @param processor	The processor used when new values are set
	 * @return			The new {@link Prop}
	 */
	public final static PropImmutable<DateTime> create(String name, DateTime value, ValueProcessor<DateTime> processor){
		return new PropImmutable<DateTime>(PropName.create(DateTime.class, name), value, processor);
	}
	/**
	 * Make a new editable DateTime {@link Prop}, accepting all values
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static PropImmutable<DateTime> editable(String name, DateTime value){
		return new PropImmutable<DateTime>(PropName.create(DateTime.class, name), value, AcceptProcessor.<DateTime>get());
	}
	/**
	 * Make a new read-only Color {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static PropImmutable<Color> create(String name, Color value){
		return new PropImmutable<Color>(PropName.create(Color.class, name), value, ReadOnlyProcessor.<Color>get());
	}
	/**
	 * Make a new Color {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @param processor	The processor used when new values are set
	 * @return			The new {@link Prop}
	 */
	public final static PropImmutable<Color> create(String name, Color value, ValueProcessor<Color> processor){
		return new PropImmutable<Color>(PropName.create(Color.class, name), value, processor);
	}
	/**
	 * Create a prop
	 * @param clazz
	 * 		The {@link Class}. 
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The initial value of the prop
	 * @return
	 * 		The new prop
	 * @param <S>
	 * 		The type of value in the {@link Prop} 
	 */
	public static <S> PropImmutable<S> editable(Class<S> clazz, String name, S value) {
		return new PropImmutable<S>(PropName.create(clazz, name), value, AcceptProcessor.<S>get());
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
	public final static PropImmutable<Color> editable(String name, Color value){
		return new PropImmutable<Color>(PropName.create(Color.class, name), value, AcceptProcessor.<Color>get());
	}
	
	
	/**
	 * Make a new read-only ImmutableIcon {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static PropImmutable<ImmutableIcon> create(String name, ImmutableIcon value){
		return new PropImmutable<ImmutableIcon>(PropName.create(ImmutableIcon.class, name), value, ReadOnlyProcessor.<ImmutableIcon>get());
	}
	/**
	 * Make a new ImmutableIcon {@link Prop}
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @param processor	The processor used when new values are set
	 * @return			The new {@link Prop}
	 */
	public final static PropImmutable<ImmutableIcon> create(String name, ImmutableIcon value, ValueProcessor<ImmutableIcon> processor){
		return new PropImmutable<ImmutableIcon>(PropName.create(ImmutableIcon.class, name), value, processor);
	}
	/**
	 * Make a new editable ImmutableIcon {@link Prop}, accepting all values
	 * @param name		The name of the {@link Prop}
	 * @param value		The value of the {@link Prop}
	 * @return			The new {@link Prop}
	 */
	public final static PropImmutable<ImmutableIcon> editable(String name, ImmutableIcon value){
		return new PropImmutable<ImmutableIcon>(PropName.create(ImmutableIcon.class, name), value, AcceptProcessor.<ImmutableIcon>get());
	}


}
