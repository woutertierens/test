package org.jpropeller.view.table.impl;

import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import org.jpropeller.collection.CList;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.list.selection.ListAndSelectionAndValueReference;
import org.jpropeller.properties.list.selection.impl.ListAndSelectionAndValueReferenceDefault;
import org.jpropeller.ui.impl.JTableImproved;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.JView;
import org.jpropeller.view.View;
import org.jpropeller.view.table.FiringTableModel;
import org.jpropeller.view.table.TableRowView;
import org.jpropeller.view.table.columns.ColumnLayout;
import org.jpropeller.view.table.columns.impl.ColumnUpdater;

/**
 * A {@link JView} displaying an {@link CList} as a table,
 * using a {@link TableRowView} to convert each element of the list
 * to a row of the table
 *
 * @param <T>
 * 		The type of element in the {@link CList}
 */
public class SingleSelectionListTableView<T> implements JView {

	private final JTable table;
	private final ListAndSelectionAndValueReference<T> model;
	private final ListTableModel<T> tableModel;
	private ColumnUpdater columnUpdater;
	
	/**
	 * Create a {@link SingleSelectionListTableView} of
	 * the list in a particular prop
	 * @param <S>		The type of value in the list 
	 * @param clazz		The class of value in the list
	 * @param prop		The {@link Prop} containing viewed {@link CList}
	 * @param rowView	The {@link TableRowView} used to display elements of the list
	 * @return			A new {@link SingleSelectionListTableView}
	 */
	public static <S> SingleSelectionListTableView<S> create(Class<S> clazz, Prop<CList<S>> prop, TableRowView<? super S> rowView) {
		return new SingleSelectionListTableView<S>(new ListAndSelectionAndValueReferenceDefault<S>(clazz, prop), rowView);
	}
	
	/**
	 * Create a {@link SingleSelectionListTableView} of
	 * the list in a particular prop
	 * @param <S>		The type of value in the list 
	 * @param clazz		The class of value in the list
	 * @param prop		The {@link Prop} containing viewed {@link CList}
	 * @param rowView	The {@link TableRowView} used to display elements of the list
	 * @param columnLayout 		The layout of the table columns, or null to 
	 * 							use default {@link JTable} behaviour
	 * @return			A new {@link SingleSelectionListTableView}
	 */
	public static <S> SingleSelectionListTableView<S> create(Class<S> clazz, Prop<CList<S>> prop, TableRowView<? super S> rowView, ColumnLayout columnLayout) {
		return new SingleSelectionListTableView<S>(new ListAndSelectionAndValueReferenceDefault<S>(clazz, prop), rowView, columnLayout, false);
	}
	
	/**
	 * Make a new {@link SingleSelectionListTableView}, with default column layout
	 * and no sorting
	 * @param model
	 * 		The model to be displayed - references the list that will be displayed
	 * as rows of a table
	 * @param rowView
	 * 		The {@link TableRowView} to convert from elements of the list to
	 * rows of the {@link JTable}
	 */
	public SingleSelectionListTableView(ListAndSelectionAndValueReference<T> model, TableRowView<? super T> rowView) {
		this(model, rowView, null, false);
	}
	
	
	/**
	 * Make a new {@link SingleSelectionListTableView}
	 * @param model				The model to be displayed - references the list 
	 * 							that will be displayed as rows of a table
	 * @param rowView			The {@link TableRowView} to convert from 
	 * 							elements of the list to rows of the {@link JTable}
	 * @param columnLayout 		The layout of the table columns, or null to 
	 * 							use default {@link JTable} behaviour
	 * @param sorting			True to allow sorting of table, false otherwise
	 */
	public SingleSelectionListTableView(
			ListAndSelectionAndValueReference<T> model, 
			TableRowView<? super T> rowView,
			ColumnLayout columnLayout,
			boolean sorting) {
		this(model, rowView, columnLayout, sorting, false, "", 0);
	}
	
	/**
	 * Make a new {@link SingleSelectionListTableView}
	 * @param model				The model to be displayed - references the list 
	 * 							that will be displayed as rows of a table
	 * @param rowView			The {@link TableRowView} to convert from 
	 * 							elements of the list to rows of the {@link JTable}
	 * @param columnLayout 		The layout of the table columns, or null to 
	 * 							use default {@link JTable} behaviour
	 * @param sorting			True to allow sorting of table, false otherwise
	 * @param indexColumn		True to have an index column 
	 * @param indexName 		Name of index caolumn, if present
	 * @param indexBase 		Base (normally 0 or 1) for index values
	 */
	public SingleSelectionListTableView(
			ListAndSelectionAndValueReference<T> model, 
			TableRowView<? super T> rowView,
			ColumnLayout columnLayout,
			boolean sorting, boolean indexColumn, String indexName, int indexBase) {
		this.model = model;
		tableModel = new ListTableModel<T>(model, rowView, indexColumn, indexName, indexBase);
		
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
		
		if (columnLayout == null) {
			table = new JTableImproved(tableModel);
		} else {
			final DefaultTableColumnModel cm = new DefaultTableColumnModel();
			table = new JTableImproved(tableModel, cm);
			columnUpdater = new ColumnUpdater(tableModel, cm, columnLayout);
		}
		
		table.setSelectionModel(new IntegerPropListSelectionModel(model.selection(), filter, table));
		
		if (sorting) {
			TableRowSorter<FiringTableModel> sorter = new TableRowSorter<FiringTableModel>(tableModel);
			table.setRowSorter(sorter);
		}

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
		//Cannot cancel
	}

	@Override
	public void commit() throws CompletionException {
		//Cannot commit
	}

	@Override
	public boolean isEditing() {
		//Never editing
		return false;
	}

	@Override
	public void dispose() {
		
		//Dispose of our column updater, if any
		if (columnUpdater != null) {
			columnUpdater.dispose();
			columnUpdater = null;
		}

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

	/**
	 * The {@link ListAndSelectionAndValueReference} displayed by this {@link View}
	 * @return	{@link ListAndSelectionAndValueReference}
	 */
	public ListAndSelectionAndValueReference<T> getModel() {
		return model;
	}
	
}
