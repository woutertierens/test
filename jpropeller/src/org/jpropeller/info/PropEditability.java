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

import org.jpropeller.properties.Prop;

/**
 * A hint as to whether or not the {@link Prop} is editable
 */
public enum PropEditability {

	/**
	 * Calling the appropriate editing methods (e.g. {@link Prop#set(Object)}
	 * will succeed for valid instances of the correct class of value, but
	 * may throw an exception on invalid values.
	 * 
	 * As a consequence of the contract for validation, a {@link Prop} must
	 * always accept a value it currently contains, and must not accept or
	 * reject values based on any state. For example serialisation may be written
	 * to assume that {@link PropEditability#EDITABLE} {@link Prop}s can
	 * have previously stored values set back into them. 
	 */
	EDITABLE,

	/**
	 * Will never accept any input value
	 */
	READ_ONLY
}