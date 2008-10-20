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
import java.util.Set;
import java.util.logging.Logger;

import org.jpropeller.bean.Bean;
import org.jpropeller.info.PropInfo;
import org.jpropeller.map.PropMap;
import org.jpropeller.name.PropName;
import org.jpropeller.path.BeanPath;
import org.jpropeller.path.BeanPathIterator;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.GenericProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.event.PropEvent;
import org.jpropeller.properties.event.PropEventOrigin;
import org.jpropeller.properties.event.PropInternalListener;
import org.jpropeller.properties.event.impl.PropEventDefault;

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
public class PathProp<R extends Bean, T> implements Prop<T>, PropInternalListener {

	private final static Logger logger = Logger.getLogger(PathProp.class.toString());
	
	PropMap propMap;
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
		
		//We need to listen to the pathRoot, and if it changes, 
		//mark the cache dirty as appropriate
		pathRoot.props().addInternalListener(this);
	}

	@Override
	public <S> void propInternalChanged(PropEvent<S> event) {
		//Ignore events where this path property is the root cause - we are not interested
		//in our own consequent changes
		if (event.getRootEvent().getProp() == this) return;
		
		//If the cache is not valid, then we do not know which properties we used to
		//reach our mirrored property, so we should just report a change to be safe
		if (!cacheValid || errored) {
			
			logger.warning("PathProp has invalid path, responding to propChanged, firing consequent change for safety");
			
			//FIXME
			//We need to check this behaviour - in fact we only really change when the path becomes complete again.
			//However we can't actually check whether the path is complete without calling getters, which is
			//forbidden. However, if we always respond to a change by firing one, we can get cycles of
			//firing between pathprops
			props().propChanged(new PropEventDefault<T>(this, PropEventOrigin.CONSEQUENCE));
			return;
		}
		
		//We have a valid cache, so we know which path we used to follow to find the
		//mirrored property
		
		//The cache is still valid UNLESS the root change was to one of the props
		//we used to follow to reach our mirrored property
		//If none of these props has had a shallow change (had set() called or otherwise 
		//changed its value) then the path to our mirrored property is still the same. Deep
		//changes to them do not change the path we follow
		if (cachedPathProps.contains(event.getRootEvent().getProp())) {
			cacheValid = false;
			//logger.finest("PathProp CACHE REBUILD");

			//This also obviously means we have a change - this may be shallow so send shallow event
			props().propChanged(new PropEventDefault<T>(this, PropEventOrigin.CONSEQUENCE));
			return;
		}
		
		//We still have a valid cache, but we may have a change to our mirrored prop - 
		//this is the case if the set of props the event has passed through includes
		//our mirrored prop
		if (event.getPropsInChain().contains(cachedProp)) {
			//OPTIMISATION can we sometimes throw a deep consequent change where the mirrored prop
			//has had only a deep change?
			//logger.finest("PathProp CHANGE, no cache rebuild");
			props().propChanged(new PropEventDefault<T>(this, PropEventOrigin.CONSEQUENCE));
		}
		
		//Otherwise we haven't actually had a change to the mirrored prop - just another change
		//that is visible via the root bean, so nothing to do
		//logger.finest("PathProp ignored change");
	}

	
	@Override
	public T get() {
		
		if ((!cacheValid) || (errored)) {
			rebuildCache();
		}
		
		if ((!cacheValid) || (errored)) {
			throw new PathPropException("Cannot look up property via path");
		}
		
		return cachedProp.get();
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

		//Clear old cache
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
		}
		
		//Follow the last stage
		GenericProp<T> finalProp = iterator.finalProp();

		//logger.finest("last name '" + path.getLastName() + "' to value '" + finalProp.get() + "'");

		//We are done, so store the prop, and mark success
		cachedProp = finalProp;
		errored = false;
		cacheValid = true;
		
		//logger.finest("Cache successfully rebuilt");

	}
	
	@Override
	public void setPropMap(PropMap set) {
		if (propMap != null) throw new IllegalArgumentException("Prop '" + this + "' already has its PropMap set to '" + propMap + "'");
		if (set == null) throw new IllegalArgumentException("PropMap must be non-null");
		this.propMap = set;
	}

	@Override
	public Bean getBean() {
		return props().getBean();
	}

	@Override
	public PropName<? extends Prop<T>, T> getName() {
		return name;
	}

	@Override
	public PropMap props() {
		return propMap;
	}

	@Override
	public PropInfo getInfo() {
		return PropInfo.DEFAULT;		
	}

}
