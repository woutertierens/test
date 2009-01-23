package org.jpropeller.view.info;

import org.jpropeller.properties.Prop;
import org.jpropeller.ui.ImmutableIcon;

/**
 * {@link Illustrated} instances have an {@link ImmutableIcon} illustration {@link Prop}
 */
public interface Illustrated {

	/**
	 * Illustration property
	 * @return
	 * 		Illustration property
	 */
	public Prop<ImmutableIcon> illustration();
	
}
