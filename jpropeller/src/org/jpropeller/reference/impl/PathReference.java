package org.jpropeller.reference.impl;

import org.jpropeller.map.ExtendedPropMap;
import org.jpropeller.map.PropMap;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.path.impl.PathProp;
import org.jpropeller.reference.Reference;
import org.jpropeller.system.Props;

/**
 * A {@link Reference} where the {@link #value()} property is
 * a {@link PathProp}
 *
 * @param <M>
 * 		The type of value
 */
public class PathReference<M> implements Reference<M> {

	ExtendedPropMap propMap = Props.getPropSystem().createExtendedPropMap(this);
	PathProp<?, M> model;
	
	protected PathReference(PathProp<?, M> model) {
		this.model = propMap.add(model);
	}

	@Override
	public PropMap props() {
		return propMap;
	}

	@Override
	public Prop<M> value() {
		return model;
	}

}
