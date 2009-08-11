package org.jpropeller.view.table;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jpropeller.view.JView;

/**
 *	A view that is a table.
 */
public interface TableView extends JView  {

	/**
	 * Gets the default editor of the contained {@link JTable}
	 * for a given class. See {@link JTable#getDefaultEditor(Class)}
	 * @param columnClass The edited class
	 * @return The default editor
	 */
	public TableCellEditor getDefaultEditor(Class<?> columnClass);

	/**
	 * Gets the default renderer of the contained {@link JTable}
	 * for a given class. See {@link JTable#getDefaultRenderer(Class)}
	 * @param columnClass The rendered class
	 * @return The default renderer
	 */
	public TableCellRenderer getDefaultRenderer(Class<?> columnClass);

	/**
	 * Sets the default editor of the contained {@link JTable}
	 * for a given class. See {@link JTable#setDefaultEditor(Class, TableCellEditor)}
	 * @param columnClass The edited class
	 * @param editor The default editor
	 */
	public void setDefaultEditor(Class<?> columnClass, TableCellEditor editor);

	/**
	 * Sets the default renderer of the contained {@link JTable}
	 * for a given class. See {@link JTable#setDefaultRenderer(Class, TableCellRenderer)}
	 * @param columnClass The rendered class
	 * @param renderer The default renderer
	 */
	public void setDefaultRenderer(Class<?> columnClass,
			TableCellRenderer renderer);

	/**
	 * Sets the height, in pixels, of all cells to <code>rowHeight</code>,
	 * revalidates, and repaints.
	 * The height of the cells will be equal to the row height minus
	 * the row margin.
	 *
	 * @param   rowHeight                       new row height
	 * @exception IllegalArgumentException      if <code>rowHeight</code> is
	 *                                          less than 1
	 */
	public void setRowHeight(int rowHeight);

	/**
	 * Remove header from table display
	 */
	public void removeHeader();

}