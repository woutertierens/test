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

/**
 * A hint as to whether or not the prop is editable (or how editable).
 *
 */
public enum PropEditability {

	/**
	 * Implements EditableProp, and so may be changed using a set() method
	 * Props with this type may be cast to EditableProp
	 */
	EDITABLE,
	
	/**
	 * Implements UneditableProp, and so may not be changed using a set() method.
	 * However, the value may change in a deep way - for example, if we have
	 * a Person with an Address Prop, and the Address Prop is uneditable, then
	 * we cannot use person.address().set(newAddress).
	 * Also, person.address() will always return the same Address instance.
	 * However, if that Address instance has properties of its own, and they are
	 * changed, this means that the person's address has changed - in a deep way,
	 * rather than a shallow way.
	 * 
	 *  This may seem useless to know that the property is uneditable in a shallow
	 *  way, but in fact it can be useful, since we can keep a reference to the value
	 *  of an uneditable property, and know that it is always the same as we would get
	 *  from calling get() on the property again. In addition, an UneditableProp which
	 *  contains an immutable primitive wrapper, or contains a Prop<Bean<UneditableProp>>
	 *  is actually genuinely immutable.
	 */
	DEFAULT
}