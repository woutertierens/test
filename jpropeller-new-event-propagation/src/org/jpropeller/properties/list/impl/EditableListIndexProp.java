/*
 *  $Id: MutablePathProp.java,v 1.1 2008/03/24 11:19:53 shingoki Exp $
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

import org.jpropeller.collection.ObservableList;
import org.jpropeller.info.PropInfo;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.GenericProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.system.Props;

/**
 * An editable version of {@link ListIndexProp}.
 * Note that this will only succeed in setting values if the
 * {@link ObservableList} is also editable (supports {@link ObservableList#set(int, Object)}
 * operation)
 * 
 * @param <T>
 * 		The type of value in the {@link ObservableList}, and in this {@link Prop}
 */
public class EditableListIndexProp<T> extends ListIndexProp<T> implements EditableProp<T> {

	PropName<EditableProp<T>, T> editableName;

	/**
	 * Create an {@link EditableListIndexProp}
	 * @param name
	 * 		The name of the prop
	 * @param listProp
	 * 		A {@link GenericProp} that contains the list we are indexing into
	 * @param indexProp
	 * 		A {@link GenericProp} that contains the index within the list
	 */
	public EditableListIndexProp(PropName<EditableProp<T>, T> name,
			GenericProp<ObservableList<T>> listProp,
			GenericProp<Integer> indexProp) {
		super(name, listProp, indexProp);
		editableName = name;
	}

	@Override
	public PropName<EditableProp<T>, T> getName() {
		return editableName;
	}

	@Override
	public PropInfo getInfo() {
		return PropInfo.EDITABLE;
	}

	@Override
	public void set(T value) throws UnsupportedOperationException {
		Props.getPropSystem().getChangeSystem().prepareChange(this);
		
		try {
			
			ObservableList<T> list = listProp.get();
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

}
