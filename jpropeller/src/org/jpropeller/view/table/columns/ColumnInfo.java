package org.jpropeller.view.table.columns;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jpropeller.properties.change.Immutable;

/**
 * Specifies info for creating a column
 */
public interface ColumnInfo extends Immutable {

	/**
	 * The index within the table model displayed by the column
	 * @return	Model index
	 */
	public int modelIndex();
	
	/**
	 * The preferred width in the table.
	 * @return	Preferred width
	 */
	public int preferredWidth();

	/**
	 * The min width in the table.
	 * @return	Minimum width
	 */
	public int minWidth();

	/**
	 * The preferred width in the table.
	 * @return	Maximum width
	 */
	public int maxWidth();
	
	/**
	 * The {@link TableCellEditor} for this column, or
	 * null if no special editor should be set.
	 * @return	Default {@link TableCellEditor}
	 */
	public TableCellEditor editor();
	
	/**
	 * The {@link TableCellRenderer} for this column, or
	 * null if no special renderer should be set.
	 * @return	Default {@link TableCellRenderer}
	 */
	public TableCellRenderer renderer();

}
