package org.jpropeller.view.table.impl;

import javax.swing.ListSelectionModel;

/**
 * A filter that will return true or false to indicate whether a 
 * selection can be set in a {@link ListSelectionModel}
 */
public interface SelectionSetFilter {

	/**
	 * Return true if the selection can be set
	 * @return
	 * 		True to allow setting selection
	 */
	public boolean canSet();
	
}
