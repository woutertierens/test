/*
 *  $Id: Prop.java,v 1.1 2008/03/24 11:19:37 shingoki Exp $
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
import org.jpropeller.name.PropName;

/**
 * A property (prop) holding a single value
 * 
 * This interface allows reading only - the prop must be referenced
 * as an {@link EditableProp} to be written. 
 * 
 * @param <T>
 * 		The type of value in the property
 */
public interface Prop<T> extends GenericProp<T> {

	/**
	 * The name of the prop
	 * This is used in the {@link BeanFeatures} to look up {@link Prop}s via {@link BeanFeatures#get(PropName)}
	 * @return
	 * 		Name of the prop
	 */
	public abstract PropName<? extends Prop<T>, T> getName();
}