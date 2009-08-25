package org.jpropeller.properties.change.impl;

import org.jpropeller.bean.Bean;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeType;

/**
 * Immutable implementation of {@link Change}, returning
 * shared instances for each possible state.
 * 
 * This is suitable for changes to {@link Bean}s, and to
 * {@link Prop}s (i.e. props with a single value).
 */
public final class ChangeDefault implements Change {

	private final static ChangeDefault TRUE_TRUE = new ChangeDefault(true, true);
	private final static ChangeDefault TRUE_FALSE = new ChangeDefault(true, false);
	private final static ChangeDefault FALSE_TRUE = new ChangeDefault(false, true);
	private final static ChangeDefault FALSE_FALSE = new ChangeDefault(false, false);
	
	private boolean initial;
	private boolean sameInstances;
	private String toString;

	/**
	 * Create a new {@link ChangeDefault}
	 * @param initial
	 * 		True if the change is an initial change in propagation
	 * @param sameInstances
	 * 		True if all instances in the prop, or all instances in all props of
	 * a bean, are the same after the change
	 */
	private ChangeDefault(boolean initial, boolean sameInstances) {
		super();
		this.initial = initial;
		this.sameInstances = sameInstances;
		
		//Precalc toString() return
		toString = "Change, " + (initial?"initial":"consequent") + ", " + (sameInstances?"instance the same":"instance may have changed");
	}

	/**
	 * Get an instance of {@link ChangeDefault}
	 * @param initial
	 * 		True if the change is an initial change in propagation
	 * @param sameInstances
	 * 		True if all instances in the prop, or all instances in all props of
	 * a bean, are the same after the change
	 * @return
	 * 		An instance (shared, but immutable) of {@link ChangeDefault} 
	 */
	public final static ChangeDefault instance(boolean initial, boolean sameInstances) {
		if (initial) {
			if (sameInstances) {
				return TRUE_TRUE;
			} else {
				return TRUE_FALSE;
			}
		} else {
			if (sameInstances) {
				return FALSE_TRUE;
			} else {
				return FALSE_FALSE;
			}			
		}
	}
	
	@Override
	public final Change extend(Change existing) {
		//We only expect to be asked to extend another ChangeDefault
		if (existing.type() != type()) throw new IllegalArgumentException("BASE Change can only extend another BASE Change");
		
		//Extended change is initial if this or the existing change is initial.
		//Extended change retains same instances if both this and the existing change retain same instances
		ChangeDefault extendedChange = ChangeDefault.instance(initial() || existing.initial(), sameInstances() && existing.sameInstances());
		
		//If there is no extension of change (that is, if the extended change is the same as the
		//existing change), then return null
		//Note, == is correct, since there are only ever 4 instances, one for each possible combination of the two flags
		if (existing == extendedChange) {
			return null;
			
		//If an extension occurs, return the extended change
		} else {
			return extendedChange;
		}
	}

	@Override
	public final boolean initial() {
		return initial;
	}

	@Override
	public final boolean sameInstances() {
		return sameInstances;
	}

	@Override
	public final ChangeType type() {
		return ChangeType.BASE;
	}

	@Override
	public String toString() {
		return toString;
	}
	
}
