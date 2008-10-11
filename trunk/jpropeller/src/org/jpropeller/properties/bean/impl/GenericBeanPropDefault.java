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
package org.jpropeller.properties.bean.impl;

import org.jpropeller.bean.Bean;
import org.jpropeller.info.PropInfo;
import org.jpropeller.map.PropMap;
import org.jpropeller.name.GenericPropName;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.GenericProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.event.PropEvent;
import org.jpropeller.properties.event.PropInternalListener;
import org.jpropeller.properties.event.impl.PropEventDefault;

/**
 * A {@link Prop} which can only have a value which is itself
 * a {@link Bean}
 *
 * Implements deep change notification
 *
 * @param <T>
 * 		The type of the {@link Prop} value
 */
public class GenericBeanPropDefault<T extends Bean> implements GenericProp<T>, PropInternalListener {

	PropMap propMap;
	T value;
	GenericPropName<? extends GenericProp<T>, T> name;
	
	/**
	 * Create a prop
	 * @param name
	 * 		The name of the prop
	 * @param value
	 * 		The initial value of the prop
	 */
	protected GenericBeanPropDefault(GenericPropName<? extends GenericProp<T>, T> name, T value) {
		this.value = value;
		this.name = name;
		
		//Make sure we listen to our initial value
		if (value != null) {
			value.props().addInternalListener(this);
		}		
	}

	/**
	 * Make a new MutablePropBean
	 * @param <S>
	 * 		The type of bean in the prop
	 * @param name
	 * 		The name of the Prop
	 * @param clazz
	 * 		The class of the Prop 
	 * @param value
	 * 		The initial value of the Prop
	 * @return
	 * 		The Prop
	 */
	public static <S extends Bean> GenericBeanPropDefault<S> create(String name, Class<S> clazz, S value) {
		return new GenericBeanPropDefault<S>(PropName.create(name, clazz), value);
	}

	@Override
	public <S> void propInternalChanged(PropEvent<S> event) {
		//We need to fire the change on as a deep change
		props().propChanged(new PropEventDefault<T>(this, event));
	}
	
	@Override
	public void setPropMap(PropMap map) {
		if (propMap != null) throw new IllegalArgumentException("Prop '" + this + "' already has its PropMap set to '" + propMap + "'");
		if (map == null) throw new IllegalArgumentException("PropMap must be non-null");
		this.propMap = map;
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
	public GenericPropName<? extends GenericProp<T>, T> getName() {
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
		return "Bean Prop '" + getName().getString() + "' = '" + get() + "'";
	}
	
}
