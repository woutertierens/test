/*
 *  $Id: Bean.java,v 1.1 2008/03/24 11:20:17 shingoki Exp $
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
package org.jpropeller.bean;

import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Changeable;

/**
 * A Bean is an object that potentially "has" {@link GeneralProp}s
 * 
 * By "has" we mean that as a minimum it provides a 
 * {@link BeanFeatures} that can be used to look up the Props associated
 * with the Bean, and listen to changes to them.
 * 
 * {@link BeanFeatures} is provided as a separate interface to be returned
 * as an instance to make it (much) easier to implement {@link Bean}s without
 * extending a base {@link Bean} implementation class. 
 * Of course {@link BeanDefault} is also available if you want to extend 
 * a base class.
 * 
 * Most {@link Bean}s will also want to provide methods to
 * return Props directly. These methods are named the
 * same as the name of the Prop as returned by {@link Prop#getName()} 
 * and simply return the prop itself. This makes it easier to use the
 * Bean, for example:
 * 
 * person.address().get()
 * 		will return the address associated with person
 * 
 * person.address().set(newAddress)
 * 		will change the address (assuming that address()
 * returns an {@link EditableProp})
 * 
 * person.address().get().street().get()
 * 		will return the street of the address of the person
 * 
 * NOTE: implementations must enforce the rules for {@link Bean}s, {@link BeanFeatures}s and {@link GeneralProp}s:
 *		
 *		A {@link Bean} must have an constant {@link BeanFeatures} - it should be created when the
 * {@link Bean} is created, and must not be changed. It cannot be shared with another
 * {@link Bean}, and the {@link BeanFeatures} MUST have its bean set to this {@link Bean}, so that
 * it is returned by {@link BeanFeatures#getBean()}
 * 
 * 		All {@link Prop}s available via fields or methods from the {@link Bean} must also be
 * available in the {@link BeanFeatures}
 * 
 * 		This is essentially a result of the rule above, but properties should not
 * be changed or removed - that is, the value of a Prop may change, but a {@link Bean}
 * should use the same Prop instance for each property throughout its lifetime,
 * so that users of the {@link Bean} may listen to, or retain a reference to, the Prop rather than the
 * {@link Bean}, if they wish. (Also because there are events when Prop values change, but
 * no events for the Prop instance itself changing, etc. Changing Prop instances instead
 * of Prop values via set() essentially defeats the point of Props)
 */
public interface Bean extends Changeable{
	
	/**
	 * Get the features of the {@link Bean}
	 */
	public BeanFeatures features();
	
}
