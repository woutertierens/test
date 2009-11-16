package org.jpropeller.view.table.columns;

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

}
