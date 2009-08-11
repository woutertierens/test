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
import org.jpropeller.collection.CList;
import org.jpropeller.collection.CMap;
import org.jpropeller.collection.CSet;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.map.MapProp;

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
 * Used to look up {@link Prop}s in a {@link BeanFeatures}, and provided by {@link Prop}s
 * as a useful string name
 * 
 * <br />
 * <br />
 * 
 * One feature of the {@link PropName} as a final class is that the parametric type T
 * and the return of {@link #getPropClass()} ALWAYS match up, with the exception of the
 * awkward case where T is itself a generic type (e.g. List&lt;String&gt;). In this case, the
 * return of {@link #getPropClass()} CANNOT match up, since there is no {@link Class}
 * specific to List&lt;String&gt; - there is just List.class that applies to List with any
 * parametric type.
 * 
 * <br />
 * <br />
 * 
 * This class deals with the case of T being a generic type in the best practical way - 
 * it makes it known that the class is parametric via the method {@link #isTGeneric()}
 * returning true. The class returned by {@link #getPropClass()} is then null, and 
 * {@link #getGenericPropClass()} is used instead, but obviously does not contain any 
 * details of the parametric type. In addition, a user-defined string is provided 
 * which can be used to distinguish the type parameters of T. It is recommended that 
 * this is used with the normal Java syntax, so for example if T is List&lt;String&gt;, 
 * {@link #getGenericPropClass()} should return List.class, and {@link #getPropParametricTypes()}
 * should return "&lt;String&gt;". Note that the parametric type string has whitespace stripped
 * so that e.g. "&lt;String, Integer&gt;" and "&lt;String,Integer &gt;" are equivalent.  
 * 
 * <br />
 * <br />
 * Some methods in JPropeller can only be called using a {@link PropName}, where
 * {@link #isTGeneric()} returns false, since they require an exactly
 * matching class to guarantee type safety. However other methods do not require this, and
 * so may be used with any {@link PropName}. Exceptions (generally runtime) are thrown
 * where generic names are not accepted.
 * 
 * If two {@link PropName}s are equal, they must have equal parametric type 
 * P and T also, except that if T is generic, it may have a different generic type - 
 * in this case the parametric type string is used to check equality. 
 * This allows {@link PropName}s to be used as keys to look up 
 * {@link Prop} instances and allow (most) casting to generic types safely. 
 * 
 * @param <T>
 * 		The parametric type of {@link Prop} this {@link PropName} can belong to,
 * this is the type of data in the {@link Prop} itself
 */
public class PropName<T> {
	
	private final static String EMPTY_STRING = "";
	
	private String s;
	private Class<T> clazz;
	private Class<?> genericClazz;
	private String propParametricTypes;
	private int hashCode;
	private boolean tGeneric;
	
	/**
	 * Construct a name using all fields
	 * @param s
	 * 		The string for the name
	 * @param clazz
	 * 		The class of value, IF the name is for a non-generic value type, null
	 * 		otherwise. 
	 * named by this instance
	 * @param genericClazz
	 * 		The class of value, IF the name is for a generic value type, null
	 * 		otherwise
	 * @param propParametricTypes
	 * 		A string description of parametric types of the prop value. This
	 * will be compared to (and must match exactly in a string sense) the
	 * parametric types of other names with generic T types. Note that the
	 * string provided will have whitespace stripped before use.
	 * An example is if T is List&lt;String&gt;, propParametricTypes should
	 * be "&lt;String&gt;"
	 * @param generic
	 * 		True if T is a generic type
	 */
	private PropName(String s, Class<T> clazz, Class<?> genericClazz,
			String propParametricTypes, boolean generic) {
		super();
		this.s = s;
		this.clazz = clazz;
		this.genericClazz = genericClazz;
		this.propParametricTypes = propParametricTypes;
		tGeneric = generic;
		calcHash();
	}

	/**
	 * Create a name
	 * @param s
	 * 		The string for the name
	 * @param clazz
	 * 		The class for the parametric type of Prop 
	 * named by this instance
	 * @param tGeneric
	 * 		True if T is a generic type
	 * @param propParametricTypes
	 * 		A string description of parametric types of the prop value. This
	 * will be compared to (and must match exactly in a string sense) the
	 * parametric types of other names with generic T types. Note that the
	 * string provided will have whitespace stripped before use.
	 * An example is if T is List&lt;String&gt;, propParametricTypes should
	 * be "&lt;String&gt;"
	 */
	protected PropName(String s, Class<T> clazz) {
		this.s = s;
		this.clazz = clazz;
		this.genericClazz = null;
		this.tGeneric = false;
		this.propParametricTypes = EMPTY_STRING;

		//precalculate our immutable hash
		calcHash();

	}

	/**
	 * Make a new {@link PropName} that is the same as this {@link PropName}, except that
	 * it has a new string name
	 * @param newName		The new string name
	 * @return				New {@link PropName}
	 */
	public PropName<T> renamed(String newName) {
		return new PropName<T>(newName, clazz, genericClazz, propParametricTypes, tGeneric);
	}
	
	private void calcHash() {
		hashCode = 
			getString().hashCode() * 31 + 
			(getPropClass() == null ? 1 : getPropClass().hashCode()) + 
			(getGenericPropClass() == null ? 1 : getGenericPropClass().hashCode() * 7) + 
			(isTGeneric() ? 17 : 1) + 
			getPropParametricTypes().hashCode() * 5;
	}
	
	/**
	 * Create a name
	 * @param s
	 * 		The string for the name
	 * @param clazz
	 * 		The class for the parametric type of Prop 
	 * named by this instance
	 * @param tGeneric
	 * 		True if T is a generic type
	 * @param propParametricTypes
	 * 		A string description of parametric types of the prop value. This
	 * will be compared to (and must match exactly in a string sense) the
	 * parametric types of other names with generic T types. Note that the
	 * string provided will have whitespace stripped before use.
	 * An example is if T is List&lt;String&gt;, propParametricTypes should
	 * be "&lt;String&gt;"
	 */
	protected PropName (String s, Class<?> clazz, boolean tGeneric, String propParametricTypes) {
		this.s = s;
		this.clazz = null;
		this.genericClazz = clazz;
		this.tGeneric = tGeneric;
		this.propParametricTypes = propParametricTypes;

		//precalculate our immutable hash
		calcHash();
	}
	
	/**
	 * Get the string value of the name
	 * @return
	 * 		The string value of the name
	 */
	public String getString() {
		return s;
	}
	
	/**
	 * Returns true if the type T (the type of the data in
	 * named props) is generic. If this is the case, we cannot
	 * assume that the class returned by {@link #getClass()}
	 * completely describes the type of value returned by
	 * props. In this case, we only know the normal type/class
	 * of the value, not the parametric type. In some cases this
	 * will be adequate.
	 * @return
	 * 		True if type T is generic, false otherwise
	 */
	public boolean isTGeneric() {
		return tGeneric;
	}

	/**
	 * This must return "" if T is not generic (i.e. if {@link #isTGeneric()}
	 * returns false)
	 * A string description of parametric types of the prop value. This
	 * will be compared to (and must match exactly in a string sense) the
	 * parametric types of other names with generic T types. Note that the
	 * string provided will have had all whitespace stripped.
	 * @return
	 * 		A string description of the parametric types of T
	 */
	public String getPropParametricTypes() {
		return propParametricTypes;
	}

	/**
	 * Get the value class of the Prop - null if the
	 * value class is generic - in this case use
	 * {@link #getGenericPropClass()} and
	 * {@link #getPropParametricTypes()}
	 * @return
	 * 		value class
	 */
	public Class<T> getPropClass() {
		return clazz;
	}

	/**
	 * Get the value class of the Prop - null if the
	 * value class is NOT generic - in this case use
	 * {@link #getPropClass()}
	 * @return
	 * 		generic value class
	 */
	public Class<?> getGenericPropClass() {
		return genericClazz;
	}

	@Override
	public String toString() {
		return s;
	}

	@Override
	/**
	 * A PropName is only equal to another PropName with equal getString(),
	 * getPropClass(), isTGeneric() and getPropParametricTypes() values
	 */
	public boolean equals(Object obj) {
		
		//We are equal to ourself
		if (this == obj) return true;
		
		//We are only equal if hashcodes match
		if (hashCode != obj.hashCode()) return false;
		
		//We are only equal to other PropNames with all data matching
		if (obj instanceof PropName<?>) {
			PropName<?> other = (PropName<?>) obj;
			return 	(
						(other.getString().equals(getString())) && 
						(equals(other.getPropClass(), getPropClass())) && 
						(equals(other.getGenericPropClass(), getGenericPropClass())) && 
						(other.isTGeneric() == isTGeneric()) &&
						(other.getPropParametricTypes().equals(getPropParametricTypes()))
					);
			
		//Not equal to other classes
		} else {
			return false;
		}
	}
	
	private static boolean equals(Object a, Object b) {
		if (a == null) {
			return (b == null);
		} else {
			return a.equals(b);
		}
	}

	@Override
	public int hashCode() {
		return hashCode; 
	}
	
	/**
	 * Make a {@link PropName} for a non-generic value type
	 * @param <T>
	 * 		Type of value in the {@link Prop}
	 * @param s
	 * 		String value of name
	 * @param clazz
	 * 		Class of value in the {@link Prop}
	 * @return
	 * 		A new name
	 */
	public static <T> PropName<T> create(String s, Class<T> clazz) {
		return new PropName<T>(s, clazz);
	}

	/**
	 * Make a {@link PropName}
	 * @param <L>
	 * 		Type of value in the lists held by the {@link Prop} 
	 * @param s
	 * 		String value of name
	 * @param contentsClass
	 * 		Class of value in the list in {@link Prop}
	 * @return
	 * 		A new name
	 */
	public static <L> PropName<CList<L>> createList(String s, Class<L> contentsClass) {
		//TODO make this a propname implementation where the contents class is held separately
		return new PropName<CList<L>>(
				s, 
				CList.class, 
				true, 
				"<" + contentsClass.getCanonicalName() + ">");
	}

	/**
	 * Make a {@link PropName}
	 * @param <T>
	 * 		Type of value in the {@link Prop}
	 * @param s
	 * 		String value of name
	 * @param clazz
	 * 		Class of value in the {@link CSet}
	 * @return
	 * 		A new name
	 */
	public static <T> PropName<CSet<T>> createSet(String s, Class<T> clazz) {
		//TODO make this a propname implementation where the contents class is held separately
		return new PropName<CSet<T>>(
				s,
				CSet.class,
				true,
				"<" + clazz.getCanonicalName() + ">");
	}
	
	/**
	 * Make a {@link PropName}
	 * @param <K> 
	 * 		Type of key for the {@link MapProp}
	 * @param <T>
	 * 		Type of value in the {@link Prop}
	 * @param s
	 * 		String value of name
	 * @param keyClass
	 * 		Class of key in the {@link Prop}
	 * @param valueClass
	 * 		Class of value in the {@link Prop}
	 * @return
	 * 		A new name
	 */
	public static <K, T> PropName<CMap<K, T>> createMap(String s, Class<K> keyClass, Class<T> valueClass) {
		//TODO make this a propname implementation where the contents classes are held separately
		return new PropName<CMap<K,T>>(
				s, 
				CMap.class, 
				true, 
				"<" + keyClass.getCanonicalName() + "," + valueClass.getCanonicalName()+">");
	}

}
