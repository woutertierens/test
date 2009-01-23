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
package org.jpropeller.properties.list.impl;

import java.util.List;
import java.util.Map;

import org.jpropeller.collection.ObservableList;
import org.jpropeller.info.PropInfo;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.GenericProp;
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
 * from a {@link GenericProp}, within an
 * {@link ObservableList} referenced by another {@link Prop}.
 * 
 * Note this does not expose a setter - if you want to expose the
 * set method of an {@link EditableProp}, use an {@link EditableListIndexProp}.
 * 
 * @param <T>
 * 		Type of value of the prop
 */
public class ListIndexProp<T> implements Prop<T> {

	ChangeableFeatures features;
	
	GenericProp<ObservableList<T>> listProp;
	GenericProp<Integer> indexProp;

	PropName<? extends Prop<T>, T> name;
	
	/**
	 * Create a {@link ListIndexProp}
	 * @param name
	 * 		The name of the prop
	 * @param listProp
	 * 		A {@link GenericProp} that contains the list we are indexing into
	 * @param indexProp
	 * 		A {@link GenericProp} that contains the index within the list
	 */
	public ListIndexProp(
			PropName<? extends Prop<T>, T> name,
			GenericProp<ObservableList<T>> listProp,
			GenericProp<Integer> indexProp
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

		//TODO see if we can reduce changes by checkign whether a change
		//to the list actually affects the indexed value
		//Currently, any change to the listProp or indexProp causes us to raise a change
		return ChangeDefault.instance(
				false,	//Not initial 
				false	//instances may have changed
				);
	}

	
	@Override
	public T get() {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		
		try {

			ObservableList<T> list = listProp.get();
			if (list == null) return null;
			
			Integer index = indexProp.get();
			if (index < 0 || index >= list.size()) return null;
			
			return list.get(index);
			
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
		}
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
