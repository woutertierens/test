/*
 *  Copyright (c) 2008 shingoki
 *
 *  This file is part of jpropeller, see http://code.google.com/p/jpropeller/
 *
 *    jpropeller is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    jpropeller is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with jpropeller. If not, see <http://www.gnu.org/licenses/>.
 *    
 */
package org.jpropeller.path;

import org.jpropeller.bean.Bean;
import org.jpropeller.properties.Prop;
import org.jpropeller.transformer.Transformer;

/**
 * A {@link BeanPath} allows for looking up of a {@link Prop} by following a series
 * of {@link Transformer}s through a chain of beans.
 * 
 * For example, we might have {@link Bean} classes Person, Address, Street
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
 * However the last step needs to lead to a {@link Prop} with value type T.
 * 
 * For this reason, the path is divided into an {@link Iterable} that gives the
 * "bean" steps of the path, and a single last transform that looks up the
 * end of the path from the last {@link Bean}.
 * 
 * {@link BeanPath}s must be immutable
 * 
 * Note that the Transformers we iterate over MUST each produce a type that is
 * acceptable to the next, and the last one in the iteration must produce
 * 
 * @param <R>		The root type - starts the path
 * @param <D>		The type of data in the final {@link Prop} reached by the path
 */
public interface BeanPath<R, D> {

	/**
	 * Iterate the properties of the {@link BeanPath}, starting from
	 * a defined root. 
	 * 
	 * @param root		The root of the path
	 * @return			An iterator through the path.
	 */
	public BeanPathIterator<D> iteratorFrom(R root);
	
}
