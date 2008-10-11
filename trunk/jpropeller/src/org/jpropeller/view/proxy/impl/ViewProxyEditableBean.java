package org.jpropeller.view.proxy.impl;

import org.jpropeller.bean.Bean;
import org.jpropeller.map.ExtendedPropMap;
import org.jpropeller.map.PropMap;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.bean.impl.EditableBeanPropDefault;
import org.jpropeller.system.Props;
import org.jpropeller.view.proxy.ViewProxy;

/**
 * A {@link ViewProxy} where the {@link #model()} property is
 * an {@link EditableProp} containing some type of {@link Bean} value
 *
 * @param <M>
 * 		The type of bean
 */
public class ViewProxyEditableBean<M extends Bean> implements ViewProxy<M> {

	ExtendedPropMap propMap = Props.getPropSystem().createExtendedPropMap(this);
	EditableProp<M> model;
	
	private ViewProxyEditableBean(EditableProp<M> model) {
		this.model = propMap.add(model);
	}

	@Override
	public PropMap props() {
		return propMap;
	}

	@Override
	public EditableProp<M> model() {
		return model;
	}
	
	/**
	 * Create a new proxy for a {@link Bean} model
	 * @param <S>
	 * 		The type of {@link Bean}
	 * @param clazz
	 * 		The class of {@link Bean}
	 * @param value
	 * 		The initial value in the model property
	 * @return
	 * 		A new proxy
	 */
	public static <S extends Bean> ViewProxyEditableBean<S> create(Class<S> clazz, S value) {
		return new ViewProxyEditableBean<S>(EditableBeanPropDefault.create("model", clazz, value));
	}

	/**
	 * Create a new proxy for a {@link Bean} model with an initial
	 * null value of model
	 * @param <S>
	 * 		The type of {@link Bean}
	 * @param clazz
	 * 		The class of {@link Bean}
	 * @return
	 * 		A new proxy
	 */
	public static <S extends Bean> ViewProxyEditableBean<S> create(Class<S> clazz) {
		return create(clazz, null);
	}

}
