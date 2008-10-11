package org.jpropeller.system;

import org.jpropeller.bean.Bean;
import org.jpropeller.map.ExtendedPropMap;
import org.jpropeller.map.PropMap;
import org.jpropeller.map.PropMapFactory;
import org.jpropeller.map.PropMapMutable;
import org.jpropeller.view.update.UpdateManager;

/**
 * Provides the various global elements required by the JPropeller
 * system 
 */
public interface PropSystem {

	/**
	 * Get the system-wide {@link UpdateManager} instance
	 * @return
	 * 		The {@link UpdateManager}
	 */
	public UpdateManager getUpdateManager();
	
	/**
	 * Get the sytem-wide {@link PropMapFactory} instance
	 * @return
	 * 		The {@link PropMapFactory}
	 */
	public PropMapFactory getPropMapFactory();
	
	/**
	 * Create a new {@link PropMapMutable} instance for a given bean
	 * This is a convenience method equivalent to calling 
	 * {@link PropMapFactory#createPropMap(Bean)} on the
	 * factory returned by {@link #getPropMapFactory()}
	 * @param bean
	 * 		The bean by which this {@link PropMap} will be used
	 * @return
	 * 		new {@link PropMap}
	 */
	public PropMapMutable createPropMap(Bean bean);
	
	/**
	 * Create a new {@link ExtendedPropMap} instance for a given bean
	 * This is a convenience method equivalent to calling 
	 * {@link PropMapFactory#createExtendedPropMap(Bean)} on the
	 * factory returned by {@link #getPropMapFactory()}
	 * @param bean
	 * 		The bean by which this {@link PropMap} will be used
	 * @return
	 * 		new {@link ExtendedPropMap}
	 */
	public ExtendedPropMap createExtendedPropMap(Bean bean);
}
