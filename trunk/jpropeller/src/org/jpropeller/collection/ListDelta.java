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

/**
 * A representation of a change to a list. This describes the
 * scope of the changes, either giving the exact changes or
 * possibly an overstatement of the changes (for example,
 * when multiple regions of the list change, the {@link ListDelta}
 * would have to represent this change as a larger single region
 * changing).
 * 
 * A {@link ListDelta} represents only a single _type_ of change - for example
 * an INSERTION OR a DELETION, but not a combination of the two. The
 * exception is that a COMPLETE change represents any change at all - 
 * it simply advises that the list may differ in any way from its old
 * state. 
 * 
 * For a less overstated representation of changes
 * a {@link List} of {@link ListDelta} instances can be used, this can represent multi-region,
 * multi-type changes by breaking the change down into multiple operations.
 * Due to the way {@link List}s are used, most changes caused by calling {@link List} methods
 * will in fact constitute a single {@link ListDelta}, the exceptions 
 * being {@link List#removeAll(java.util.Collection)} and 
 * {@link List#retainAll(java.util.Collection)}, which may well be best
 * represented as a COMPLETE change in any case.
 * 
 * Implementations MUST BE IMMUTABLE
 */
public interface ListDelta {

	/**
	 * @return i such that for all int j < i,
	 * get(j) will return the same value now as it would have
	 * before the change, assuming that j is in list now, and
	 * was in list before change. May return -1 for COMPLETE change,
	 * indicating first changed index is not known
	 */
	public int getFirstChangedIndex();
	
	/**
	 * @return i such that for all int j > i,
	 * get(j) will return the same value now as it would have
	 * before the change, assuming that j is in list now, and
	 * was in list before change. May return -1 for COMPLETE change,
	 * indicating last changed index is not known
	 */
	public int getLastChangedIndex();
	
	/**
	 * @return The size of the list before the change
	 * For a COMPLETE change this may be -1 indicating no knowledge of
	 * old size
	 */
	public int getOldSize();
	
	/**
	 * @return The size of the list after the change
	 */
	public int getNewSize();
	
	/**
	 * @return The type of change
	 */
	public CollectionChangeType getType();
	
	/**
	 * @return The size of the change:
	 * 	For COMPLETE, this will be the change in size of the list, or
	 * 	may be -1 indicating no knowledge of the size of the change
	 * 	For REPLACEMENT, the number of possibly altered elements
	 * (from firstChangedIndex to lastChangedIndex).
	 * 	For INSERTION, the number of inserted elements
	 * 	For DELETION, the number of deleted elements
	 */
	public int getChangeSize();
	
}
