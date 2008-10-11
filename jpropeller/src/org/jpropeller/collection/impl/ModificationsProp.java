/*
 *  $Id: org.eclipse.jdt.ui.prefs,v 1.1 2008/03/24 11:20:15 shingoki Exp $
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

import org.jpropeller.bean.Bean;
import org.jpropeller.collection.ListChange;
import org.jpropeller.collection.MapChange;
import org.jpropeller.info.PropInfo;
import org.jpropeller.map.PropMap;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.event.PropEvent;
import org.jpropeller.properties.event.PropEventOrigin;
import org.jpropeller.properties.event.impl.ListPropEventDefault;
import org.jpropeller.properties.event.impl.MapPropEventDefault;

/**
 * A {@link Prop} used to present the modifications count of
 * an observable collection (List or Map)
 * @author shingoki
 */
final class ModificationsProp implements Prop<Long> {
	
	private final Bean bean;
	PropName<Prop<Long>, Long> modificationsName = PropName.create("modifications", Long.class);
	
	/**
	 * Create a {@link ModificationsProp}
	 * @param bean
	 * 		The bean using this prop
	 */
	ModificationsProp(Bean bean) {
		this.bean = bean;
	}

	PropMap propMap = null;
	private long modifications = 0;

	@Override
	public PropInfo getInfo() {
		return PropInfo.DEFAULT;
	}

	@Override
	public PropMap props() {
		return propMap;
	}

	@Override
	public void setPropMap(PropMap map) {
		this.propMap = map;
	}

	@Override
	public PropName<Prop<Long>, Long> getName() {
		return modificationsName;
	}

	@Override
	public Bean getBean() {
		return this.bean;
	}

	@Override
	public Long get() {
		return modifications;
	}

	/**
	 * Increment the modification count, then fire an event on the 
	 * modification prop change
	 * @param event
	 * 		The event to fire for the change
	 */
	void increment(PropEvent<Long> event) {
		modifications++;
		props().propChanged(event);
	}

	/**
	 * Increment the modifications count and fire a prop changed event
	 * The event will be a shallow ListPropEvent with origin USER,
	 * and the specified ListChange
	 * @param listChange
	 * 		The change that occurred in the list
	 */
	void incrementFromListChange(ListChange listChange) {
		incrementFromListChange(listChange, PropEventOrigin.USER);
	}

	/**
	 * Increment the modifications count and fire a prop changed event
	 * The event will be a shallow ListPropEvent with specified origin,
	 * and specified ListChange
	 * @param listChange
	 * 		The change that occurred in the list
	 * @param rootOrigin
	 * 		The root origin of the prop event
	 */
	private void incrementFromListChange(ListChange listChange, PropEventOrigin rootOrigin) {
		increment(new ListPropEventDefault<Long>(this, rootOrigin, listChange));
	}
	
	/**
	 * Increment the modifications count and fire a prop changed event
	 * The event will be a shallow MapPropEvent with origin USER,
	 * and the specified MapChange
	 * @param listChange
	 * 		The change that occurred in the list
	 */
	void incrementFromMapChange(MapChange<?> mapChange) {
		incrementFromMapChange(mapChange, PropEventOrigin.USER);
	}

	/**
	 * Increment the modifications count and fire a prop changed event
	 * The event will be a shallow MapPropEvent with specified origin,
	 * and specified MapChange
	 * @param mapChange
	 * 		The change that occurred in the list
	 * @param rootOrigin
	 * 		The root origin of the prop event
	 */
	private <K> void incrementFromMapChange(MapChange<K> mapChange, PropEventOrigin rootOrigin) {
		increment(new MapPropEventDefault<K, Long>(this, rootOrigin, mapChange));
	}
}