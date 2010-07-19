package org.jpropeller.view.info;

import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.transformer.PathStep;

/**
 * {@link Versioned} instances have an {@link Integer} version {@link Prop}
 */
public interface Versioned {

	/**
	 * Version property
	 * @return
	 * 		Version property
	 */
	public Prop<Integer> version();
	
	/**
	 * The {@link PropName} for {@link #version()} property
	 */
	public final static PropName<String> VERSION = PropName.create(String.class, "version"); 

	/**
	 * Path to {@link #version()}
	 */
	public final static PathStep<Versioned, Integer> toVersion = new PathStep<Versioned, Integer>() {
		@Override
		public Prop<Integer> transform(Versioned s) {
			return s.version();
		}
	};

}
