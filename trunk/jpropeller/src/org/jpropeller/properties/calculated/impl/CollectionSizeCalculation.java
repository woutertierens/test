package org.jpropeller.properties.calculated.impl;

import org.jpropeller.calculation.Calculation;
import org.jpropeller.collection.CCollection;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.util.NoInstanceAvailableException;
import org.jpropeller.util.Source;

/**
 * Factory for {@link Calculation}s and {@link Prop}s
 * giving the size of the {@link CCollection} in a {@link Prop}
 */
public class CollectionSizeCalculation {

	private CollectionSizeCalculation(){};
	
	/**
	 * Make a {@link Calculation} giving the size of the 
	 * {@link CCollection} in a {@link Prop}
	 * @param collection		The {@link Prop} containing the {@link CCollection}
	 * @return					A {@link Calculation} giving the size
	 */
	public static Calculation<Integer> calculation(final Prop<? extends CCollection<?>> collection) {
		return BuildCalculation.<Integer>on(collection).returning(new Source<Integer>() {
			@Override
			public Integer get() throws NoInstanceAvailableException {
				CCollection<?> c = collection.get();
				if (c == null) {
					return 0;
				} else {
					return c.size();
				}
			}
		});
	}

	/**
	 * Make a {@link Prop} containing the size of the 
	 * {@link CCollection} in another {@link Prop}
	 * @param name				The name for the new {@link Prop}
	 * @param collection		The {@link Prop} containing the {@link CCollection}
	 * @return					A {@link Prop} giving the size
	 */
	public static Prop<Integer> prop(final String name, final Prop<? extends CCollection<?>> collection) {
		return new CalculatedProp<Integer>(PropName.create(Integer.class, name), calculation(collection));
	}
	
}
