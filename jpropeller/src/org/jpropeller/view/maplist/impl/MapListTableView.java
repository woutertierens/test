package org.jpropeller.view.maplist.impl;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jpropeller.collection.CList;
import org.jpropeller.ui.impl.JTableImproved;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.JView;
import org.jpropeller.view.table.TableRowView;

/**
 * A {@link JView} using a {@link MapListTableModel}
 * in a {@link JTableImproved} to display a {@link MapListReference}
 *
 * @param <K>	The type of key
 * @param <V>	The type of value
 * @param <L>	The type of {@link CList} in the map 
 */
public class MapListTableView<K, V, L extends CList<V>> implements JView {

	JTable table;
	MapListReference<K, V, L> model;
	MapListTableModel<K, V, L> tableModel;
	
	/**
	 * Make a new {@link MapListTableView}
	 * @param model 		The model to be displayed
	 * @param rowView		The {@link TableRowView} to convert from elements of 
	 * 						the model to rows of the {@link JTable}
	 */
	public MapListTableView(MapListReference<K, V, L> model, TableRowView<? super V> rowView) {
		tableModel = new MapListTableModel<K, V, L>(model, rowView);
		table = new JTableImproved(tableModel);
	}
	
	/**
	 * Make a new {@link MapListTableView}
	 * @param model 		The model to be displayed
	 * @param rowView		The {@link TableRowView} to convert from elements of 
	 * 						the model to rows of the {@link JTable}
	 * @param keyColumn 	True to display a column showing the key
	 * @param keyName 		The name of the column used to show key (if any)
	 * @param keyClass 		The class of the column used to show key (if any)
	 * 						May be null of keyColumn is false
	 * @param indexColumn 	True to display a column showing the index
	 * @param indexName 	The name of the column used to show index (if any)
	 * @param indexBase		The offset for displayed indices - e.g. 0 to
	 * 						display 0-based indices 0, 1, 2 ... or
	 * 						1 to display 1-based indices 1, 2, 3 ...
	 */
	public MapListTableView(MapListReference<K, V, L> model, TableRowView<? super V> rowView, boolean keyColumn, String keyName, Class<?> keyClass, boolean indexColumn, String indexName, int indexBase) {
		tableModel = new MapListTableModel<K, V, L>(model, rowView, keyColumn, keyName, keyClass, indexColumn, indexName, indexBase);
		table = new JTableImproved(tableModel);
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
	
}
