package org.jpropeller.properties.change.impl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jpropeller.collection.ListDelta;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeType;
import org.jpropeller.properties.change.ListChange;

/**
 * Immutable implementation of {@link ListChange}
 */
public class ListChangeDefault implements ListChange {

	boolean initial;
	boolean sameInstances;
	List<ListDelta> listDeltas;

	/**
	 * Make a new {@link ListChangeDefault}
	 * @param initial
	 * 		True if the change is an initial change in propagation
	 * @param sameInstances
	 * 		True if all instances in the list are the same after the change
	 * @param listDeltas
	 * 		The deltas that have occurred to the list
	 */
	public ListChangeDefault(boolean initial, boolean sameInstances,
			List<ListDelta> listDeltas) {
		super();
		this.initial = initial;
		this.sameInstances = sameInstances;
		this.listDeltas = listDeltas;
	}

	/**
	 * Make a new {@link ListChangeDefault} with a single delta
	 * @param initial
	 * 		True if the change is an initial change in propagation
	 * @param sameInstances
	 * 		True if all instances in the list are the same after the change
	 * @param delta
	 * 		The delta that has occurred to the list
	 */
	public ListChangeDefault(boolean initial, boolean sameInstances, ListDelta delta) {
		this(initial, sameInstances, makeSingleList(delta));
	}
	
	private static List<ListDelta> makeSingleList(ListDelta delta) {
		List<ListDelta> list = new LinkedList<ListDelta>();
		list.add(delta);
		return Collections.unmodifiableList(list);
	}
	
	@Override
	public Change extend(Change existing) {
		
		if (existing.type() != ChangeType.LIST) {
			throw new IllegalArgumentException("A LIST Change can only extend another LIST change");
		}
		
		ListChange listExisting = (ListChange)existing;
		
		//Extend different aspects of the change
		boolean extendsInitial = initial() || existing.initial();
		boolean extendsSameInstances = sameInstances() && existing.sameInstances();
		
		//Extend the delta list by adding our own deltas to the end
		LinkedList<ListDelta> extendsDeltas = new LinkedList<ListDelta>(listExisting.getListDeltas());
		extendsDeltas.addAll(getListDeltas());
		
		return new ListChangeDefault(extendsInitial, extendsSameInstances, extendsDeltas);
	}
	
	@Override
	public List<ListDelta> getListDeltas() {
		return listDeltas;
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
		return ChangeType.LIST;
	}

	@Override
	public String toString() {
		return "List Change, " + (initial?"initial":"consequent") + ", " + (sameInstances?"instance(s) the same":"instance(s) may have changed") + ", " + getListDeltas().toString();
	}
	
}
