package org.jpropeller.view.table.impl;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jpropeller.collection.CList;
import org.jpropeller.reference.Reference;
import org.jpropeller.ui.impl.JTableImproved;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.JView;
import org.jpropeller.view.table.TableRowView;
import org.jpropeller.view.table.TableView;

/**
 * A {@link JView} displaying an {@link CList} as a table,
 * using a {@link TableRowView} to convert each element of the list
 * to a row of the table
 *
 * @param <T>
 * 		The type of element in the {@link CList}
 */
public class ListTableView<T> implements TableView {

	JTable table;
	Reference<? extends CList<T>> model;
	ListTableModel<T> tableModel;
	
	
	/**
	 * Make a new {@link ListTableView}
	 * @param tableModel		The table model to be displayed
	 */
	public ListTableView(ListTableModel<T> tableModel) {
		this.tableModel = tableModel;
		this.model = tableModel.getModel();
		table = new JTableImproved(tableModel);
	}
	
	/**
	 * Make a new {@link ListTableView}
	 * @param model
	 * 		The model to be displayed - references the list that will be displayed
	 * as rows of a table
	 * @param rowView
	 * 		The {@link TableRowView} to convert from elements of the list to
	 * rows of the {@link JTable}
	 */
	public ListTableView(Reference<? extends CList<T>> model, TableRowView<? super T> rowView) {
		this(new ListTableModel<T>(model, rowView));
	}
	
	/**
	 * Gets the default editor of the contained {@link JTable}
	 * for a given class. See {@link JTable#getDefaultEditor(Class)}
	 * @param columnClass The edited class
	 * @return The default editor
	 */
	public TableCellEditor getDefaultEditor(Class<?> columnClass) {
		return table.getDefaultEditor(columnClass);
	}

	/**
	 * Gets the default renderer of the contained {@link JTable}
	 * for a given class. See {@link JTable#getDefaultRenderer(Class)}
	 * @param columnClass The rendered class
	 * @return The default renderer
	 */
	public TableCellRenderer getDefaultRenderer(Class<?> columnClass) {
		return table.getDefaultRenderer(columnClass);
	}

	/**
	 * Sets the default editor of the contained {@link JTable}
	 * for a given class. See {@link JTable#setDefaultEditor(Class, TableCellEditor)}
	 * @param columnClass The edited class
	 * @param editor The default editor
	 */
	public void setDefaultEditor(Class<?> columnClass, TableCellEditor editor) {
		table.setDefaultEditor(columnClass, editor);
	}

	/**
	 * Sets the default renderer of the contained {@link JTable}
	 * for a given class. See {@link JTable#setDefaultRenderer(Class, TableCellRenderer)}
	 * @param columnClass The rendered class
	 * @param renderer The default renderer
	 */
	public void setDefaultRenderer(Class<?> columnClass,
			TableCellRenderer renderer) {
		table.setDefaultRenderer(columnClass, renderer);
	}
	
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
    public void setRowHeight(int rowHeight) {
    	table.setRowHeight(rowHeight);
    }
	
    /**
     * Remove header from table display
     */
    public void removeHeader() {
    	table.setTableHeader(null);
    }

	@Override
	public JTable getComponent() {
		return table;
	}

	@Override
	public boolean selfNaming() {
		//Just displays list contents
		return false;
	}

	@Override
	public void cancel() {
		//TODO find way to cancel
		//Can't currently cancel
	}

	@Override
	public void commit() throws CompletionException {
		//TODO find way to commit
		//Can't currently commit
	}

	@Override
	public boolean isEditing() {
		// TODO find way to tell if we are editing
		//Currently never editing (or rather - not editing in a way we can cancel or commit)
		return false;
	}

	@Override
	public void dispose() {
		//Dispose our model
		tableModel.dispose();
	}

	@Override
	public void update() {
		//JTable updates itself automatically based on table model changes, without using
		//the UpdateManager - the table model does use UpdateManager though, and this 
		//achieves the same goal
	}

	@Override
	public Format format() {
		return Format.LARGE;
	}
	
}
