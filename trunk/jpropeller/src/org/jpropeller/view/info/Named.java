package org.jpropeller.view.info;

import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.transformer.PathStep;

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
	public final static PropName<String> NAME = PropName.create(String.class, "name"); 

	/**
	 * Path to {@link #name()}
	 */
	public final static PathStep<Named, String> toName = new PathStep<Named, String>() {
		@Override
		public Prop<String> transform(Named s) {
			return s.name();
		}
	};

}
