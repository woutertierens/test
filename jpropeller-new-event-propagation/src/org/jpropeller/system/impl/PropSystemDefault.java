package org.jpropeller.system.impl;

import org.jpropeller.bean.Bean;
import org.jpropeller.bean.BeanFeaturesFactory;
import org.jpropeller.bean.ExtendedBeanFeatures;
import org.jpropeller.bean.MutableBeanFeatures;
import org.jpropeller.bean.impl.BeanFeaturesFactoryDefault;
import org.jpropeller.properties.change.ChangeSystem;
import org.jpropeller.properties.change.impl.ChangeDispatcherSwing;
import org.jpropeller.properties.change.impl.ChangeSystemDefault;
import org.jpropeller.properties.change.impl.InternalChangeImplementation;
import org.jpropeller.system.PropSystem;
import org.jpropeller.view.update.UpdateManager;
import org.jpropeller.view.update.impl.DirectUpdateManager;

/**
 * Default implementation of {@link PropSystem}
 * 
 * Designed for a thread-safe model layer using
 * a Swing-compatible view layer  
 *
 */
public class PropSystemDefault implements PropSystem {

	UpdateManager updateManager = new DirectUpdateManager();
	BeanFeaturesFactory beanFeaturesFactory = new BeanFeaturesFactoryDefault();
	ChangeSystem changePropagator = new ChangeSystemDefault(new ChangeDispatcherSwing());
	
	@Override
	public UpdateManager getUpdateManager() {
		return updateManager;
	}

	@Override
	public ChangeSystem getChangeSystem() {
		return changePropagator;
	}

	@Override
	public BeanFeaturesFactory getBeanFeaturesFactory() {
		return beanFeaturesFactory;
	}

	@Override
	public MutableBeanFeatures createBeanFeatures(Bean bean) {
		return getBeanFeaturesFactory().createBeanFeatures(bean);
	}

	@Override
	public ExtendedBeanFeatures createExtendedBeanFeatures(Bean bean) {
		return getBeanFeaturesFactory().createExtendedBeanFeatures(bean);
	}

	@Override
	public MutableBeanFeatures createBeanFeatures(Bean bean,
			InternalChangeImplementation implementation) {
		return getBeanFeaturesFactory().createBeanFeatures(bean, implementation);
	}

	@Override
	public ExtendedBeanFeatures createExtendedBeanFeatures(Bean bean,
			InternalChangeImplementation implementation) {
		return getBeanFeaturesFactory().createExtendedBeanFeatures(bean, implementation);
	}

}
