package org.jpropeller2.view;

import java.util.Map;

import org.jpropeller2.box.Box;
import org.jpropeller2.change.Change;

/**
 * A {@link View} responds to changes in data in {@link Box}es.
 * {@link View}s form a layer of the system - the view layer,
 * just as {@link Box}es form the data layer.
 */
public interface View {
	/**
	 * Called when changes have occurred
	 * @param changes	All changes that have occurred since the last
	 * 					time changes were dispatched. 
	 */
	public void changes(Map<Box, Change> changes);
}
