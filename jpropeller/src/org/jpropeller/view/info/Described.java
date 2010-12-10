package org.jpropeller.view.info;

import org.jpropeller.properties.Prop;
import org.jpropeller.transformer.PathStep;

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
	
	/**
	 * {@link PathStep} to {@link #description()}
	 */
	public final static PathStep<Described, String> toDescription = new PathStep<Described, String>() {
		public Prop<String> transform(Described s) {
			return s.description();
		};
	};
	
}
