package org.jpropeller.reference.impl;

import org.jpropeller.bean.Bean;
import org.jpropeller.map.ExtendedPropMap;
import org.jpropeller.map.PropMap;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.bean.impl.EditableBeanPropDefault;
import org.jpropeller.reference.Reference;
import org.jpropeller.system.Props;

/**
 * A {@link Reference} where the {@link #value()} property is
 * an {@link EditableProp} containing some type of {@link Bean} value
 *
 * @param <M>
 * 		The type of bean value
 */
public class EditableBeanReference<M extends Bean> implements Reference<M> {

	ExtendedPropMap propMap = Props.getPropSystem().createExtendedPropMap(this);
	EditableProp<M> model;
	
	private EditableBeanReference(EditableProp<M> model) {
		this.model = propMap.add(model);
	}

	@Override
	public PropMap props() {
		return propMap;
	}

	@Override
	public EditableProp<M> value() {
		return model;
	}
	
	/**
	 * Create a new reference for a {@link Bean} value
	 * @param <S>
	 * 		The type of {@link Bean}
	 * @param clazz
	 * 		The class of {@link Bean}
	 * @param value
	 * 		The initial value in the model property
	 * @return
	 * 		A new reference
	 */
	public static <S extends Bean> EditableBeanReference<S> create(Class<S> clazz, S value) {
		return new EditableBeanReference<S>(EditableBeanPropDefault.create("model", clazz, value));
	}

	/**
	 * Create a new reference for a {@link Bean} model with an initial
	 * null value
	 * @param <S>
	 * 		The type of {@link Bean}
	 * @param clazz
	 * 		The class of {@link Bean}
	 * @return
	 * 		A new reference
	 */
	public static <S extends Bean> EditableBeanReference<S> create(Class<S> clazz) {
		return create(clazz, null);
	}

}
