package org.jpropeller.view.table.impl;

import java.awt.Color;

import javax.swing.JTable;

import org.jpropeller.collection.ObservableList;
import org.jpropeller.reference.Reference;
import org.jpropeller.ui.ColorCellRenderer;
import org.jpropeller.ui.IconCellRenderer;
import org.jpropeller.ui.ImmutableIcon;
import org.jpropeller.ui.NumberCellEditor;
import org.jpropeller.ui.SelectingTextCellEditor;
import org.jpropeller.view.CompletionException;
import org.jpropeller.view.JView;
import org.jpropeller.view.table.TableRowView;

/**
 * A {@link JView} displaying an {@link ObservableList} as a table,
 * using a {@link TableRowView} to convert each element of the list
 * to a row of the table
 * @author shingoki
 *
 * @param <T>
 * 		The type of element in the {@link ObservableList}
 */
public class ListTableView<T> implements JView<ObservableList<T>> {

	JTable table;
	Reference<? extends ObservableList<T>> model;
	ListTableModel<T> tableModel;
	
	/**
	 * Make a new {@link ListTableView}
	 * @param model
	 * 		The model to be displayed - references the list that will be displayed
	 * as rows of a table
	 * @param rowView
	 * 		The {@link TableRowView} to convert from elements of the list to
	 * rows of the {@link JTable}
	 */
	public ListTableView(Reference<? extends ObservableList<T>> model, TableRowView<? super T> rowView) {
		tableModel = new ListTableModel<T>(model, rowView);
		table = new JTable(tableModel);
		
		//Improved editors and renderers
		table.setDefaultEditor(Double.class, 	NumberCellEditor.doubleEditor());
		table.setDefaultEditor(Float.class, 	NumberCellEditor.floatEditor());
		table.setDefaultEditor(Integer.class, 	NumberCellEditor.integerEditor());
		table.setDefaultEditor(Long.class, 		NumberCellEditor.longEditor());
		table.setDefaultEditor(String.class, 	SelectingTextCellEditor.editor());

		table.setDefaultRenderer(ImmutableIcon.class, 	new IconCellRenderer());
		table.setDefaultRenderer(Color.class, 			new ColorCellRenderer());

		//Set sensible row height
		table.setRowHeight(22);

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
	public Reference<? extends ObservableList<T>> getModel() {
		return model;
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
	
}
