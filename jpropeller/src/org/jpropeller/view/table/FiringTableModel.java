package org.jpropeller.view.table;

import javax.swing.table.TableModel;

/**
 * A {@link TableModel} that can be checked to see whether it
 * is currently firing a table change
 */
public interface FiringTableModel extends TableModel {

	/**
	 * Check whether the model is currently firing a table change. Should only be called
	 * from the Swing EDT, where the value is relevant.
	 * @return
	 * 		True if the model is currently firing a table change event
	 */
	public boolean isFiring();
	
	/**
	 * Dispose of the table model
	 */
	public void dispose();
}
