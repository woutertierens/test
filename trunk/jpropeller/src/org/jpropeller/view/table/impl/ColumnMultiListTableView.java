package org.jpropeller.view.table.impl;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jpropeller.collection.CList;
import org.jpropeller.ui.impl.JTableImproved;
import org.jpropeller.ui.table.ColumnCompositeTableModel;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.JView;
import org.jpropeller.view.View;
import org.jpropeller.view.table.TableView;

/**
 * A {@link JView} displaying multiple {@link CList}s as a 
 * single table, where the lists are composed along columns - 
 * that is the first list provides the first m columns, the
 * second list provides the next n columns, etc. (where m
 * is the number of columns in the row view used for the
 * first list, n for the second list)
 * 
 * Note the model for this {@link View} is just an empty list - 
 * see requirements for {@link View} interface. The model
 * does NOT reflect the actual contents, since this is a composite
 * view referencing multiple table models.
 */
public class ColumnMultiListTableView implements TableView {

	JTable table;
	List<ListTableModel<?>> tableModels;
	
	/**
	 * Make a new {@link ColumnMultiListTableView}
	 * @param tableModels		The table models to be displayed. These will
	 * 							be disposed when this view is disposed.
	 */
	public ColumnMultiListTableView(List<ListTableModel<?>> tableModels) {
		if (tableModels.isEmpty()) {
			throw new IllegalArgumentException("Must have non-empty list of tableModels");
		}
		this.tableModels = new ArrayList<ListTableModel<?>>(tableModels);
		table = new JTableImproved(new ColumnCompositeTableModel(tableModels));
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
		for (ListTableModel<?> tableModel : tableModels) {
			tableModel.dispose();
		}
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
