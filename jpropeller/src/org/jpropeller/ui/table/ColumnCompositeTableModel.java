package org.jpropeller.ui.table;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.jpropeller.view.table.FiringTableModel;

/**
 * Composites a list of table models together into one larger table model,
 * in a column-wise direction. That is, the first m columns are from the
 * first table, then the next n from the next table, etc. The number
 * of rows is the same as the longest table, and any cells in the composite
 * table falling outside the individual tables will be non-editable null values. 
 */
public class ColumnCompositeTableModel extends AbstractTableModel implements FiringTableModel, TableModelListener {

	List<? extends TableModel> models;

	int columnCount;
	int rowCount;
	int[] columnStarts;
	
	int currentRowCount = 0;
	
	private final AtomicBoolean firing = new AtomicBoolean(false);
	
	/**
	 * Create ColumnCompositeTableModel
	 * @param models
	 * 		The list of models to composite. They will be displayed
	 * across the columns of the composite table model, in order.
	 * This list is copied, so any changes to the list itself will
	 * not be reflected in this table model. However changes to the
	 * table models themselves will be listened for and passed on 
	 * as appropriate table model events from the composite table
	 * model itself.
	 */
	public ColumnCompositeTableModel(List<? extends TableModel> models) {
		this.models = new ArrayList<TableModel>(models);
		
		for (TableModel model : models) {
			model.addTableModelListener(this);
		}
		
		updateForSizes();
		
		currentRowCount = getRowCount();
		
		firing.set(true);
		fireTableStructureChanged();
		firing.set(false);
	}
	
	@Override
	public void tableChanged(TableModelEvent e) {
		
		firing.set(true);

		//If the incoming event is for a structure change, then perform a complete
		//update which will update sizes and fire a structure change event
		if (
				(e.getFirstRow() == TableModelEvent.HEADER_ROW)
				|| (e.getLastRow() == TableModelEvent.HEADER_ROW) ){
			updateForSizes();
			fireTableStructureChanged();		
		
		//If we have an update, then fire on with the same rows,
		//since we always map rows through directly from our constituent
		//models. We will expand scope to all columns even if only one column
		//is affected - OPTIMIZE we could translate column numbers, but it seems
		//fairly unimportant
		} else if (e.getType() == TableModelEvent.UPDATE) {
			//Note we need to update sizes here, since UPDATE may still
			//include changes to size of rows (in this case the last row
			//is set to a very large number).
			updateForSizes();
			
			//Fire appropriate event depending on current row count
			if (currentRowCount != getRowCount()) {
				fireTableDataChanged();
			} else {
				fireTableRowsUpdated(e.getFirstRow(), e.getLastRow());
			}
			
		//Otherwise, just fire a complete data change - this allows for
		//changes to any row, and to number of rows, but not to columns
		//We cannot react more finely to insert/delete events, since
		//e.g. inserting a row into a single constituent model will not
		//result in a row insert to the composite model, but rather to
		//a change in number of rows and cell contents etc.
		} else {
			//Note we need to update sizes in case row counts have changed
			updateForSizes();

			//Fire appropriate event depending on current row count
			if (currentRowCount != getRowCount()) {
				fireTableDataChanged();
			} else {
				//Any row could have changed
				fireTableRowsUpdated(0, getRowCount()-1);
			}
		}

		//Store new row count
		currentRowCount = getRowCount();
		
		firing.set(false);
	}
	
	/**
	 * Update size and column indexing according to the current
	 * column and row counts of constituent models
	 */
	public void updateForSizes() {
		columnCount = 0;
		rowCount = 0;
		columnStarts = new int[models.size()];
		
		for (int i = 0; i < models.size(); i++) {
			TableModel model = models.get(i);
			columnStarts[i] = columnCount;
			columnCount += model.getColumnCount();
			if (model.getRowCount() > rowCount) {
				rowCount = model.getRowCount();
			}
		}
	}

	/**
	 * Find the index of the model displayed in the given column
	 * @param columnIndex
	 * 		The index of the column
	 * @return
	 * 		The index of the model displayed in that column
	 */
	public int modelForColumn(int columnIndex) {
		//OPTIMISE - for a large number of tables, it would be better to 
		//use something like a binary search, but it seems very unlikely that
		//we would actually use a significant number of tables in the composite,
		//since this would lead to a large number of columns, which is unusual.
		int i = 0;
		while ((i < columnStarts.length) && (columnStarts[i] <= columnIndex)) {
			i++;
		}
		return i - 1;
	}
	
	/**
	 * Get the model at a given index in the composite
	 * @param index
	 * 		The index of the model
	 * @return
	 * 		The model itself
	 */
	public TableModel getModel(int index) {
		return models.get(index);
	}
	
	@Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
    	if (columnIndex >= getColumnCount()) return false; 
    	int modelIndex = modelForColumn(columnIndex);
    	TableModel model = models.get(modelIndex);
    	if (rowIndex >= model.getRowCount()) return false;
    	return model.isCellEditable(rowIndex, columnIndex - columnStarts[modelIndex]);
    }

	@Override
	public int getRowCount() {
    	return rowCount;
	}

	@Override
	public int getColumnCount() {
		return columnCount;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
    	if (columnIndex >= getColumnCount()) return null; 
    	int modelIndex = modelForColumn(columnIndex);
    	TableModel model = models.get(modelIndex);
    	if (rowIndex >= model.getRowCount()) return null;
    	return model.getValueAt(rowIndex, columnIndex - columnStarts[modelIndex]);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
    	if (columnIndex >= getColumnCount()) return null; 
    	int modelIndex = modelForColumn(columnIndex);
    	TableModel model = models.get(modelIndex);
    	return model.getColumnClass(columnIndex - columnStarts[modelIndex]);
	}

	@Override
	public String getColumnName(int columnIndex) {
    	if (columnIndex >= getColumnCount()) return null; 
    	int modelIndex = modelForColumn(columnIndex);
    	TableModel model = models.get(modelIndex);
    	return model.getColumnName(columnIndex - columnStarts[modelIndex]);
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
    	if (columnIndex >= getColumnCount()) return; 
    	int modelIndex = modelForColumn(columnIndex);
    	TableModel model = models.get(modelIndex);
    	if (rowIndex >= model.getRowCount()) return;
    	model.setValueAt(value, rowIndex, columnIndex - columnStarts[modelIndex]);
	}

	@Override
	public boolean isFiring() {
		return firing.get();
	}

	@Override
	public void dispose() {
		//Nothing to do
	}
	
}
