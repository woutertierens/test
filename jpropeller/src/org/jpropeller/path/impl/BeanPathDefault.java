/*
 *  $Id: BeanPathDefault.java,v 1.1 2008/03/24 11:19:51 shingoki Exp $
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
package org.jpropeller.path.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.path.BeanPath;
import org.jpropeller.path.PathNameList;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.list.ListProp;

/**
 * Simple implementation of BeanPath, using a List as
 * source for iterator
 * @author shingoki
 *
 * @param <P> 
 * 		The type of final prop reached by the path (e.g. {@link Prop},
 * {@link EditableProp}, {@link ListProp} etc.)
 * @param <T>
 * 		The type of data in the final prop reached by the path
 */
public class BeanPathDefault<P extends Prop<T>, T> implements BeanPath<P, T> {

	List<PropName<? extends Prop<? extends Bean>, ? extends Bean>> names;
	PropName<? extends P, T> lastName;
	
	/**
	 * Create a BeanPath
	 * The iterable portion of the path will be taken from the list
	 * @param names
	 * 		The names to iterate in the BeanPath, this will be copied so
	 * that changes will not affect the path (since path is immutable)
	 * @param lastName
	 * 		The last step of the path
	 */
	public BeanPathDefault(List<PropName<? extends Prop<? extends Bean>, ? extends Bean>> names, PropName<? extends P, T> lastName) {
		super();
		this.names = new LinkedList<PropName<? extends Prop<? extends Bean>, ? extends Bean>>(names);
		this.lastName = lastName;
	}
	
	@Override
	public PropName<? extends P, T> getLastName() {
		return lastName;
	}

	@Override
	public Iterator<PropName<? extends Prop<? extends Bean>, ? extends Bean>> iterator() {
		return names.iterator();
	}

	/**
	 * This is a temporary object that is used to make a {@link BeanPathDefault} using
	 * the {@link #to(PropName)} method. It stores the first part of the path - 
	 * the list of {@link PropName}s excluding the last name. This first part of the
	 * path can be grown using {@link #via(PropName)}
	 * 
	 * This class is not useful in itself, only as part of the easy way of making
	 * a {@link BeanPathDefault}:
	 * <pre>
	 * BeanPathDefault.via(a, b, c).then(d)
	 * </pre>
	 * 
	 * @author bwebster
	 *
	 * @param <P> 
	 * 		The type of final prop reached by the path (e.g. {@link Prop},
	 * {@link EditableProp}, {@link ListProp} etc.)
	 * @param <T>
	 * 		The type of data in the final prop reached by the path
	 */
	public static class TemporaryPath<P extends Prop<T>, T> {  
		PathNameList list;
		
		private TemporaryPath(PropName<? extends Prop<? extends Bean>, ? extends Bean> firstName) {
			super();
			list = new PathNameListDefault();
			via(firstName);
		}

		/**
		 * Produce a {@link BeanPathDefault} using the {@link PropName}s used to create this
		 * object, then the last name specified
		 * @param lastName
		 * 		The last {@link PropName} in the path
		 * @return
		 * 		The path itself
		 */
		public BeanPathDefault<P, T> to(PropName<? extends P, T> lastName) {
			return new BeanPathDefault<P, T>(list, lastName);
		}
		
		/**
		 * Add another name to this path, and return this
		 * instance (to allow chaining of calls to {@link #via(PropName)})
		 * @param nextName
		 * 		The next{@link PropName} in the path
		 * @return
		 * 		The path itself
		 */
		public TemporaryPath<P, T> via(PropName<? extends Prop<? extends Bean>, ? extends Bean> nextName) {
			list.add(nextName);
			return this;
		}
		
	}
	
	/**
	 * This returns a {@link TemporaryPath} that can then be used to make a {@link BeanPathDefault} via
	 * {@link TemporaryPath#via(PropName)} and {@link TemporaryPath#to(PropName)}
	 * This sounds complicated - but in practice it is actually much easier - if you want a 
	 * {@link BeanPathDefault} that follows names "a, b, c, d" you just need to call:
	 * <pre>
	 * BeanPathDefault.via(a).via(b).via(c).to(d)
	 * </pre>
	 * @param <P> 
	 * 		The type of final prop reached by the path (e.g. {@link Prop},
	 * {@link EditableProp}, {@link ListProp} etc.)
	 * @param <T>
	 * 		The type of data in the final prop reached by the path
	 * @param firstName
	 * 		The first name in the path
	 * @return
	 * 		A {@link TemporaryPath} that can be used to produce a {@link BeanPathDefault}
	 */
	public static <P extends Prop<T>, T> TemporaryPath<P, T> via(PropName<? extends Prop<? extends Bean>, ? extends Bean> firstName) {
		return new TemporaryPath<P, T>(firstName);
	}
	
	/**
	 * Produce a {@link BeanPathDefault} using a path with only one link
	 * @param <P> 
	 * 		The type of final prop reached by the path (e.g. {@link Prop},
	 * {@link EditableProp}, {@link ListProp} etc.)
	 * @param <T>
	 * 		The type of data in the final prop reached by the path
	 * @param lastName
	 * 		The last {@link PropName} in the path
	 * @return
	 * 		The path itself
	 */
	public static <P extends Prop<T>, T> BeanPathDefault<P, T> to(PropName<? extends P, T> lastName) {
		return new BeanPathDefault<P, T>(new PathNameListDefault(), lastName);
	}

}
