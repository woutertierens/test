/*
 *  $Id: PathProp.java,v 1.1 2008/03/24 11:19:51 shingoki Exp $
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
package org.jpropeller.properties.list.selection.impl;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

import org.jpropeller.collection.CList;
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
import org.jpropeller.system.Props;

/**
 * A {@link Prop} that mirrors the value of a specific index 
 * from a {@link Prop}, within an
 * {@link CList} referenced by another {@link Prop}.
 * 
 * Note that this {@link Prop} appears to be editable at all times,
 * but may fail to {@link #set(Object)} if the mirrored {@link Prop}
 * is not editable.
 * 
 * @param <T>	Type of value of the prop
 */
public class ListIndexProp<T> implements Prop<T> {

	ChangeableFeatures features;
	
	Prop<CList<T>> listProp;
	Prop<Integer> indexProp;

	PropName<T> name;
	WeakReference<CList<T>> currentList = null;
	
	/**
	 * Create a {@link ListIndexProp}
	 * @param name		The name of the prop
	 * @param listProp	A {@link Prop} that contains the list we are indexing into
	 * @param indexProp	A {@link Prop} that contains the index within the list
	 */
	public ListIndexProp(
			PropName<T> name,
			Prop<CList<T>> listProp,
			Prop<Integer> indexProp
			) {
		this.name = name;
		this.listProp = listProp;
		this.indexProp = indexProp;
		
		features = new ChangeableFeaturesDefault(new InternalChangeImplementation() {
			@Override
			public Change internalChange(Changeable changed, Change change,
					List<Changeable> initial, Map<Changeable, Change> changes) {
				return internalChangeHandler(changed, change, initial, changes);
			}
		}, this);
		
		//We need to listen to the listProp and indexProp, and if either changes, 
		//mark the cache dirty as appropriate
		listProp.features().addChangeableListener(this);
		indexProp.features().addChangeableListener(this);
	}

	@Override
	public ChangeableFeatures features() {
		return features;
	}

	private Change internalChangeHandler(Changeable changed, Change change,
			List<Changeable> initial, Map<Changeable, Change> changes) {

		//Work out whether we have any of the conditions for a shallow change
		boolean shallowChange = false;

		//If there is a change to the instance in the listProp, then we
		//don't know our current list any more, and we may have
		//a shallow change
		Change listPropChange = changes.get(listProp);
		if (listPropChange != null && (!listPropChange.sameInstances())) {
			currentList = null;
			shallowChange = true;
		}

		//If there is a change to the indexProp, then we are selecting
		//a new element of the list, so may have a shallow change
		if (changes.containsKey(indexProp)) {
			shallowChange = true;
		}
		
		CList<T> currentListValue = null;
		if (currentList != null) {
			currentListValue = currentList.get();
		}
		
		//If we don't know what list we are in, we have to assume a shallow change
		if (currentListValue == null) {
			shallowChange = true;
			
	    //If we do know what list we are in, and the list has a shallow change,
		//then we do too.
		} else {
			Change listChange = changes.get(currentListValue);
			if (listChange != null && (!listChange.sameInstances())) {
				shallowChange = true;
			}
		}

		return ChangeDefault.instance(
				false,			//Not initial 
				!shallowChange	
				);

		//TODO this could be extended so as not to throw a change at all, if the
		//list change gives a specific Changeable in the list that has changed, 
		//and that Changeable is not the one we are displaying. This requires
		//caching a reference to the Changeable we are displaying, and clearing
		//that reference whenever we get a !sameInstances list change, or an
		//indexProp change (that is, whenever we return a not same instance change
		//at present).

		//OPTIMISATION
		//The code above is heavily optimised to propagate sameInstances changes
		//whenever possible. This greatly reduces the amount of change propagation
		//when used in conjunction with path props etc., since many other Changeables
		//can optimise in the case where a change is with same instances.
		//The unoptimised code is below:
		//
//		return ChangeDefault.instance(
//				false,			//Not initial 
//				false			//Not same instances	
//				);
		
	}

	
	@Override
	public T get() {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		
		try {

			CList<T> list = listProp.get();

			//Note we will remember the current list for as long as it is current,
			//to help filter changes
			currentList = new WeakReference<CList<T>>(list);
			
			if (list == null) return null;
			
			Integer index = indexProp.get();
			if (index < 0 || index >= list.size()) return null;
			
			return list.get(index);
			
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
		}
	}

	@Override
	public PropName<T> getName() {
		return name;
	}

	@Override
	public PropEditability getEditability() {
		return PropEditability.EDITABLE;
	}

	@Override
	public PropAccessType getAccessType() {
		return PropAccessType.SINGLE;
	}
	
	@Override
	public void set(T value) throws UnsupportedOperationException {
		Props.getPropSystem().getChangeSystem().prepareChange(this);
		
		try {
			
			CList<T> list = listProp.get();
			
			currentList = new WeakReference<CList<T>>(list);

			if (list == null) return;
			
			Integer index = indexProp.get();
			if (index < 0 || index >= list.size()) return;
			
			list.set(index, value);

			//Note we don't propagate the change - we know that the list will propagate it for us,
			//then we will notice it and report that we have changed too. This avoids having two "initial"
			//changes from one actual change
			
		} finally {
			Props.getPropSystem().getChangeSystem().concludeChange(this);
		}
	}
	
	/*
	public final static void main(String[] args) {
		System.out.println("Creating");
		LotsOfProps l = new LotsOfProps();
		ListProp<LotsOfProps> listProp = ListPropDefault.editable("list", LotsOfProps.class);
		Prop<Integer> indexProp = PropImmutable.editable("index", 0);
		ListIndexProp<LotsOfProps> selected = new ListIndexProp<LotsOfProps>(PropName.create("selected", LotsOfProps.class), listProp, indexProp);
		selected.debug = true;

		System.out.println();
		System.out.println("Adding l");
		listProp.add(l);
		
		System.out.println();
		System.out.println("Selecting l");
		indexProp.set(0);
		
		System.out.println();
		System.out.println("Getting selected");
		System.out.println(selected.get());

		System.out.println();
		System.out.println("Altering l");
		l.booleanProp().set(false);
		
		System.out.println();
		System.out.println("Removing l");
		listProp.get().remove(l);
	}
	 */
}
