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
package org.jpropeller.properties.path.impl;

import org.jpropeller.bean.Bean;
import org.jpropeller.info.PropInfo;
import org.jpropeller.name.PropName;
import org.jpropeller.path.BeanPath;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.Prop;

/**
 * A {@link Prop} that mirrors the value of another {@link Prop}. The clever
 * thing about this is that a {@link PathProp} always mirrors the {@link Prop}
 * at a given path relative to a root {@link Bean}.
 * 
 * This uses an editable {@link BeanPath} to ensure that an {@link EditableProp}
 * is looked up, and so this {@link Prop} is itself an {@link EditableProp}
 * 
 * @author shingoki
 *
 * @param <T>
 * 		The type of value in the property
 */
public class EditablePathProp<T> extends PathProp<T> implements EditableProp<T> {

	PropName<EditableProp<T>, T> editableName;
	
	BeanPath<? extends EditableProp<T>, T> path;

	/**
	 * Create a prop which always has the same value as, and reports changes to,
	 * another prop. The other prop is looked up in a given root bean, using a given
	 * path to follow through properties of the root bean and its children.
	 * @param name
	 * 		The name of the prop
	 * @param pathRoot
	 * 		The root bean for the relative lookup
	 * @param path
	 * 		The name for the relative lookup
	 */
	public EditablePathProp(
			PropName<EditableProp<T>, T> name, 
			Bean pathRoot, 
			BeanPath<? extends EditableProp<T>, T> path) {
		super(name, pathRoot, path);
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
	public void set(T value) {
		
		if ((!cacheValid) || (errored)) {
			rebuildCache();
		}
		
		if ((!cacheValid) || (errored)) {
			throw new PathPropException("Cannot look up property via path");
		}
		
		//We know that the prop is editable, since we looked it up using a PropName<EditableProp<T>, T>
		//from the BeanPath. What we don't know is why eclipse and javac don't notice and warn
		//about the cast.
		((EditableProp<T>)cachedProp).set(value);
	}

}
