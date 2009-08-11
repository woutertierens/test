package org.jpropeller.view.table.impl;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jpropeller.collection.CList;
import org.jpropeller.properties.list.selection.ListAndSelectionAndValueReference;
import org.jpropeller.ui.impl.JTableImproved;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.JView;
import org.jpropeller.view.table.TableRowView;

/**
 * A {@link JView} displaying an {@link CList} as a table,
 * using a {@link TableRowView} to convert each element of the list
 * to a row of the table
 *
 * @param <T>
 * 		The type of element in the {@link CList}
 */
public class SingleSelectionListTableView<T> implements JView {

	JTable table;
	ListAndSelectionAndValueReference<T> model;
	ListTableModel<T> tableModel;
	
	/**
	 * Make a new {@link SingleSelectionListTableView}
	 * @param model
	 * 		The model to be displayed - references the list that will be displayed
	 * as rows of a table
	 * @param rowView
	 * 		The {@link TableRowView} to convert from elements of the list to
	 * rows of the {@link JTable}
	 */
	public SingleSelectionListTableView(ListAndSelectionAndValueReference<T> model, TableRowView<? super T> rowView) {
		this.model = model;
		tableModel = new ListTableModel<T>(model, rowView);
		
		//Only allow the selection to be set when the table model is NOT firing a change - 
		//this is somewhat messy, but is necessary to wrest control of updating the selection
		//away from the JTable - we already update the selection ourself in a more intelligent
		//way, so we only want the selection changes that are NOT in response to a table model
		//change, but in response to a user selection action
		SelectionSetFilter filter = new SelectionSetFilter() {
			@Override
			public boolean canSet() {
				return !tableModel.isFiring();
			}
		};
		
		table = new JTableImproved(tableModel, null, new IntegerPropListSelectionModel(model.selection(), filter));
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
	 * Sets the row height of the contained {@link JTable}
	 * @param rowHeight		The row height
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
