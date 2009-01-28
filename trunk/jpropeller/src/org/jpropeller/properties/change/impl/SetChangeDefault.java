package org.jpropeller.properties.change.impl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jpropeller.collection.SetDelta;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeType;
import org.jpropeller.properties.change.SetChange;

/**
 * Immutable implementation of {@link SetChange}
 */
public class SetChangeDefault implements SetChange {

	boolean initial;
	boolean sameInstances;
	List<SetDelta> deltas;

	/**
	 * Make a new {@link SetChangeDefault}
	 * @param initial
	 * 		True if the change is an initial change in propagation
	 * @param sameInstances
	 * 		True if all instances in the {@link Set} are the same after the change
	 * @param listDeltas
	 * 		The deltas that have occurred to the {@link Set}
	 */
	public SetChangeDefault(boolean initial, boolean sameInstances,
			List<SetDelta> listDeltas) {
		super();
		this.initial = initial;
		this.sameInstances = sameInstances;
		this.deltas = listDeltas;
	}

	/**
	 * Make a new {@link SetChangeDefault} with a single delta
	 * @param initial
	 * 		True if the change is an initial change in propagation
	 * @param sameInstances
	 * 		True if all instances in the {@link Set} are the same after the change
	 * @param delta
	 * 		The delta that has occurred to the {@link Set}
	 */
	public SetChangeDefault(boolean initial, boolean sameInstances, SetDelta delta) {
		this(initial, sameInstances, makeSingleList(delta));
	}
	
	private static List<SetDelta> makeSingleList(SetDelta delta) {
		List<SetDelta> list = new LinkedList<SetDelta>();
		list.add(delta);
		return Collections.unmodifiableList(list);
	}
	
	@Override
	public Change extend(Change existing) {
		
		if (existing.type() != ChangeType.SET) {
			throw new IllegalArgumentException("A SET Change can only extend another SET change");
		}
		
		SetChange setExisting = (SetChange)existing;
		
		//Extend different aspects of the change
		boolean extendsInitial = initial() || existing.initial();
		boolean extendsSameInstances = sameInstances() && existing.sameInstances();
		
		//Extend the delta list by adding our own deltas to the end
		LinkedList<SetDelta> extendsDeltas = new LinkedList<SetDelta>(setExisting.getSetDeltas());
		extendsDeltas.addAll(getSetDeltas());
		
		return new SetChangeDefault(extendsInitial, extendsSameInstances, extendsDeltas);
	}
	
	@Override
	public List<SetDelta> getSetDeltas() {
		return deltas;
	}

	@Override
	public boolean initial() {
		return initial;
	}

	@Override
	public boolean sameInstances() {
		return sameInstances;
	}

	@Override
	public ChangeType type() {
		return ChangeType.SET;
	}

	@Override
	public String toString() {
		return "Set Change, " + (initial?"initial":"consequent") + ", " + (sameInstances?"instance(s) the same":"instance(s) may have changed") + ", " + getSetDeltas().toString();
	}
	
}
