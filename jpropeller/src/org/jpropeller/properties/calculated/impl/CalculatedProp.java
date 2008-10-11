/*
 *  $Id: MutablePropPrimitive.java,v 1.1 2008/03/24 11:19:49 shingoki Exp $
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
package org.jpropeller.properties.calculated.impl;

import java.util.HashSet;
import java.util.Set;

import org.jpropeller.bean.Bean;
import org.jpropeller.info.PropInfo;
import org.jpropeller.map.PropMap;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.calculated.PropCalculation;
import org.jpropeller.properties.event.PropEvent;
import org.jpropeller.properties.event.PropEventOrigin;
import org.jpropeller.properties.event.PropInternalListener;
import org.jpropeller.properties.event.impl.PropEventDefault;

/**
 * A {@link Prop} which calculates its value as needed
 * based on the values of other properties
 * 
 * @param <T>
 * 		The type of the {@link Prop} value
 */
public class CalculatedProp<T> implements Prop<T>, PropInternalListener {

	PropMap propMap;
	T cachedValue;
	boolean cacheValid = false;
	PropName<? extends Prop<T>, T> name;
	PropCalculation<T> calculation;
	
	/**
	 * Create a prop
	 * @param name
	 * 		The name of the prop
	 * @param calculation
	 * 		The calculation used for the prop value
	 */
	public CalculatedProp(PropName<? extends Prop<T>, T> name, PropCalculation<T> calculation) {
		this.name = name;
		this.calculation = calculation;
		
		//Make a set of PropMaps for props used by the calculation
		//We will then listen to these. We don't just listen to
		//each as we iterate since we may use more than one prop
		//per set, and we don't want to listen multiple times
		Set<PropMap> usedPropMaps = new HashSet<PropMap>();
		for (GeneralProp<?> prop : calculation.getSourceProps()) {
			usedPropMaps.add(prop.props());
		}
		
		//Listen to all the PropMaps
		for (PropMap propMap : usedPropMaps) {
			propMap.addInternalListener(this);
		}
		
	}
	
	@Override
	public <S> void propInternalChanged(PropEvent<S> event) {
		//We are only concerned with props that are in the calculation set
		if (!calculation.getSourceProps().contains(event.getProp())) return;
		
		//Mark the cache as invalid - it will need to be recalculated
		cacheValid = false;
		
		//Fire a consequent change on this prop
		System.out.println("Firing recalculation change from event " + event.hashCode());
		props().propChanged(new PropEventDefault<T>(this, PropEventOrigin.CONSEQUENCE));	
	}

	@Override
	public void setPropMap(PropMap map) {
		if (propMap != null) throw new IllegalArgumentException("Prop '" + this + "' already has its PropMap set to '" + propMap + "'");
		if (map == null) throw new IllegalArgumentException("PropMap must be non-null");
		this.propMap = map;
	}

	@Override
	public T get() {
		if (!cacheValid) {
			calculateValue();
		}
		return cachedValue;
	}

	private void calculateValue() {
		cachedValue = calculation.calculate();
		cacheValid = true;
	}
	
	@Override
	public String toString() {
		return "Calculated Prop '" + getName().getString() + "' = '" + get() + "'";
	}
	
	@Override
	public Bean getBean() {
		return props().getBean();
	}

	@Override
	public PropName<? extends Prop<T>, T> getName() {
		return name;
	}

	@Override
	public PropMap props() {
		return propMap;
	}

	@Override
	public PropInfo getInfo() {
		return PropInfo.DEFAULT;
	}
	
}
