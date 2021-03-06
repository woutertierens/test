package org.jpropeller.view.info;

import org.jpropeller.properties.Prop;
import org.jpropeller.transformer.PathStep;
import org.jpropeller.ui.impl.ImmutableIcon;

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
	
	/**
	 * Path to {@link #illustration()}
	 */
	public final static PathStep<Illustrated, ImmutableIcon> toIllustration = new PathStep<Illustrated, ImmutableIcon>() {
		@Override
		public Prop<ImmutableIcon> transform(Illustrated s) {
			return s.illustration();
		}
	};

}
