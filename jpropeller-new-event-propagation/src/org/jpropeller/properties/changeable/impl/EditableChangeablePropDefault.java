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

import org.jpropeller.name.PropName;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.changeable.EditableChangeableProp;

/**
 * A {@link EditableProp} which can only have a value which is
 * a {@link Changeable}
 *
 * Implements deep change notification
 *
 * @param <T>
 * 		The type of the {@link Prop} value
 */
public class EditableChangeablePropDefault<T extends Changeable> extends GenericEditableChangeablePropDefault<T> implements EditableChangeableProp<T> {

	PropName<EditableProp<T>, T> propName;
	
	/**
	 * Create a prop
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The initial value of the prop
	 */
	private EditableChangeablePropDefault(PropName<EditableProp<T>, T> name, T value) {
		super(name, value);
		this.propName = name;
	}

	/**
	 * Make a new {@link EditableChangeableProp}
	 * @param <S>
	 * 		The type of {@link Changeable} in the prop
	 * @param name
	 * 		The name of the Prop
	 * @param clazz
	 * 		The class of the Prop 
	 * @param value
	 * 		The initial value of the Prop
	 * @return
	 * 		The Prop
	 */
	public static <S extends Changeable> EditableChangeablePropDefault<S> create(String name, Class<S> clazz, S value) {
		return new EditableChangeablePropDefault<S>(PropName.editable(name, clazz), value);
	}
	
	@Override
	public PropName<EditableProp<T>, T> getName() {
		return propName;
	}
	
}
