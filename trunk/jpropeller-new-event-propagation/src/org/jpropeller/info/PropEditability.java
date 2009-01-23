/*
 *  $Id: PropType.java,v 1.1 2008/03/24 11:19:42 shingoki Exp $
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
package org.jpropeller.info;

import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.GenericEditableProp;
import org.jpropeller.properties.GenericProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.list.EditableListProp;
import org.jpropeller.properties.list.ListProp;
import org.jpropeller.properties.map.EditableMapProp;
import org.jpropeller.properties.map.MapProp;

/**
 * A hint as to whether or not the prop is editable (or how editable).
 *
 */
public enum PropEditability {

	/**
	 * Implements an editable interface, having appropriate
	 * set methods.
	 * {@link GenericProp}s with this type may be cast to {@link GenericEditableProp}
	 * <br />
	 * {@link Prop}s with this type may be cast to {@link EditableProp}
	 * <br />
	 * {@link ListProp}s with this type may be cast to {@link EditableListProp}
	 * <br />
	 * {@link MapProp}s with this type may be cast to {@link EditableMapProp}
	 */
	EDITABLE,
	
	/**
	 * Does not implement an editable interface, and so cannot have a new
	 * value set directly.
	 * However, the value may change in a deep way - for example, if we have
	 * a Person with an Address {@link Prop}, and the Address Prop is default, then
	 * we cannot use person.address().set(newAddress).
	 * However, if that Address instance has properties of its own, and they are
	 * changed, this means that the person's address has changed - in a deep way,
	 * rather than a shallow way.
	 */
	DEFAULT
}