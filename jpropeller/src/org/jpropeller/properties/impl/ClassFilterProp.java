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
package org.jpropeller.properties.impl;

import java.util.List;
import java.util.Map;

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
 * A {@link Prop} which mirrors the value of another prop
 * when that prop has a value matching a given class, and is
 * null otherwise.
 * 
 * This essentially allows casting of a {@link Prop} from one
 * value type to another, where the casting returns null if not
 * possible.
 * 
 * @param <T>
 * 		The type of the {@link Prop} value
 */
public class ClassFilterProp<T> implements Prop<T> {

	final Prop<? super T> filteredProp;
	PropName<T> name;
	ChangeableFeaturesDefault features;
	Class<T> requiredClass;
	
	/**
	 * Create a {@link ClassFilterProp}
	 * 
	 * @param name			The name of the prop
	 * @param filteredProp	The prop mirrored by this prop, 
	 * 						when it matches the required class
	 * @param requiredClass	The required class for mirroring
	 */
	public ClassFilterProp(PropName<T> name, final Prop<? super T> filteredProp, Class<T> requiredClass) {
		this.name = name;
		this.filteredProp = filteredProp;
		this.requiredClass = requiredClass;
		
		features = new ChangeableFeaturesDefault(new InternalChangeImplementation() {
			@Override
			public Change internalChange(Changeable changed, Change change,
					List<Changeable> initial, Map<Changeable, Change> changes) {
				
				boolean sameInstances = changes.get(filteredProp).sameInstances();
				
				//We may have a change whenever the mirrored prop has a change,
				return ChangeDefault.instance(
						false,	//Change is consequence of filteredProp change
						
						sameInstances	//We point to same instance if the 
										//filtered prop does - either we are
										//mirroring and will mirror the same
										//instance, or we are null and will
										//stay so. If filtered prop has a deep
										//change, we may not (if we stay null),
										//but it is acceptable to report different
										//instances when instance is in fact the same
						);
			}
		}, this); 

		filteredProp.features().addChangeableListener(this);
	}
	
	@Override
	public ChangeableFeatures features() {
		return features;
	}

	//We check that T is assignable from fVal, before casting
	@SuppressWarnings("unchecked")
	@Override
	public T get() {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		try {
			//Get value from filtered prop
			Object fVal = filteredProp.get();
			if (fVal == null) return null;
			
			if (requiredClass.isAssignableFrom(fVal.getClass())) {
				return (T)fVal;
			} else {
				return null;
			}
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
		}
	}

	@Override
	public String toString() {
		return "Filtered Prop '" + getName().getString() + "' = '" + get() + "'";
	}
	
	@Override
	public PropName<T> getName() {
		return name;
	}

	@Override
	public PropEditability getEditability() {
		return filteredProp.getEditability();
	}

	@Override
	public PropAccessType getAccessType() {
		return PropAccessType.SINGLE;
	}

	@Override
	public void set(T value) throws UnsupportedOperationException {
		filteredProp.set(value);
	}
	
}
