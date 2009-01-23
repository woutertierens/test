package org.jpropeller.view.info;

import org.jpropeller.properties.Prop;

/**
 * {@link Described} instances have a {@link String} description {@link Prop}
 */
public interface Described {

	/**
	 * Description property
	 * @return
	 * 		Description property
	 */
	public Prop<String> description();
	
}
