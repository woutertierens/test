package org.jpropeller.system;

import org.jpropeller.bean.Bean;
import org.jpropeller.bean.BeanFeaturesFactory;
import org.jpropeller.bean.ExtendedBeanFeatures;
import org.jpropeller.bean.MutableBeanFeatures;
import org.jpropeller.properties.change.ChangeSystem;
import org.jpropeller.properties.change.impl.InternalChangeImplementation;
import org.jpropeller.view.update.UpdateManager;

/**
 * Provides the various global elements required by the JPropeller
 * system 
 */
public interface PropSystem {

	/**
	 * Get the system-wide {@link UpdateManager} instance, this must not
	 * change after it is first called.
	 * @return
	 * 		The {@link UpdateManager}
	 */
	public UpdateManager getUpdateManager();

	/**
	 * Get the system-wide {@link ChangeSystem} instance, this must not
	 * change after it is first called.
	 * @return
	 * 		The {@link ChangeSystem}
	 */
	public ChangeSystem getChangeSystem();
	
	/**
	 * Get the current system-wide {@link BeanFeaturesFactory} instance
	 * @return
	 * 		The {@link BeanFeaturesFactory}
	 */
	public BeanFeaturesFactory getBeanFeaturesFactory();
	
	/**
	 * Create a new {@link MutableBeanFeatures} instance for a given bean
	 * This is a convenience method equivalent to calling 
	 * {@link BeanFeaturesFactory#createBeanFeatures(Bean)} on the
	 * factory returned by {@link #getBeanFeaturesFactory()}
	 * @param bean
	 * 		The bean by which this {@link MutableBeanFeatures} will be used
	 * @return
	 * 		new {@link MutableBeanFeatures}
	 */
	public MutableBeanFeatures createBeanFeatures(Bean bean);
	
	/**
	 * Create a new {@link ExtendedBeanFeatures} instance for a given bean
	 * This is a convenience method equivalent to calling 
	 * {@link BeanFeaturesFactory#createExtendedBeanFeatures(Bean)} on the
	 * factory returned by {@link #getBeanFeaturesFactory()}
	 * @param bean
	 * 		The bean by which this {@link ExtendedBeanFeatures} will be used
	 * @return
	 * 		new {@link ExtendedBeanFeatures}
	 */
	public ExtendedBeanFeatures createExtendedBeanFeatures(Bean bean);
	
	/**
	 * Create a new {@link MutableBeanFeatures} instance for a given bean,
	 * with a specific internal change implementation
	 * This is a convenience method equivalent to calling 
	 * {@link BeanFeaturesFactory#createBeanFeatures(Bean, InternalChangeImplementation)} on the
	 * factory returned by {@link #getBeanFeaturesFactory()}
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
	 * {@link BeanFeaturesFactory#createExtendedBeanFeatures(Bean, InternalChangeImplementation)} on the
	 * factory returned by {@link #getBeanFeaturesFactory()}
	 * @param bean
	 * 		The bean by which this {@link ExtendedBeanFeatures} will be used
	 * @param implementation
	 * 		The implementation of internal change response
	 * @return
	 * 		new {@link ExtendedBeanFeatures}
	 */
	public ExtendedBeanFeatures createExtendedBeanFeatures(Bean bean, InternalChangeImplementation implementation);

}
