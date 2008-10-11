package org.jpropeller.transformer.impl;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.transformer.Transformer;

/**
 * A {@link Transformer} from a {@link Bean} to a named {@link Prop} of that {@link Bean}.
 * The named {@link Prop} must return a Bean
 */
public class BeanToBeanPropTransformer implements Transformer<Bean, Prop<? extends Bean>>{

	private PropName<? extends Prop<? extends Bean>, ? extends Bean> name;

	/**
	 * Make a {@link Transformer} that looks up a {@link Prop} using the name
	 * @param name
	 * 		The name to use for lookup
	 */
	public BeanToBeanPropTransformer(
			PropName<? extends Prop<? extends Bean>, ? extends Bean> name) {
		super();
		this.name = name;
	}

	@Override
	public Prop<? extends Bean> transform(Bean s) {
		return s.props().get(name);
	}

}
