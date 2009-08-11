package org.jpropeller.view;

import org.jpropeller.reference.Reference;

/**
 * A source of {@link JView}s for models
 * 
 * @param <M>		The type of model to be viewed 
 */
public interface JViewSource<M> {

	/**
	 * Get a view for a model
	 * 
	 * @param model		The model to be viewed
	 * @return			A view for the model
	 */
	public JView get(Reference<? extends M> model);
	
}
