package org.jpropeller.bean.impl;

import org.jpropeller.bean.Bean;
import org.jpropeller.bean.BeanFeaturesFactory;
import org.jpropeller.bean.ExtendedBeanFeatures;
import org.jpropeller.bean.MutableBeanFeatures;
import org.jpropeller.properties.change.impl.InternalChangeImplementation;

/**
 * Default {@link BeanFeaturesFactory} providing {@link BeanFeaturesDefault}
 * and {@link ExtendedBeanFeaturesDefault} instances
 */
public class BeanFeaturesFactoryDefault implements BeanFeaturesFactory {

	@Override
	public MutableBeanFeatures createBeanFeatures(Bean bean) {
		return new BeanFeaturesDefault(bean);
	}

	@Override
	public ExtendedBeanFeatures createExtendedBeanFeatures(Bean bean) {
		return new ExtendedBeanFeaturesDefault(createBeanFeatures(bean));
	}

	@Override
	public MutableBeanFeatures createBeanFeatures(Bean bean, InternalChangeImplementation implementation) {
		return new BeanFeaturesDefault(bean, implementation);
	}

	@Override
	public ExtendedBeanFeatures createExtendedBeanFeatures(Bean bean, InternalChangeImplementation implementation) {
		return new ExtendedBeanFeaturesDefault(createBeanFeatures(bean, implementation));
	}

}
