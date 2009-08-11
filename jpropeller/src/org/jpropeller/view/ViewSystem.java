package org.jpropeller.view;

import java.awt.Color;

/**
 * Global system settings for {@link View}s
 */
public interface ViewSystem {

	/**
	 * Return the background color to be used
	 * by components having an error - for example
	 * an invalid current value.
	 * 
	 * @return		Error background color
	 */
	public Color getErrorBackgroundColor();
	
	/**
	 * Get a {@link JViewSource} for a given class
	 * 
	 * @param <M> 		The type of model
	 * 
	 * @param clazz		The class to view
	 * @return			The {@link JViewSource}, 
	 * 					or null if none is available
	 */
	public <M> JViewSource<? super M> jviewSourceFor(Class<M> clazz);

	/**
	 * Register the source of views for a class
	 * @param <M>		The type of viewed instance 
	 * 
	 * @param clazz		The class
	 * @param source	The source of views for instances of the class
	 */
	public <M> void registerJViewSourceFor(Class<M> clazz, JViewSource<? super M> source);

	/**
	 * Get the jgoodies forms layout definition for 3 columns to display
	 * a label, a gap, then a simple editor component
	 * 
	 * @return			Standard 3 column definition
	 */
	public String getStandard3ColumnDefinition();
	
}
