/*
 *  $Id: PropName.java,v 1.1 2008/03/24 11:19:38 shingoki Exp $
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
package org.jpropeller.name;

import org.jpropeller.bean.BeanFeatures;
import org.jpropeller.info.PropInfo;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.list.EditableListProp;
import org.jpropeller.properties.list.ListProp;
import org.jpropeller.properties.map.EditableMapProp;
import org.jpropeller.properties.map.MapProp;
import org.jpropeller.properties.set.EditableSetProp;

/**
 * The name of a Prop&lt;T&gt;
 *
 * <br />
 * <br />
 * 
 * Immutable
 * 
 * <br />
 * <br />
 * 
 * This is a stricter version of {@link GenericPropName}. It must have T as a 
 * non-generic type, and so {@link #isTGeneric()} always returns false, and
 * {@link #getPropParametricTypes()} always returns the empty string "". As a result,
 * the {@link Class} returned by {@link #getPropClass()} exactly matches the
 * type of T (that is, it is accepted as being an instance of Class&lt;T&gt;). This
 * means that when {@link Prop}s are looked up from a {@link BeanFeatures} using 
 * a {@link PropName}, it is guaranteed that the value in the prop is exactly of
 * type T, preserving type safety.
 * 
 * <br />
 * <br />
 * 
 * Used to look up {@link Prop}s in a {@link BeanFeatures}, and provided by {@link Prop}s
 * as a useful string name
 * 
 * <br />
 * <br />
 * 
 * One feature of the {@link PropName} as a final class is that the parametric type
 * P and the {@link PropInfo} ALWAYS match up. In addition, the parametric type T
 * and the return of {@link #getPropClass()} ALWAYS match up.
 * 
 * <br />
 * <br />
 * 
 * Hence if two {@link PropName}s are equal, they must have equal parametric type 
 * P and T also. This allows {@link PropName}s to be used as keys to look up 
 * {@link Prop} instances and allow casting to generic types safely. 
 * 
 * @param <P>
 * 		The type of {@link Prop} to which this {@link PropName} belongs, e.g.
 * {@link Prop}, {@link EditableProp} etc.
 *
 * @param <T>
 * 		The parametric type of {@link Prop} this {@link PropName} can belong to,
 * this is the type of data in the {@link Prop} itself
 */
public final class PropName<P extends GeneralProp<T>, T> extends GenericPropName<P, T>{
	
	private final static String EMPTY_STRING = "";
	
	String s;
	PropInfo info;
	
	/**
	 * Create a name
	 * @param s
	 * 		The string for the name
	 * @param clazz
	 * 		The class for the parametric type of Prop 
	 * named by this instance
	 */
	private PropName (String s, Class<T> clazz, PropInfo info) {
		super(s, clazz, false, EMPTY_STRING, info);
	}

	/**
	 * Return the prop class as the more specific type, Class<T>
	 * @return
	 * 		The parametric class of the {@link Prop}
	 */
	@SuppressWarnings("unchecked")	//We know clazz is a Class<T> since we require that on our only constructor
	@Override
	public Class<T> getPropClass() {
		return (Class<T>)clazz;
	}

	/**
	 * Make a {@link PropName}
	 * @param <T>
	 * 		Type of value in the {@link Prop}
	 * @param s
	 * 		String value of name
	 * @param clazz
	 * 		Class of value in the {@link Prop}
	 * @return
	 * 		A new name
	 */
	public static <T> PropName<EditableProp<T>, T> editable(String s, Class<T> clazz) {
		return new PropName<EditableProp<T>, T>(s, clazz, PropInfo.EDITABLE);
	}

	/**
	 * Make a {@link PropName}
	 * @param <T>
	 * 		Type of value in the {@link Prop}
	 * @param s
	 * 		String value of name
	 * @param clazz
	 * 		Class of value in the {@link Prop}
	 * @return
	 * 		A new name
	 */
	public static <T> PropName<Prop<T>, T> create(String s, Class<T> clazz) {
		return new PropName<Prop<T>, T>(s, clazz, PropInfo.DEFAULT);
	}

	/**
	 * Make a {@link PropName}
	 * @param <T>
	 * 		Type of value in the {@link Prop}
	 * @param s
	 * 		String value of name
	 * @param clazz
	 * 		Class of value in the {@link Prop}
	 * @return
	 * 		A new name
	 */
	public static <T> PropName<EditableListProp<T>, T> editableList(String s, Class<T> clazz) {
		return new PropName<EditableListProp<T>, T>(s, clazz, PropInfo.EDITABLE_LIST);
	}

	/**
	 * Make a {@link PropName}
	 * @param <T>
	 * 		Type of value in the {@link Prop}
	 * @param s
	 * 		String value of name
	 * @param clazz
	 * 		Class of value in the {@link Prop}
	 * @return
	 * 		A new name
	 */
	public static <T> PropName<EditableSetProp<T>, T> editableSet(String s, Class<T> clazz) {
		return new PropName<EditableSetProp<T>, T>(s, clazz, PropInfo.EDITABLE_SET);
	}

	/**
	 * Make a {@link PropName}
	 * @param <T>
	 * 		Type of value in the {@link Prop}
	 * @param s
	 * 		String value of name
	 * @param clazz
	 * 		Class of value in the {@link Prop}
	 * @return
	 * 		A new name
	 */
	public static <T> PropName<ListProp<T>, T> createList(String s, Class<T> clazz) {
		return new PropName<ListProp<T>, T>(s, clazz, PropInfo.DEFAULT_LIST);
	}

	/**
	 * Make a {@link PropName}
	 * @param <K> 
	 * 		Type of key for the {@link MapProp}
	 * @param <T>
	 * 		Type of value in the {@link MapProp}
	 * @param s
	 * 		String value of name
	 * @param clazz
	 * 		Class of value in the {@link Prop}
	 * @return
	 * 		A new name
	 */
	public static <K, T> PropName<EditableMapProp<K, T>, T> editableMap(String s, Class<T> clazz) {
		return new PropName<EditableMapProp<K, T>, T>(s, clazz, PropInfo.EDITABLE_MAP);
	}

	/**
	 * Make a {@link PropName}
	 * @param <K> 
	 * 		Type of key for the {@link MapProp}
	 * @param <T>
	 * 		Type of value in the {@link Prop}
	 * @param s
	 * 		String value of name
	 * @param clazz
	 * 		Class of value in the {@link Prop}
	 * @return
	 * 		A new name
	 */
	public static <K, T> PropName<MapProp<K, T>, T> createMap(String s, Class<T> clazz) {
		return new PropName<MapProp<K, T>, T>(s, clazz, PropInfo.DEFAULT_MAP);
	}
}
