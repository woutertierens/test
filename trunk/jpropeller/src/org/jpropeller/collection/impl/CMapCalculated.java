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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jpropeller.calculation.Calculation;
import org.jpropeller.collection.CMap;
import org.jpropeller.properties.calculated.impl.CalculatedProp;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeSystem;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.ChangeableFeatures;
import org.jpropeller.properties.change.impl.ChangeDefault;
import org.jpropeller.properties.change.impl.ChangeableFeaturesDefault;
import org.jpropeller.properties.change.impl.InternalChangeImplementation;
import org.jpropeller.system.PropSystem;
import org.jpropeller.system.Props;

/**
 * An {@link CMapCalculated} uses a {@link Calculation} to
 * produce a {@link Map} from source properties, and then makes that
 * {@link Map} available for read-only access.
 * 
 * The {@link CMapCalculated} changes only when a new calculation
 * is required due to the sources of the calculation changing, 
 * and that calculation is performed lazily, and cached - this
 * behaviour is similar to that for {@link CalculatedProp}.
 * 
 * Note that the {@link CMapCalculated} will NOT propagate changes
 * to the contents of previously calculated {@link Map}s - it is assumed
 * that {@link Calculation} will NOT produce changeable content as a result,
 * unless it can guarantee that any changes to the result will also involve
 * changes to the sources, triggering a new calculation. This is part of
 * the contract for {@link Calculation}.
 * 
 * NOTE: Currently {@link #keySet()}, {@link #entrySet()} and {@link #values()}
 * will throw {@link UnsupportedOperationException}.
 * 
 * @param <K>		The type of key in the map
 * @param <V>		The type of value in the map
 */
public class CMapCalculated<K, V> implements CMap<K, V> {
	
	//Standard code block for a bean
	private ChangeableFeatures features;

	private Calculation<Map<K, V>> calculation;

	//The current calculation map result we delegate 
	//to for actual storage, etc., or null if no value
	private Map<K, V> core;	
	private boolean cacheValid = false;

	@Override
	public ChangeableFeatures features() {
		return features;
	}
	
	/**
	 * Create a new {@link CMapCalculated}
	 * 
	 * @param calculation	The calculation yielding map contents
	 */
	public CMapCalculated(Calculation<Map<K, V>> calculation) {
		super();
		
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
		
		//Listen to each source changeable
		for (Changeable changeable : calculation.getSources()) {
			changeable.features().addChangeableListener(this);
		}
	}

	/**
	 * Start a read operation - prepare for a read, then attempt to
	 * calculate a new cached value if necessary.
	 * Note that when this returns or throws an exception, the 
	 * {@link PropSystem} read lock WILL be held 
	 * {@link ChangeSystem#prepareRead(Changeable)} is called 
	 * as first operation). Hence it is necessary to use this inside
	 * a try...finally block, with {@link #end()} in the finally block:
	 * <pre>
	 * 	try {
	 * 		start();
	 * 		//Read state from the cachedValue
	 * </pre>
	 */
	private void start() {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		
		if (!cacheValid) {

			Map<K, V> newCore = calculation.calculate();
			
			//Null values are not acceptable - throw exception sooner rather than later
			if (newCore == null) {
				throw new NullPointerException("Null list returned by calculation");
			}
			
			core = Collections.unmodifiableMap(newCore);
			
			//We now have a usable value
			cacheValid = true;
		}
	}
	
	private void end() {
		Props.getPropSystem().getChangeSystem().concludeRead(this);
	}

	//The means of reading state - need to check cache before each, and
	//also use read lock
	
	@Override
	public boolean isEmpty() {
		try {
			start();
			return core.isEmpty();
		} finally {
			end();
		}
	}

	@Override
	public int size() {
		try {
			start();
			return core.size();
		} finally {
			end();
		}
	}
	
	@Override
	public boolean containsKey(Object key) {
		try {
			start();
			return core.containsKey(key);
		} finally {
			end();
		}
	}

	@Override
	public boolean containsValue(Object value) {
		try {
			start();
			return core.containsValue(value);
		} finally {
			end();
		}
	}

	@Override
	public V get(Object key) {
		try {
			start();
			return core.get(key);
		} finally {
			end();
		}
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		//TODO implement the following, which will require a set wrapper
		throw new UnsupportedOperationException("Can't get entrySet of " + CMapCalculated.class.getName());
	}

	@Override
	public Set<K> keySet() {
		//TODO implement the following, which will require a set wrapper
		throw new UnsupportedOperationException("Can't get keySet of " + CMapCalculated.class.getName());
	}

	@Override
	public Collection<V> values() {
		//TODO implement the following, which will require a Collection wrapper
		throw new UnsupportedOperationException("Can't get values of " + CMapCalculated.class.getName());
	}
	
	//Unsupported operations
	private void throwUnsupported(String operation) {
		throw new UnsupportedOperationException("Can't " + operation + " " + CMapCalculated.class.getName());		
	}
	@Override
	public void clear() {throwUnsupported("clear");}
	@Override
	public void replace(Map<? extends K, ? extends V> newContents) {throwUnsupported("replace");}
	@Override
	public V put(K key, V value) {
		throw new UnsupportedOperationException("Can't put into " + CMapCalculated.class.getName());		
	}
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {throwUnsupported("putAll");}

	@Override
	public V remove(Object key) {
		throw new UnsupportedOperationException("Can't remove from " + CMapCalculated.class.getName());		
	}
	
}
