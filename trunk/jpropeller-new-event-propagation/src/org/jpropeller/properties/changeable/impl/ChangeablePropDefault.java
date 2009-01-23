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
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.changeable.ChangeableProp;

/**
 * A {@link Prop} which can only have a value which is
 * a {@link Changeable}
 *
 * Implements deep change notification
 *
 * @param <T>
 * 		The type of the {@link Prop} value
 */
public class ChangeablePropDefault<T extends Changeable> extends GenericChangeablePropDefault<T> implements ChangeableProp<T> {

	PropName<? extends Prop<T>, T> propName;
	
	/**
	 * Create a prop
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The initial value of the prop
	 */
	private ChangeablePropDefault(PropName<? extends Prop<T>, T> name, T value) {
		super(name, value);
		this.propName = name;
	}

	@Override
	public PropName<? extends Prop<T>, T> getName() {
		return propName;
	}
	
}
