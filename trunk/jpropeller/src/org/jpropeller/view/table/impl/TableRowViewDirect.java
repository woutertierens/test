package org.jpropeller.view.table.impl;

import org.jpropeller.view.table.TableRowView;

/**
 * A {@link TableRowView} that displays a single
 * column, containing the row object itself.
 * Cells are not editable.
 * @author bwebster
 * @param <T>
 * 		The type of viewed object 
 */
public class TableRowViewDirect<T> implements TableRowView<T> {

	Class<T> clazz;
	String columnName;

	/**
	 * Create a {@link TableRowViewDirect}
	 * @param clazz
	 * 		The class of the displayed elements
	 * @param columnName
	 * 		The name of the single column
	 */
	public TableRowViewDirect(Class<T> clazz, String columnName) {
		super();
		this.clazz = clazz;
		this.columnName = columnName;
	}

	@Override
	public Object getColumn(T row, int column) {
		return row;
	}

	@Override
	public Class<?> getColumnClass(int column) {
		return clazz;
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public String getColumnName(int column) {
		return columnName;
	}

	@Override
	public boolean isEditable(T row, int column) {
		return false;
	}

	@Override
	public void setColumn(T row, int column, Object value) {
		//Not editable
	}

}
