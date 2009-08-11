package org.jpropeller.view.info;

import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.transformer.BeanPathTo;

/**
 * {@link Named} instances have a {@link String} name {@link Prop}
 */
public interface Named {

	/**
	 * Name property
	 * @return
	 * 		Name property
	 */
	public Prop<String> name();
	
	/**
	 * The {@link PropName} for {@link #name()} property
	 */
	public final static PropName<String> NAME = PropName.create("name", String.class); 

	/**
	 * Path to {@link #name()}
	 */
	public final static BeanPathTo<Named, String> toName = new BeanPathTo<Named, String>() {
		@Override
		public Prop<String> transform(Named s) {
			return s.name();
		}
	};

}
