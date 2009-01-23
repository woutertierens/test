/*
 *  $Id: MutableProp.java,v 1.1 2008/03/24 11:19:35 shingoki Exp $
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
package org.jpropeller.properties;

import org.jpropeller.bean.BeanFeatures;
import org.jpropeller.info.PropEditability;
import org.jpropeller.name.GenericPropName;
import org.jpropeller.name.PropName;

/**
 * A property that can be edited, using {@link GenericEditableProp#set(Object)}
 * This must have {@link PropEditability} as {@link PropEditability#EDITABLE}
 * 
 * @author shingoki
 *
 * @param <T>
 * 		The type of value in the prop
 */
public interface GenericEditableProp<T> extends GenericProp<T> {

	/**
	 * The name of the prop
	 * This is used in the {@link BeanFeatures} to look up {@link Prop}s via {@link BeanFeatures#get(PropName)}
	 * @return
	 * 		Name of the prop
	 */
	public GenericPropName<? extends GenericEditableProp<T>, T> getName();
	
	/**
	 * Set the prop value
	 * @param value
	 * 		The new value of the prop
	 */
	public void set(T value);
	
}
