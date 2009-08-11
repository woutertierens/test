package org.jpropeller.properties.change.impl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jpropeller.collection.MapDelta;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeType;
import org.jpropeller.properties.change.MapChange;

/**
 * Immutable implementation of {@link MapChange}
 */
public class MapChangeDefault implements MapChange {

	boolean initial;
	boolean sameInstances;
	List<MapDelta> mapDeltas;

	/**
	 * Make a new {@link MapChangeDefault}
	 * @param initial
	 * 		True if the change is an initial change in propagation
	 * @param sameInstances
	 * 		True if all instances in the list are the same after the change
	 * @param mapDeltas
	 * 		The deltas that have occurred to the map
	 */
	public MapChangeDefault(boolean initial, boolean sameInstances,
			List<MapDelta> mapDeltas) {
		super();
		this.initial = initial;
		this.sameInstances = sameInstances;
		this.mapDeltas = mapDeltas;
	}
	
	/**
	 * Make a new {@link MapChangeDefault} with a single {@link MapDelta}
	 * @param initial
	 * 		True if the change is an initial change in propagation
	 * @param sameInstances
	 * 		True if all instances in the list are the same after the change
	 * @param mapDelta
	 * 		The delta that has occurred to the map
	 */
	public MapChangeDefault(boolean initial, boolean sameInstances, MapDelta mapDelta) {
		this(initial, sameInstances, makeSingleList(mapDelta));
	}
	
	private static List<MapDelta> makeSingleList(MapDelta delta) {
		List<MapDelta> list = new LinkedList<MapDelta>();
		list.add(delta);
		return Collections.unmodifiableList(list);
	}

	@Override
	public Change extend(Change existing) {
		
		if (existing.type() != ChangeType.MAP) {
			throw new IllegalArgumentException("A MAP Change can only extend another MAP change");
		}
		
		MapChange mapExisting = (MapChange)existing;
		
		//Extend different aspects of the change
		boolean extendsInitial = initial() || existing.initial();
		boolean extendsSameInstances = sameInstances() && existing.sameInstances();
		
		//Extend the delta list by adding our own deltas to the end
		LinkedList<MapDelta> extendsDeltas = new LinkedList<MapDelta>(mapExisting.getMapDeltas());
		extendsDeltas.addAll(getMapDeltas());

		return new MapChangeDefault(extendsInitial, extendsSameInstances, extendsDeltas);
	}
	
	@Override
	public List<MapDelta> getMapDeltas() {
		return mapDeltas;
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
		return ChangeType.MAP;
	}

	@Override
	public String toString() {
		return "Map Change, " + (initial?"initial":"consequent") + ", " + (sameInstances?"instance(s) the same":"instance(s) may have changed") + ", " + getMapDeltas().toString();
	}

}
