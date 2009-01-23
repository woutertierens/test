/*
 * Created on 08-Jan-2005
 */
package org.jpropeller.view;

/**
 * Listens for a {@link View} changing its model
 * @param <M> 
 * 		The type of model
 */
public interface ViewChangeListener<M> {

	/**
	 * Called when a View changes its model
	 * @param event The ViewChangeEvent with details of change
	 */
	public void viewChanged(ViewChangeEvent<? extends M> event);
	
}
