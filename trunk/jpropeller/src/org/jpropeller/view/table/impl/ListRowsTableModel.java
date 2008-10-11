package org.jpropeller.view.table.impl;

import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.jpropeller.collection.ObservableList;
import org.jpropeller.properties.event.PropEvent;
import org.jpropeller.properties.event.PropListener;
import org.jpropeller.view.table.TableRowView;

/**
 * A {@link TableModel} displaying an {@link ObservableList},
 * where elements of the {@link List} are displayed as rows, and the
 * columns of each row are given by a {@link TableRowView}
 * @author bwebster
 * @param <T> 
 * 		The type of data in the list
 */
public class ListRowsTableModel<T> extends AbstractTableModel implements PropListener {

	ObservableList<T> list;
	TableRowView<? super T> rowView;

	/**
	 * Create a {@link ListRowsTableModel}
	 * @param list
	 * 		The list to view
	 * @param rowView
	 * 		The view of each row
	 */
	public ListRowsTableModel(ObservableList<T> list,
			TableRowView<? super T> rowView) {
		super();
		this.rowView = rowView;
		setList(list);
	}

	/**
	 * Get the list being viewed
	 * @return
	 * 		The viewed {@link ObservableList}
	 */
	public ObservableList<T> getList() {
		return list;
	}
	
	/**
	 * Set the list being viewed
	 * @param list
	 * 		The viewed {@link ObservableList}
	 */
	public void setList(ObservableList<T> list) {
		
		//Do nothing if list has not actually changed
		if (this.list == list) return;
		
		//Swap over listener
		if (this.list != null) {
			this.list.props().removeListener(this);
		}
		if (list != null) {
			list.props().addListener(this);
		}

		this.list = list;
		
		//Fire all rows change for new data
		fireTableDataChanged();
	}

	@Override
	public int getColumnCount() {
		return rowView.getColumnCount();
	}

	@Override
	public int getRowCount() {
		return list.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return rowView.getColumn(list.get(rowIndex), columnIndex);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return rowView.getColumnClass(columnIndex);
	}

	@Override
	public String getColumnName(int column) {
		return rowView.getColumnName(column);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return rowView.isEditable(list.get(rowIndex), columnIndex);
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		rowView.setColumn(list.get(rowIndex), columnIndex, value);
	}

	@Override
	public <S> void propChanged(PropEvent<S> event) {
		//FIXME fire finer-grained changes if possible
		
		//If we have a deep change to the list, then contents have changed
		//only - we don't have more or less rows
		if (event.isDeep()) {
			fireTableRowsUpdated(0, getRowCount()-1);
			
		//If the list itself has been edited, fire a change to number of rows
		} else {
			fireTableDataChanged();
		}
	}
	
}
