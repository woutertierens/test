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
	 */
	public void tableRowViewChanged(TableRowView<?> tableRowView);
	
}
