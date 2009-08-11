package org.jpropeller.reference.impl;

import org.jpropeller.bean.BeanFeatures;
import org.jpropeller.bean.MutableBeanFeatures;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.path.impl.PathProp;
import org.jpropeller.reference.Reference;
import org.jpropeller.system.Props;

/**
 * A {@link Reference} where the {@link #value()} property is
 * an {@link PathProp}
 *
 * @param <M>
 * 		The type of value
 */
public class PathReference<M> implements Reference<M> {

	MutableBeanFeatures features = Props.getPropSystem().createBeanFeatures(this); 
	PathProp<?, M> model;
	
	protected PathReference(PathProp<?, M> model) {
		this.model = features.add(model);
	}

	@Override
	public BeanFeatures features() {
		return features;
	}

	@Override
	public Prop<M> value() {
		return model;
	}

}
