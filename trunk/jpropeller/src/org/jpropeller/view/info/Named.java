package org.jpropeller.view.info;

import org.jpropeller.properties.Prop;

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
	
}
