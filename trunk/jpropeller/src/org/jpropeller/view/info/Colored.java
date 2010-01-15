package org.jpropeller.view.info;

import java.awt.Color;

import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.transformer.PathStep;

/**
 * {@link Colored} instances have a {@link Prop} giving their {@link Color} 
 */
public interface Colored {

	/**
	 * Color property
	 * @return	Color property
	 */
	public Prop<Color> color();
	
	/**
	 * The {@link PropName} for {@link #color()} property
	 */
	public final static PropName<Color> COLOR = PropName.create(Color.class, "color"); 

	/**
	 * Path to {@link #color()}
	 */
	public final static PathStep<Colored, Color> toColor = new PathStep<Colored, Color>() {
		@Override
		public Prop<Color> transform(Colored s) {
			return s.color();
		}
	};
	
}
