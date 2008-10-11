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

import java.util.Collection;
import java.util.List;

import org.jpropeller.collection.ListChange;
import org.jpropeller.collection.CollectionChangeType;

/**
 * Default implementation of ListChange, with factory methods for 
 * easier creation of events using the modified list
 */
public class ListChangeDefault implements ListChange {

	int firstChangedIndex;
	int lastChangedIndex;
	int oldSize;
	int newSize;
	CollectionChangeType type;
	int changeSize;

	/**
	 * Make a list change for adding to the end of a list. The list must have
	 * had the element added already when this is called.
	 * @param l The list added to
	 * @return The corresponding list change
	 */
	public static ListChangeDefault newAddChange(List<?> l){
		return new ListChangeDefault(
				CollectionChangeType.INSERTION,
				l.size() - 1,				//last element is the first and last changed - all the original contents of list are unaltered
				l.size() - 1,				//see previous comment
				l.size() - 1,				//size before addition was current size - 1
				l.size());					//size now is size now ;)
	}

	/**
	 * Make a list change for adding at an index into a list. The list must have
	 * had the element added already when this is called.
	 * @param l The list added to
	 * @param index The index added at
	 * @return The corresponding list change
	 */
	public static ListChangeDefault newAddChange(List<?> l, int index){
		return new ListChangeDefault(
				CollectionChangeType.INSERTION,
				index,					//index of insertion is first changed index					
				l.size() - 1,			//all indices after insertion changes - so last changed is last index
				l.size() - 1,			//size before addition was current size - 1
				l.size());				//size now is size now ;)
	}

	/**
	 * Make a list change indicating a single element 
	 * of a list at a known index has changed. The change may
	 * be either due to e.g. {@link List#set(int, Object)} being called,
	 * or due to the instance at the index changing in a deep way. 
	 * The list must have had the element changed already when this is called.
	 * @param l The list where element is set/changed
	 * @param index The index of element set/changed
	 * @return The corresponding list change
	 */
	public static ListChangeDefault newSingleElementAlteration(List<?> l, int index){
		return new ListChangeDefault(
				CollectionChangeType.ALTERATION,
				index,					//index of set is first changed index					
				index,					//index set is last changed index
				l.size(),				//size before addition was current size
				l.size());				//size now is size now ;)
	}

	/**
	 * Make a list change for altering elements of a list. The
	 * change indicates that any element may have changed, but the 
	 * list is still the same length. 
	 * The list must have had the alterations made already when 
	 * this is called.
	 * @param l The list where element is set
	 * @return The corresponding list change
	 */
	public static ListChangeDefault newEntireListAlteration(List<?> l){
		return new ListChangeDefault(
				CollectionChangeType.ALTERATION,
				0,						//0 is first changed index					
				l.size()-1,				//last changed index is last index in list
				l.size(),				//size before addition was current size
				l.size());				//size now is size now ;)
	}

	/**
	 * Make a list change for removing from an index in a list. The list must have
	 * had the element removed already when this is called.
	 * @param l The list removed from
	 * @param index The index removed
	 * @return The corresponding list change
	 */
	public static ListChangeDefault newRemoveChange(List<?> l, int index){
		return new ListChangeDefault(
				CollectionChangeType.DELETION,
				index,					//index of deletion is first changed index					
				l.size() - 1,			//all indices after insertion changes - so last changed is last index
				l.size() + 1,			//size before addition was current size + 1
				l.size());				//size now is size now ;)
	}	
	
	/**
	 * Make a list change for adding a collection at an index into a list. The list must have
	 * had the collection added already when this is called.
	 * @param l The list added to
	 * @param index The index added at
	 * @param c The collection added
	 * @return The corresponding list change
	 */
	public static ListChangeDefault newAddChange(List<?> l, int index, Collection<?> c){
		return new ListChangeDefault(
				CollectionChangeType.INSERTION,
				index,					//index of insertion is first changed index					
				l.size() - 1,			//all indices after insertion changes - so last changed is last index
				l.size() - c.size(),	//size before addition was current size - size of collection added
				l.size());				//size now is size now ;)
	}
	
	/**
	 * Make a list change for adding a collection to the end of a list. The list must have
	 * had the collection added already when this is called.
	 * @param l The list added to
	 * @param c The collection added
	 * @return The corresponding list change
	 */
	public static ListChangeDefault newAddChange(List<?> l, Collection<?> c){
		return new ListChangeDefault(
				CollectionChangeType.INSERTION,
				l.size() - c.size(),	//first changed index is first NEW index				
				l.size() - 1,			//all indices after insertion change - so last changed is last index
				l.size() - c.size(),	//size before addition was current size - size of collection added
				l.size());				//size now is size now ;)
	}

	/**
	 * Make a list change for clearing a list. 
	 * @param oldSize The size of the list BEFORE it was cleared
	 * @return The corresponding list change
	 */
	public static ListChangeDefault newClearChange(int oldSize){
		return new ListChangeDefault(
				CollectionChangeType.CLEAR,
				0,						//first changed index is 0				
				oldSize - 1,			//last changed index is last index in list before it was cleared
				oldSize,				//size before clear
				0);						//size now is 0
	}

	/**
	 * Make a list change for completely changing a list, where
	 * any/all elements may have changed, and the list length may
	 * also have changed.
	 * @param l
	 * 		The list that has changed
	 * @param oldSize
	 * 		The size of the list BEFORE it was changed
	 * @return The corresponding list change
	 */
	public static ListChangeDefault newCompleteChange(List<?> l, int oldSize){
		return new ListChangeDefault(
				CollectionChangeType.COMPLETE,
				0,						//first changed index is 0				
				Math.max(oldSize - 1, 
						l.size() - 1),	//last changed index is last index in list before it was changed, or current last index, whichever is greater
				oldSize,				//size before change
				l.size());				//size now
	}

	/**
	 * Make a list change for completely changing a list, where old size is not known
	 * @param l The list that has changed 
	 * @return The corresponding list change
	 */
	public static ListChangeDefault newCompleteChange(List<?> l){
		return new ListChangeDefault(
				CollectionChangeType.COMPLETE,
				-1,						//first changed index is 0				
				-1,	//last changed index is last index in list before it was changed, or current last index, whichever is greater
				-1,				//size before change
				l.size());				//size now
	}
	
	/**
	 * Create a change. Use factory methods to get instances
	 */
	private ListChangeDefault(
			CollectionChangeType type, 
			int firstChangedIndex, int lastChangedIndex,
			int oldSize, int newSize) {
		super();
		this.firstChangedIndex = firstChangedIndex;
		this.lastChangedIndex = lastChangedIndex;
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
	public int getFirstChangedIndex() {
		return firstChangedIndex;
	}

	@Override
	public int getLastChangedIndex() {
		return lastChangedIndex;
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
		return "List Change, type " + getType() + 
			" on indices " + getFirstChangedIndex() + " to " + getLastChangedIndex() + 
			" inclusive, size changed by " + getChangeSize() + 
			" from " + getOldSize() + " to " + getNewSize();		
	}
	
}
