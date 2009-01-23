package org.jpropeller.bean;

import org.jpropeller.properties.change.impl.InternalChangeImplementation;

/**
 * Factory for {@link MutableBeanFeatures} and {@link ExtendedBeanFeatures} instances
 */
public interface BeanFeaturesFactory {

	/**
	 * Create a new {@link MutableBeanFeatures} instance for a given bean
	 * @param bean
	 * 		The bean by which this {@link MutableBeanFeatures} will be used
	 * @return
	 * 		new {@link MutableBeanFeatures}
	 */
	public MutableBeanFeatures createBeanFeatures(Bean bean);
	
	/**
	 * Create a new {@link ExtendedBeanFeatures} instance for a given bean
	 * @param bean
	 * 		The bean by which this {@link ExtendedBeanFeatures} will be used
	 * @return
	 * 		new {@link ExtendedBeanFeatures}
	 */
	public ExtendedBeanFeatures createExtendedBeanFeatures(Bean bean);
	
	/**
	 * Create a new {@link MutableBeanFeatures} instance for a given bean,
	 * with a specific internal change implementation
	 * @param bean
	 * 		The bean by which this {@link MutableBeanFeatures} will be used
	 * @param implementation
	 * 		The implementation of internal change response
	 * @return
	 * 		new {@link MutableBeanFeatures}
	 */
	public MutableBeanFeatures createBeanFeatures(Bean bean, InternalChangeImplementation implementation);

	/**
	 * Create a new {@link ExtendedBeanFeatures} instance for a given bean,
	 * with a specific internal change implementation
	 * @param bean
	 * 		The bean by which this {@link ExtendedBeanFeatures} will be used
	 * @param implementation
	 * 		The implementation of internal change response
	 * @return
	 * 		new {@link ExtendedBeanFeatures}
	 */
	public ExtendedBeanFeatures createExtendedBeanFeatures(Bean bean, InternalChangeImplementation implementation);
}
