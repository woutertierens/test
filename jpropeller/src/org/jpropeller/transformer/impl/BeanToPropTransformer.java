package org.jpropeller.transformer.impl;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.transformer.Transformer;

/**
 * A {@link Transformer} from a {@link Bean} to a named {@link Prop} of that {@link Bean}
 * 
 * @param <T>		The type of value in the {@link Prop} 
 */
public class BeanToPropTransformer<T> implements Transformer<Bean, Prop<T>>{

	private PropName<T> name;

	/**
	 * Make a {@link Transformer} that looks up a {@link Prop} using the name
	 * 
	 * @param name		The name to use for lookup
	 */
	public BeanToPropTransformer(
			PropName<T> name) {
		super();
		this.name = name;
	}

	@Override
	public Prop<T> transform(Bean s) {
		return s.features().get(name);
	}

}
