package org.jpropeller.view.table.columns;

import javax.swing.table.TableModel;

/**
 * Can lay out a {@link TableModel} to a list of {@link ColumnInfo}
 * specifying the key information about the columns of a table
 */
public interface ColumnLayout {

	/**
	 * Lay out a {@link TableModel} to a list of {@link ColumnInfo}
	 * specifying the key information about the columns of a table
	 * @param model		The {@link TableModel}
	 * @return			The {@link ColumnInfo}s, in the order columns will be
	 * 					displayed in the table
	 */
	public Iterable<ColumnInfo> layout(TableModel model);
	
}
