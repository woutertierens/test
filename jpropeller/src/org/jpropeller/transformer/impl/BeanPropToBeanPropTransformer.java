package org.jpropeller.transformer.impl;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.transformer.Transformer;

/**
 * A {@link Transformer} that transforms from one {@link Prop} with a {@link Bean}
 * value to another. This is done by getting the {@link Bean} value of the input {@link Prop},
 * and looking up a named {@link Prop} in it.
 * 
 * This allows the {@link BeanPropToBeanPropTransformer} to be used as one stage of a {@link ChainTransformer}
 * that navigates through a data model.
 */
public class BeanPropToBeanPropTransformer implements Transformer<Prop<? extends Bean>, Prop<? extends Bean>>{

	private PropName<? extends Bean> name;

	/**
	 * Create a {@link BeanPropToBeanPropTransformer} using a specified {@link PropName}
	 * to look up the output {@link Prop}
	 * 
	 * @param name		The name
	 */
	public BeanPropToBeanPropTransformer(
			PropName<? extends Bean> name) {
		super();
		this.name = name;
	}

	@Override
	public Prop<? extends Bean> transform(Prop<? extends Bean> s) {
		return s.get().features().get(name);
	}

}
