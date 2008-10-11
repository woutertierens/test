package org.jpropeller.system.impl;

import org.jpropeller.bean.Bean;
import org.jpropeller.map.ExtendedPropMap;
import org.jpropeller.map.PropMapFactory;
import org.jpropeller.map.PropMapMutable;
import org.jpropeller.map.impl.PropMapFactoryDefault;
import org.jpropeller.system.PropSystem;
import org.jpropeller.view.update.UpdateManager;
import org.jpropeller.view.update.impl.UpdateManagerDefault;

/**
 * Default implementation of {@link PropSystem}
 * 
 * Designed for a thread-safe model layer using
 * a Swing-compatible view layer  
 *
 */
public class PropSystemDefault implements PropSystem {

	UpdateManager updateManager = new UpdateManagerDefault();
	PropMapFactory propMapFactory = new PropMapFactoryDefault();
	
	
	@Override
	public UpdateManager getUpdateManager() {
		return updateManager;
	}

	@Override
	public PropMapFactory getPropMapFactory() {
		return propMapFactory;
	}

	@Override
	public ExtendedPropMap createExtendedPropMap(Bean bean) {
		return getPropMapFactory().createExtendedPropMap(bean);
	}

	@Override
	public PropMapMutable createPropMap(Bean bean) {
		return getPropMapFactory().createPropMap(bean);
	}

}
