package org.jpropeller.map;

import org.jpropeller.bean.Bean;

/**
 * Factory for {@link PropMapMutable} instances
 */
public interface PropMapFactory {

	/**
	 * Create a new {@link PropMapMutable} instance for a given bean
	 * @param bean
	 * 		The bean by which this {@link PropMapMutable} will be used
	 * @return
	 * 		new {@link PropMapMutable}
	 */
	public PropMapMutable createPropMap(Bean bean);
	
	/**
	 * Create a new {@link ExtendedPropMap} instance for a given bean
	 * @param bean
	 * 		The bean by which this {@link PropMapMutable} will be used
	 * @return
	 * 		new {@link PropMapMutable}
	 */
	public ExtendedPropMap createExtendedPropMap(Bean bean);
}
