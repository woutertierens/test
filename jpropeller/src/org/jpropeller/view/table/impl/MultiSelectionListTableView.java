package org.jpropeller.view.table.impl;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import org.jpropeller.collection.CList;
import org.jpropeller.properties.list.selection.MultiSelectionReference;
import org.jpropeller.ui.impl.JTableImproved;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.JView;
import org.jpropeller.view.table.FiringTableModel;
import org.jpropeller.view.table.TableRowView;
import org.jpropeller.view.table.TableView;

/**
 * A {@link JView} displaying an {@link CList} as a table,
 * using a {@link TableRowView} to convert each element of the list
 * to a row of the table
 * 
 * Maintains a multiple selection in its reference
 *
 * @param <T>
 * 		The type of element in the {@link CList}
 */
public class MultiSelectionListTableView<T> implements JView, TableView {

	JTable table;
	MultiSelectionReference<T> model;
	FiringTableModel tableModel;
	
	
	/**
	 * Make a new {@link MultiSelectionListTableView}
	 * @param model				The model to be displayed - references the list that will be displayed
	 * 							as rows of a table
	 * @param rowView			The {@link TableRowView} to convert from elements of the list to
	 * 							rows of the {@link JTable}
	 * @param indexColumn		True if the first column should show the row index 
	 * @param indexName 		The name for first column, if indexColumn is true
	 * @param indexBase 		The base for row index numbering - 0 to be 0-based, 0, 1, 2...
	 * 							1 to be 1-based, 1, 2, 3...
	 */
	public MultiSelectionListTableView(
			MultiSelectionReference<T> model, 
			TableRowView<? super T> rowView,
			boolean indexColumn,
			String indexName,
			int indexBase) {
		
		this(model, new ListTableModel<T>(model, rowView, indexColumn, indexName, indexBase));
	}
	
	/**
	 * Make a new {@link MultiSelectionListTableView}
	 * @param model				The model to be displayed - references the list that will be displayed
	 * 							as rows of a table
	 * @param firingTableModel	The actual table model to display
	 */
	public MultiSelectionListTableView(
			MultiSelectionReference<T> model, 
			FiringTableModel firingTableModel
			) {
		
		this.model = model;
		this.tableModel = firingTableModel;
		
		//Only allow the selection to be set when the table model is NOT firing a change - 
		//this is somewhat messy, but is necessary to wrest control of updating the selection
		//away from the JTable
		SelectionSetFilter filter = new SelectionSetFilter() {
			@Override
			public boolean canSet() {
				return !tableModel.isFiring();
			}
		};
		
		TableRowSorter<FiringTableModel> sorter = new TableRowSorter<FiringTableModel>(tableModel);
		
		//TODO this currently causes a problem, since it is not possible to make the delegate
		//selection match the prop selection when the prop selection contains indices that are
		//filtered out of the table. Need a solution to this before any filtering can be used.
//		sorter.setRowFilter(new RowFilter<FiringTableModel, Integer>() {
//			@Override
//			public boolean include(
//					Entry<? extends FiringTableModel, ? extends Integer> entry) {
//				return entry.getIdentifier() % 2 == 0;
//			}
//		});
		
		table = new JTableImproved(tableModel);
		IntegersListSelectionModel listSelectionModel = new IntegersListSelectionModel(model.selection(), filter, table);
		table.setSelectionModel(listSelectionModel);
		
		table.setRowSorter(sorter);
	}
	
	/**
	 * Make a new {@link MultiSelectionListTableView}
	 * @param model
	 * 		The model to be displayed - references the list that will be displayed
	 * as rows of a table
	 * @param rowView
	 * 		The {@link TableRowView} to convert from elements of the list to
	 * rows of the {@link JTable}
	 */
	public MultiSelectionListTableView(MultiSelectionReference<T> model, TableRowView<? super T> rowView) {
		this(model, rowView, false, "", 0);
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

	@Override
	public void removeHeader() {
		table.setTableHeader(null);
	}

	@Override
	public void setRowHeight(int rowHeight) {
		table.setRowHeight(rowHeight);
	}

}
