package org.jpropeller.view.table;

/**
 * Listener to a {@link TableRowView}
 * Notified whenever any aspect of the {@link TableRowView}
 * has changed
 */
public interface TableRowViewListener {

	/**
	 * Called when any aspect of the {@link TableRowView}
	 * has changed
	 * @param tableRowView		The {@link TableRowView} that has changed
	 * @param columnsChanged	True if the columns produced by the row view have changed,
	 * 							or false if the columns are the same, but the rendering
	 * 							of any row may change
	 */
	public void tableRowViewChanged(TableRowView<?> tableRowView, boolean columnsChanged);
	
}
