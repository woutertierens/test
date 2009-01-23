package org.jpropeller.view.table;

import javax.swing.table.TableModel;

/**
 * Provides data for a line of a {@link TableModel}, from objects of a given
 * type
 *
 * @param <T>
 * 	The type of objects to be viewed in a table
 */
public interface TableRowView<T> {

	/**
	 * @return
	 * 		The number of columns per object
	 */
	public int getColumnCount();
	
	/**
	 * Check whether a column is editable
	 * @param row
	 * 		The object for the row
	 * @param column
	 * 		The column index
	 * @return
	 * 		True if the property of the row object at the given column
	 * 		can be displayed
	 */
	public boolean isEditable(T row, int column);
	
	/**
	 * Get the class of the displayed object for a column
	 * @param column
	 * 		The column index
	 * @return
	 * 		The class of the object displayed by this view in the
	 * 		given column
	 */
	public Class<?> getColumnClass(int column);

	/**
	 * Get the name of the displayed object for a column
	 * @param column
	 * 		The column index
	 * @return
	 * 		The name of the object displayed by this view in the
	 * 		given column
	 */
	public String getColumnName(int column);

	/**
	 * Get the object representing the property of the row
	 * object for the given column
	 * @param row
	 * 		The object for the row
	 * @param column
	 * 		The column index
	 * @return
	 * 		The object for this column of the object
	 */
	public Object getColumn(T row, int column);

	/**
	 * Set the object representing the property of the row
	 * object for the given column
	 * @param row
	 * 		The object for the row
	 * @param column
	 * 		The column index
	 * @param value
	 * 		The value to set
	 */
	public void setColumn(T row, int column, Object value);

}
