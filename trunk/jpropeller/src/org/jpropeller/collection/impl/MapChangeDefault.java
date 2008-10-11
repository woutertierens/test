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

import java.util.Map;

import org.jpropeller.collection.CollectionChangeType;
import org.jpropeller.collection.MapChange;

/**
 * Default implementation of MapChange, with factory methods for 
 * easier creation of events using the modified map
 * @param <K> 
 * 		The type of key for the map.
 */
public class MapChangeDefault<K> implements MapChange<K> {

	private K key;
	boolean keyValid;
	int oldSize;
	int newSize;
	CollectionChangeType type;

	/**
	 * Make a map change for inserting a single mapping. The map must have
	 * had the mapping added already when this is called.
	 * @param <K> 
	 * 		The type of key for the map.
	 * @param map The map where element is set
	 * @param key The key from which a mapping has been added
	 * @return The corresponding map change
	 */
	public static <K> MapChangeDefault<K> newInsertionChange(Map<K, ?> map, K key){
		return new MapChangeDefault<K>(
				key,
				CollectionChangeType.INSERTION,
				//Map has increased in size by one
				map.size()-1, 	//old size
				map.size());	//new size
	}
	
	/**
	 * Make a map change for altering any/all mappings in a map. The
	 * change indicates that any mapping may have changed, but the 
	 * map is still the same size. 
	 * The map must have had the alterations made already when 
	 * this is called.
	 * @param <K>
	 * 		The type of key for the map.
	 * @param map The map where mappings have changed
	 * @return The corresponding map change
	 */
	public static <K> MapChangeDefault<K> newEntireMapAlteration(Map<K, ?> map){
		return new MapChangeDefault<K>(
				null, 
				CollectionChangeType.ALTERATION, 
				map.size(), 
				map.size());
	}

	/**
	 * Make a map change for altering a single mapping in a map.
	 * The map must have had the alteration made already when 
	 * this is called.
	 * @param <K>
	 * 		The type of key for the map.
	 * @param map The map where mappings have changed
	 * @param key The key whose mapping has changed
	 * @return The corresponding map change
	 */
	public static <K> MapChangeDefault<K> newSingleKeyAlteration(Map<K, ?> map, K key){
		return new MapChangeDefault<K>(
				key, 
				CollectionChangeType.ALTERATION, 
				map.size(), 
				map.size());
	}
	
	/**
	 * Make a map change for removing from an element in a map. The map must have
	 * had the element removed already when this is called.
	 * @param <K>
	 * 		The type of key for the map. 
	 * @param map The map where element was removed.
	 * @param key The key corresponding to the element that was removed.
	 * @return The corresponding map change
	 */
	public static <K> MapChangeDefault<K> newRemoveChange(Map<K, ?> map, K key){
		return new MapChangeDefault<K>(
				key, 
				CollectionChangeType.DELETION, 
				map.size()+1, 
				map.size());
	}	
	
	/**
	 * Make a map change for clearing a map. 
	 * @param <K> 
	 * 		The type of key for the map.
	 * @param oldSize The size of the list BEFORE it was cleared
	 * @return The corresponding map change
	 */
	public static <K> MapChangeDefault<K> newClearChange(int oldSize){
		return new MapChangeDefault<K>(
				null, 
				CollectionChangeType.CLEAR, 
				oldSize, 
				0);
	}
	
	/**
	 * Make a map change for completely changing a map. 
	 * @param <K> 
	 * 		The type of key for the map.
	 * @param size The size of the map now.
	 * @param oldSize The size of the list BEFORE it was cleared
	 * @return The corresponding map change
	 */
	public static<K> MapChangeDefault<K> newCompleteChange(int size, int oldSize){
		return new MapChangeDefault<K>(
				null, 
				CollectionChangeType.COMPLETE, 
				oldSize, 
				size);
	}

	/**
	 * Create a change. Use factory methods to get instances
	 */
	private MapChangeDefault(
			K key,
			CollectionChangeType type, 
			int oldSize, int newSize) {
		this.key = key;
		keyValid = (key != null);
		this.oldSize = oldSize;
		this.newSize = newSize;
		this.type = type;
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
	public K getKey() {
		return key;
	}

	@Override
	public boolean isKeyValid() {
		return keyValid;
	}

	@Override
	public String toString() {
		String s = "Map Change, type " + getType() +  
			" from size " + getOldSize() + " to size " + getNewSize();
		if (isKeyValid()) {
			s += " affecting only key '" + getKey() + "'";
		}
		return s;
	}
	
}
