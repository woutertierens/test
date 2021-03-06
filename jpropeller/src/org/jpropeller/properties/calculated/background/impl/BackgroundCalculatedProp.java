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
package org.jpropeller.properties.calculated.background.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.jpropeller.calculation.Calculation;
import org.jpropeller.concurrency.Responder;
import org.jpropeller.concurrency.impl.BackgroundResponder;
import org.jpropeller.concurrency.impl.ExecutorUtils;
import org.jpropeller.info.PropAccessType;
import org.jpropeller.info.PropEditability;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.ChangeableFeatures;
import org.jpropeller.properties.change.impl.ChangeDefault;
import org.jpropeller.properties.change.impl.ChangeableFeaturesDefault;
import org.jpropeller.properties.change.impl.InternalChangeImplementation;
import org.jpropeller.properties.exception.ReadOnlyException;
import org.jpropeller.system.Props;

/**
 * A {@link Prop} which calculates its value as needed
 * based on the values of other properties, performing
 * the calculation in the background, rather than when
 * getter is called. The getter always returns the most
 * recent calculation result - might not be up to date
 * at any given time, but will eventually become correct
 * if the input values remain constant.
 * 
 * @param <T> The type of the {@link Prop} value
 */
public class BackgroundCalculatedProp<T> implements Prop<T> {

	private T cachedValue;
	private PropName<T> name;
	private final Calculation<T> calculation;
	private ChangeableFeaturesDefault features;
	
	//Handles updating in the background - just calculates a new value and then
	//sets it in this prop
	private final Runnable updateRunnable = new Runnable() {
		
		@Override
		public void run() {
			T newValue = calculation.calculate();

			Props.getPropSystem().getChangeSystem().prepareChange(BackgroundCalculatedProp.this);		
			try {
				cachedValue = newValue;
				
				//Propagate the change we just made
				Props.getPropSystem().getChangeSystem().propagateChange(BackgroundCalculatedProp.this, ChangeDefault.instance(
						true,	//Change IS initial 
						false	//Instance is NOT the same - we must assume new value is produced by calculation
						));
			} finally {
				Props.getPropSystem().getChangeSystem().concludeChange(BackgroundCalculatedProp.this);
			}
		}
	};
	
	/**
	 * Handles updating in the background - just calculates a new value and then
	 * sets it in this prop
	 */
	Responder backgroundUpdater;// = new BackgroundResponder(updateRunnable, executor);

	/**
	 * Create a prop using default {@link ExecutorService} from {@link ExecutorUtils}
	 * @param name The name of the prop
	 * @param propCalculation The calculation used for the prop value
	 * @param initialValue The initial value for the prop, used until the first
	 * calculation result is ready
	 */
	public BackgroundCalculatedProp(PropName<T> name, Calculation<T> propCalculation, T initialValue) {
		this(name, propCalculation, initialValue, ExecutorUtils.getExecutorService());
	}
	
	/**
	 * Create a prop
	 * @param name 				The name of the prop
	 * @param propCalculation 	The calculation used for the prop value
	 * @param initialValue 		The initial value for the prop, used until the first
	 * 							calculation result is ready
	 * @param executor			The {@link ExecutorService} actually used to 
	 * 							run background calculation
	 */
	public BackgroundCalculatedProp(PropName<T> name, Calculation<T> propCalculation, T initialValue, ExecutorService executor) {

		this.backgroundUpdater = new BackgroundResponder(updateRunnable, executor);
		
		this.name = name;
		this.calculation = propCalculation;
		this.cachedValue = initialValue;

		features = new ChangeableFeaturesDefault(new InternalChangeImplementation() {
			@Override
			public Change internalChange(Changeable changed, Change change,
					List<Changeable> initial, Map<Changeable, Change> changes) {
				
				//We don't actually change in response to seeing source properties change - 
				//we still retain our old value, we just set off a background update that
				//will set the newly calculated value in the future
				backgroundUpdater.request();
				return null;
			}
		}, this); 

		//Listen to each source changeable
		for (Changeable changeable : calculation.getSources()) {
			changeable.features().addChangeableListener(this);
		}
		
		//Start initial calculation
		backgroundUpdater.request();
	}

	@Override
	public T get() {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		try {
			//Always just return the cached value
			return cachedValue;
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
		}
	}

	//Normal prop methods
	
	@Override
	public ChangeableFeatures features() {
		return features;
	}
	
	@Override
	public String toString() {
		return "Background Calculated Prop '" + getName().getString() + "' = '" + get() + "'";
	}
	
	@Override
	public PropName<T> getName() {
		return name;
	}

	@Override
	public PropEditability getEditability() {
		return PropEditability.READ_ONLY;
	}

	@Override
	public PropAccessType getAccessType() {
		return PropAccessType.SINGLE;
	}
	
	@Override
	public void set(T value) throws UnsupportedOperationException {
		throw new ReadOnlyException("Calculated property cannot be set");
	}
	
}
