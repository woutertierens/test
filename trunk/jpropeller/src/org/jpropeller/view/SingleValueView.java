package org.jpropeller.view;

import org.jpropeller.reference.Reference;

/**
 * A {@link View} that displays data contained in the 
 * single value of a {@link Reference}, as accessed
 * using {@link Reference#value()}
 *
 * @param <M>	The type of model viewed
 */
public interface SingleValueView<M> extends View {
	
	/**
	 * Get the {@link Reference} that is the model for this {@link View}.
	 * The actual value displayed/edited by this view is
	 * accessed using the {@link Reference#value()} prop. All
	 * viewed/edited state is contained in this value
	 * 
	 * @return		The {@link View}'s model
	 */
	public Reference<? extends M> getModel();

}
