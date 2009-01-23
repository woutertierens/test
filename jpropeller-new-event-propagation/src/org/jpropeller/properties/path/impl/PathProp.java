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
package org.jpropeller.properties.path.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.jpropeller.bean.Bean;
import org.jpropeller.info.PropInfo;
import org.jpropeller.name.PropName;
import org.jpropeller.path.BeanPath;
import org.jpropeller.path.BeanPathIterator;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.GenericProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.ChangeableFeatures;
import org.jpropeller.properties.change.impl.ChangeDefault;
import org.jpropeller.properties.change.impl.ChangeableFeaturesDefault;
import org.jpropeller.properties.change.impl.InternalChangeImplementation;
import org.jpropeller.system.Props;
import org.jpropeller.util.GeneralUtils;

/**
 * A {@link Prop} that mirrors the value of another {@link Prop}. The clever
 * thing about this is that a {@link PathProp} always mirrors the {@link Prop}
 * at a given path relative to a root {@link Bean}.
 * 
 * Note this does not expose a setter - if you want to expose the
 * set method of an {@link EditableProp}, use an {@link EditablePathProp}.
 * 
 * @param <R>
 * 		The type of root bean we must start from
 * @param <T>
 * 		Type of value of the prop
 */
public class PathProp<R extends Bean, T> implements Prop<T> {

	private final static Logger logger = GeneralUtils.logger(PathProp.class);
	
	Bean bean;
	ChangeableFeatures features;
	PropName<? extends Prop<T>, T> name;
	
	R pathRoot;
	
	BeanPath<? super R, ? extends GenericProp<T>, T> path;
	
	GenericProp<T> cachedProp = null;
	boolean cacheValid = false;
	boolean errored = false;

	Set<GeneralProp<?>> cachedPathProps = new HashSet<GeneralProp<?>>();
	
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
	public PathProp(
			PropName<? extends Prop<T>, T> name, 
			R pathRoot, 
			BeanPath<? super R, ? extends GenericProp<T>, T> path) {
		this.name = name;
		this.pathRoot = pathRoot;
		this.path = path;
		
		features = new ChangeableFeaturesDefault(new InternalChangeImplementation() {
			@Override
			public Change internalChange(Changeable changed, Change change,
					List<Changeable> initial, Map<Changeable, Change> changes) {
				return internalChangeHandler(changed, change, initial, changes);
			}
		}, this);
		
		//We need to listen to the pathRoot, and if it changes, 
		//mark the cache dirty as appropriate
		pathRoot.features().addChangeableListener(this);
	}

	@Override
	public ChangeableFeatures features() {
		return features;
	}

	private Change internalChangeHandler(Changeable changed, Change change,
			List<Changeable> initial, Map<Changeable, Change> changes) {
	
		//logger.finest("Saw change to " + changed + ", cacheValid? " + cacheValid + ", errored? " + errored);
		
		//If the cache is not valid, then we do not know which properties we used to
		//reach our mirrored property, so we should just report a change to be safe
		if (!cacheValid || errored) {
			logger.warning("PathProp has invalid path, responding to propChanged, firing consequent change for safety");
			return ChangeDefault.instance(
					false,	//Not initial 
					false	//instances may have changed
					);
		}
		
		//We have a valid cache, so we know which path we used to follow to find the
		//mirrored property
		
		//The cache is still valid UNLESS a shallow change has occurred to one of the props
		//we used to follow to reach our mirrored property
		//If none of these props has had a shallow change (had set() called or otherwise 
		//changed its value) then the path to our mirrored property is still the same. Deep
		//changes to them do not change the path we follow
		boolean pathInvalid = false;
		for (GeneralProp<?> cachedPathProp : cachedPathProps) {
			Change cachedChange = changes.get(cachedPathProp);
			if ((cachedChange!=null) && (!cachedChange.sameInstances())) {
				pathInvalid = true;
			}
		}
		if (pathInvalid) {
			cacheValid = false;
			//logger.finest("PathProp CACHE REBUILD");

			//This also obviously means we have a change
			return ChangeDefault.instance(
					false,	//Not initial 
					false	//instances may have changed
					);
		}
		
		//We still have a valid cache, but we may have a change to our mirrored prop - see
		//if changes contains the cached mirrored prop
		if (changes.containsKey(cachedProp)) {
			//logger.finest("PathProp CHANGE, no cache rebuild");
			return ChangeDefault.instance(
					false,	//Not initial 
					changes.get(cachedProp).sameInstances()	//instances have changed only if they have changed in mirrored prop
					);
		}
		
		//Otherwise we haven't actually had a change to the mirrored prop - just another change
		//that is visible via the root bean, so nothing to do
		//logger.finest("PathProp ignored change");
		return null;
	}

	
	@Override
	public T get() {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		
		try {
			if ((!cacheValid) || (errored)) {
				rebuildCache();
			}
			
			if ((!cacheValid) || (errored)) {
				//TODO decide whether it is best to throw exception or just return null on invalid path
				//throw new PathPropException("Cannot look up property via path");
				return null;
			}
			
			return cachedProp.get();
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
		}
	}

	void rebuildCache() {
		
		//logger.finest("About to rebuildCache()");
		
		//Unless we complete, cache is invalid, and we are errored
		cacheValid = false;
		errored = true;
		
		//Start from root of path, and clear cache of path props followed
		Bean currentBean = pathRoot;
		
		if (currentBean == null) {
			logger.warning("pathRoot null");
		}

		//Stop listening to old cache, and clear it
		for (GeneralProp<?> cachedPathProp : cachedPathProps) {
			cachedPathProp.features().removeChangeableListener(this);
		}
		cachedPathProps.clear();
		
		//Iterate the path from our root
		BeanPathIterator<? extends GenericProp<T>, T> iterator = path.iteratorFrom(pathRoot);

		//Go through all but last stage, caching each prop we visit.
		while (iterator.hasNext()) {
			GeneralProp<?> prop = iterator.next();

			//If we fail to look up a property, give up
			if (prop == null) {
				logger.warning("Failed to look up property in path");
				return;
			}
			
			cachedPathProps.add(prop);
			
			prop.features().addChangeableListener(this);
		}
		
		//Follow the last stage
		GenericProp<T> finalProp = iterator.finalProp();

		//If we fail to look up final property, give up
		if (finalProp == null) {
			logger.warning("Failed to look up final property in path");
			return;
		}

		//logger.finest("last name '" + path.getLastName() + "' to value '" + finalProp.get() + "'");

		//Before we change cachedProp, stop listening to it
		if (cachedProp != null) {
			cachedProp.features().removeChangeableListener(this);
		}
		
		//We are done, so store the prop, and mark success
		cachedProp = finalProp;
		errored = false;
		cacheValid = true;
		
		//List to the new cachedProp
		cachedProp.features().addChangeableListener(this);
		
		//logger.finest("Cache successfully rebuilt");

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
