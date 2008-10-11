/*
 *  $Id: BeanPath.java,v 1.1 2008/03/24 11:19:36 shingoki Exp $
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
package org.jpropeller.path;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.list.ListProp;

/**
 * A BeanPath allows for looking up of a Prop by following a series
 * of named properties through a chain of beans.
 * 
 * For example, we might have Bean classes Person, Address, Street
 * where a person has an address property, an address has a street
 * property, and finally a street has a name property (which has type
 * String)
 * We want to specify a path that finds the name of a person's street.
 * In this case we have a Path that follows properties named:
 * 
 * "address", "street", "name"
 * 
 * Now if we use this path relative to the same person, we always look up
 * his street name, even if he has a new address property set.
 * 
 * The structure of the path is slightly unusual, since we require all 
 * steps in the path except the last to lead to a Bean - this is so that
 * we can continue to follow the path by looking up Bean properties.
 * However the last step needs to lead to type T - the type of the path,
 * which is the type of Prop we expect to end up with when we follow the
 * path.
 * 
 * For this reason, the path is divided into an {@link Iterable} that gives the
 * "bean" steps of the path, and a single lastName that looks up the
 * end of the path from the last Bean.
 * 
 * BeanPaths must be immutable
 * 
 * @author shingoki
 *
 * @param <P> 
 * 		The type of final prop reached by the path (e.g. {@link Prop},
 * {@link EditableProp}, {@link ListProp} etc.)
 * @param <T>
 * 		The type of data in the final prop reached by the path
 */
public interface BeanPath<P extends Prop<T>, T> extends Iterable<PropName<? extends Prop<? extends Bean>, ? extends Bean>> {

	/**
	 * The name of the last step in the path - this is taken from the
	 * last bean in the path, to the actual property we are looking for
	 * @return
	 * 		The lastName
	 */
	public PropName<? extends P, T> getLastName();
	
}
