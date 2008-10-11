package org.jpropeller.map.impl;

import org.jpropeller.bean.Bean;
import org.jpropeller.map.ExtendedPropMap;
import org.jpropeller.map.PropMapFactory;
import org.jpropeller.map.PropMapMutable;

/**
 * {@link PropMapFactory} providing default newly-created {@link PropMapDefault} instances,
 * or such instances wrapped in newly created {@link ExtendedPropMapDefault} instances
 */
public class PropMapFactoryDefault implements PropMapFactory{

	@Override
	public PropMapMutable createPropMap(Bean bean) {
		return new PropMapDefault(bean);
	}

	@Override
	public ExtendedPropMap createExtendedPropMap(Bean bean) {
		return new ExtendedPropMapDefault(new PropMapDefault(bean));
	}
	
}
