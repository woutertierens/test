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
package org.jpropeller.collection;

import java.util.List;
import java.util.Set;

/**
 * A representation of a change to a {@link Set}. This describes the
 * scope of the changes, either giving the exact changes or
 * possibly an overstatement of the changes (for example, it
 * is always valid (although not optimal) to represent any change as 
 * a COMPLETE change).
 * 
 * A {@link SetDelta} represents only a single _type_ of change - for example
 * an INSERTION OR a DELETION, but not a combination of the two. The
 * exception is that a COMPLETE change represents any change at all - 
 * it simply advises that the set may differ in any way from its old
 * state. 
 * 
 * For a less overstated representation of changes
 * a {@link List} of {@link SetDelta} instances can be used, breaking 
 * the change down into multiple operations.
 * 
 * Implementations MUST BE IMMUTABLE
 */
public interface SetDelta {

	/**
	 * @return The size of the set before the change
	 * For a COMPLETE change this may be -1 indicating no knowledge of
	 * old size
	 */
	public int getOldSize();
	
	/**
	 * @return The size of the set after the change
	 */
	public int getNewSize();
	
	/**
	 * @return The type of change
	 */
	public CollectionChangeType getType();
	
	/**
	 * @return The size of the change:
	 * 	For COMPLETE, this will be the change in size of the set (positive
	 *  if the set grows, negative if it shrinks), or
	 * 	may be -1 indicating no knowledge of the size of the change
	 * 	REPLACEMENT is not possible for a set.
	 * 	For INSERTION, the number of inserted elements
	 * 	For DELETION, the number of deleted elements
	 */
	public int getChangeSize();
	
}
