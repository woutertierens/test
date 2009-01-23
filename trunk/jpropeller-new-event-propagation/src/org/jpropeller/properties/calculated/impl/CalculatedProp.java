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

import java.util.List;
import java.util.Map;

import org.jpropeller.info.PropInfo;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.calculated.PropCalculation;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.ChangeableFeatures;
import org.jpropeller.properties.change.impl.ChangeDefault;
import org.jpropeller.properties.change.impl.ChangeableFeaturesDefault;
import org.jpropeller.properties.change.impl.InternalChangeImplementation;
import org.jpropeller.system.Props;

/**
 * A {@link Prop} which calculates its value as needed
 * based on the values of other properties
 * 
 * @param <T>
 * 		The type of the {@link Prop} value
 */
public class CalculatedProp<T> implements Prop<T> {

	T cachedValue;
	boolean cacheValid = false;
	PropName<? extends Prop<T>, T> name;
	PropCalculation<T> calculation;
	ChangeableFeaturesDefault features;
	
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
		
		features = new ChangeableFeaturesDefault(new InternalChangeImplementation() {
			@Override
			public Change internalChange(Changeable changed, Change change,
					List<Changeable> initial, Map<Changeable, Change> changes) {
				
				//Mark the cache as invalid - it will need to be recalculated
				cacheValid = false;
				
				//Since we only listen to the actual props we are calculated
				//from, we don't need to filter here, we always have a
				//change
				return ChangeDefault.instance(
						false,	//Change is consequence of source proper change 
						false	//Instance may well change - we don't know until we recalculate, 
								//but it is very likely (e.g. a new primitive wrapper value)
						);
			}
		}, this); 

		//Listen to each source prop
		for (GeneralProp<?> prop : calculation.getSourceProps()) {
			prop.features().addChangeableListener(this);
		}
	}
	
	@Override
	public ChangeableFeatures features() {
		return features;
	}

	@Override
	public T get() {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		try {
			if (!cacheValid) {
				calculateValue();
			}
			return cachedValue;
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
		}
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
	public PropName<? extends Prop<T>, T> getName() {
		return name;
	}

	@Override
	public PropInfo getInfo() {
		return PropInfo.DEFAULT;
	}
	
}
