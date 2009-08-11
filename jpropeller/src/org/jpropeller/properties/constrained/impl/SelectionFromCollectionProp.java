/*
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
package org.jpropeller.properties.constrained.impl;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

import org.jpropeller.collection.CCollection;
import org.jpropeller.constraint.Constraint;
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
 * A {@link Prop} which constrains its value when read,
 * based on a {@link Constraint} that calculates its value 
 * as needed based on the values of other {@link Changeable}s,
 * AND on any values set into the {@link SelectionFromCollectionProp} since
 * it was last read.
 * 
 * @param <T>		The type of the {@link Prop} value
 */
public class SelectionFromCollectionProp<T> implements Prop<T> {

	private T value = null;
	private boolean cacheValid = false;
	private final PropName<T> name;
	private final Prop<? extends CCollection<? extends T>> collectionProp;
	private final ChangeableFeaturesDefault features;

	private final boolean selectFirst;

	WeakReference<CCollection<? extends T>> currentCollection = null;
	
	/**
	 * Create a {@link SelectionFromCollectionProp}
	 * 
	 * This will ensure that its value is always an 
	 * element of the collection (or at least that 
	 * collection.contains(value) is always true).
	 * Hence changes to values that are not contained 
	 * in the collection are rejected (value is reset) 
	 * and if the list changes so that it no longer 
	 * contains the value, the value will also be reset. 
	 * 
	 * @param collectionProp	A {@link Prop} giving the 
	 * 							{@link CCollection} within which 
	 * 							the value of this prop must be contained
	 * 
	 * @param name				The name of the prop
	 * 
	 * @param selectFirst		Determines the behavious when value is 
	 * 							reset as described above. 
	 * 							If true, the value will be set to the 
	 * 							first item in the collection, or null 
	 * 							if the collection is empty.
	 * 							If false, the value will be nulled on 
	 * 							selection reset.
	 */
	public SelectionFromCollectionProp(PropName<T> name, Prop<? extends CCollection<? extends T>> collectionProp, boolean selectFirst) {
		this.name = name;
		this.collectionProp = collectionProp;
		this.selectFirst = selectFirst;
		
		features = new ChangeableFeaturesDefault(new InternalChangeImplementation() {
			@Override
			public Change internalChange(Changeable changed, Change change,
					List<Changeable> initial, Map<Changeable, Change> changes) {
				return internalChangeHandler(changed, change, initial, changes);
			}
		}, this); 

		collectionProp.features().addChangeableListener(this);
	}
	
	private Change internalChangeHandler(Changeable changed, Change change,
			List<Changeable> initial, Map<Changeable, Change> changes) {

		//Work out whether we have any of the conditions for a shallow change
		boolean shallowChange = false;

		//If there is a change to the instance in the collectionProp, then we
		//don't know our current collection any more, and we may have
		//a shallow change. We also need to recalculate when read.
		Change collectionPropChange = changes.get(collectionProp);
		if (collectionPropChange != null && (!collectionPropChange.sameInstances())) {
			currentCollection = null;
			shallowChange = true;
		}
		
		CCollection<? extends T> currentCollectionValue = null;
		if (currentCollection != null) {
			currentCollectionValue = currentCollection.get();
		}
		
		//If we don't know what collection we are in, we have to assume a shallow change
		if (currentCollectionValue == null) {
			shallowChange = true;
			
	    //If we do know what collection we are in, and the collection has a shallow change,
		//then we do too, and re need to recalculate when read.
		} else {
			Change collectionChange = changes.get(currentCollectionValue);
			if (collectionChange != null && (!collectionChange.sameInstances())) {
				shallowChange = true;
			}
		}
		
		//If we have had a shallow change, then we need to recalculate when read - the
		//list may have changed such that it might not contain our value any more.
		if (shallowChange) {
			cacheValid = false;
		}

		//Note that the only remaining option where the state of shallowChange 
		//has not been considered is where there is a change to the current value - 
		//note however that this is by definition a deep change, since the only
		//way of achieving a shallow change to this prop is to call set(), and the set()
		//method deals with this already - this is similar to the much simpler ChangeablePropDefault.
		
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
//		cacheValid = false;
//		return ChangeDefault.instance(
//				false,			//Not initial 
//				false			//Not same instances	
//				);
		
	}
	
	@Override
	public ChangeableFeatures features() {
		return features;
	}

	@Override
	public T get() {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		try {
			
			CCollection<? extends T> collection = collectionProp.get();
			
			//Note we will remember the current collection for as long as it is current,
			//to help filter changes
			currentCollection = new WeakReference<CCollection<? extends T>>(collection);
			
			//We avoid recalculating unless cache is invalid
			if (!cacheValid) {

				T oldValue = this.value;

				//Try to find the value in the collection,
				//comparing by reference
				boolean contained = false;
				if (collection != null) {
					for (T t : collection) {
						if (value == t) {
							contained = true;
							break;
						}
					}
				}
				
				//If the value is not in the collection, then
				//choose a new value appropriately
				if (!contained) {
					if ((collection != null) && (!collection.isEmpty()) && selectFirst) {
						value = collection.iterator().next();
					} else {
						value = null;
					}
				}
				
				//If value changes, listen to new value
				adjustListeners(oldValue, value);
				
				cacheValid = true;
			}
			
			return value;
			
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
		}
	}
	
	@Override
	public void set(T value) {
		
		Props.getPropSystem().getChangeSystem().prepareChange(this);
		try {
			
			//Note we will remember the current collection for as long as it is current,
			//to help filter changes
			currentCollection = new WeakReference<CCollection<? extends T>>(collectionProp.get());
			
			T oldValue = this.value;
			
			this.value = value;
			
			//If value changes, listen to new value
			adjustListeners(oldValue, value);
			
			//We will need to recalculate when read - 
			//don't know if value is in collection
			cacheValid = false;
			
			//Propagate the change we just made
			Props.getPropSystem().getChangeSystem().propagateChange(this, ChangeDefault.instance(
					true,	//Change IS initial 
					false	//Instance is NOT the same - new value set
					));
			
		//Always conclude change
		} finally {
			Props.getPropSystem().getChangeSystem().concludeChange(this);
		}
	}

	@Override
	public String toString() {
		return "Selection From Collection Prop '" + getName().getString() + "' = '" + get() + "'";
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

	private void adjustListeners(T oldValue, T value) {
		if (oldValue != value) {
			//We need to stop listening to the old value, and start listening to the new one
			if (oldValue != null && oldValue instanceof Changeable) {
				((Changeable)oldValue).features().removeChangeableListener(this);
			}
			if (value != null && value instanceof Changeable) {
				((Changeable)value).features().addChangeableListener(this);
			}
		}
	}
	
}
