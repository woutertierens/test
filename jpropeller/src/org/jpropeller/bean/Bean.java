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

import org.jpropeller.map.PropMap;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.map.HasPropMap;

/**
 * A Bean is an object that potentially "has" Props
 * 
 * By "has" we mean that as a minimum it provides a 
 * PropMap that can be used to look up the Props associated
 * with the Bean, and listen to changes to them.
 * 
 * Most Beans will also want to provide methods to
 * return Props directly. These methods are named the
 * same as {@link Prop#getName()} and simply
 * return the prop. This makes it easier to use the
 * Bean, for example:
 * 
 * person.address().get()
 * 		will return the address associated with person
 * 
 * person.address().set(newAddress)
 * 		will change the address (assuming that address()
 * returns a MutableProp)
 * 
 * person.address().get().street().get()
 * 		will return the street associated with the address
 * associated with the person
 * 
 * NOTE: implementations must enforce the rules for {@link Bean}s, {@link PropMap}s and {@link Prop}s:
 *		
 *		A {@link Bean} must have an uneditable {@link PropMap} - it should be created when the
 * {@link Bean} is created, and must not be changed. It cannot be shared with another
 * {@link Bean}, and the {@link PropMap} MUST have its bean property set to this {@link Bean}
 * 
 * 		All {@link Prop}s available via fields or methods from the {@link Bean} must also be
 * available in the {@link PropMap}
 * 
 * 		This is essentially a result of the rule above, but properties should not
 * be changed or removed - that is, the value of a Prop may change, but a Bean
 * should use the same Prop instance for each property throughout its lifetime,
 * so that users of the Bean may retain a reference to the Prop rather than the
 * Bean, if they wish. (Also because there are events when Prop values change, but
 * no events for the Prop instance changing, etc. Changing Prop instances instead
 * of Prop values via set() essentially defeats the point of Props)
 * 
 * @author shingoki
 *
 */
public interface Bean extends HasPropMap{	
}
