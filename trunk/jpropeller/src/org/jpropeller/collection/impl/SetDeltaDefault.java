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
package org.jpropeller.collection.impl;

import java.util.Set;

import org.jpropeller.collection.CollectionChangeType;
import org.jpropeller.collection.ListDelta;
import org.jpropeller.collection.SetDelta;

/**
 * Default implementation of {@link SetDelta}, with factory methods for 
 * easier creation of events using the modified {@link Set}
 */
public class SetDeltaDefault implements SetDelta {

	int oldSize;
	int newSize;
	CollectionChangeType type;
	int changeSize;

	/**
	 * Make a {@link SetDelta} for adding to a {@link Set} 
	 * The {@link Set} must have had the element added 
	 * already when this is called.
	 * @param set The {@link Set} added to
	 * @param additionCount The number of added elements - must be > 0
	 * @return The corresponding set change
	 */
	public static SetDeltaDefault newAdd(Set<?> set, int additionCount){
		
		return new SetDeltaDefault(
				CollectionChangeType.INSERTION,
				set.size() - additionCount,				//size before addition was current size - 1
				set.size());				//size now is size now ;)
	}
	
	/**
	 * Make a {@link SetDelta} for altering elements of a {@link Set}. The
	 * change indicates that any element may have changed, but the 
	 * list is still the same length. 
	 * @param set The changed {@link Set}
	 * @return The corresponding {@link SetDelta}
	 */
	public static SetDeltaDefault newEntireSetAlteration(Set<?> set){
		return new SetDeltaDefault(
				CollectionChangeType.ALTERATION,
				set.size(),				//size before addition was current size
				set.size());			//size now is size now ;)
	}

	/**
	 * Make a {@link SetDelta} for removing one element 
	 * from a {@link Set}. The {@link Set} must have
	 * had the element removed already when this is called.
	 * @param set The {@link Set} removed from
	 * @param removalCount The number of removed elements - must be > 0
	 * @return The corresponding {@link SetDelta}
	 */
	public static SetDeltaDefault newRemoveChange(Set<?> set, int removalCount){
		return new SetDeltaDefault(
				CollectionChangeType.DELETION,
				set.size() + removalCount,	//size before removal was current size + 1
				set.size());				//size now is size now ;)
	}	
	
	/**
	 * Make a {@link SetDelta} for clearing a {@link Set}. 
	 * @param oldSize The size of the {@link Set} BEFORE it was cleared
	 * @return The corresponding {@link SetDelta}
	 */
	public static SetDeltaDefault newClearChange(int oldSize){
		return new SetDeltaDefault(
				CollectionChangeType.CLEAR,
				oldSize,				//size before clear
				0);						//size now is 0
	}

	/**
	 * Make a {@link SetDelta} for completely changing 
	 * a {@link Set}, where any/all elements may have 
	 * changed, and the set size may also have changed.
	 * @param set
	 * 		The {@link Set} that has changed
	 * @param oldSize
	 * 		The size of the {@link Set} BEFORE it was changed
	 * @return The corresponding {@link SetDelta}
	 */
	public static SetDeltaDefault newCompleteChange(Set<?> set, int oldSize){
		return new SetDeltaDefault(
				CollectionChangeType.COMPLETE,
				oldSize,				//size before change
				set.size());			//size now
	}

	/**
	 * Make a {@link SetDelta} for completely changing 
	 * a {@link Set}, where old size is not known
	 * @param set The {@link Set} that has changed 
	 * @return The corresponding {@link SetDelta}
	 */
	public static SetDeltaDefault newCompleteChange(Set<?> set){
		return new SetDeltaDefault(
				CollectionChangeType.COMPLETE,
				-1,				//size before change is unknown
				set.size());		//size now
	}
	
	/**
	 * Create a {@link ListDelta}. Use factory methods to get instances
	 */
	private SetDeltaDefault(
			CollectionChangeType type, 
			int oldSize, int newSize) {
		super();
		this.oldSize = oldSize;
		this.newSize = newSize;
		this.type = type;
		
		//Calculate size of change - if either size is negative,
		//then we don't know it, so the size of the change is also
		//unknown
		if (oldSize<0 || newSize <0) {
			changeSize = -1;
		} else {
			changeSize = newSize - oldSize;
		}
	}

	@Override
	public int getOldSize() {
		return oldSize;
	}

	@Override
	public int getNewSize() {
		return newSize;
	}

	@Override
	public CollectionChangeType getType() {
		return type;
	}

	@Override
	public int getChangeSize() {
		return changeSize;
	}

	@Override
	public String toString() {
		return "Set Delta, type " + getType() + 
			", change size is " + getChangeSize() + 
			", old size " + getOldSize() + 
			" to new size " + getNewSize();		
	}
	
}
