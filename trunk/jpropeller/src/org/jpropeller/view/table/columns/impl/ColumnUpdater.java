package org.jpropeller.view.table.columns.impl;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.jpropeller.view.table.columns.ColumnInfo;
import org.jpropeller.view.table.columns.ColumnLayout;

/**
 * Monitors a {@link TableModel}. When changes to the columns occur,
 * updates a {@link TableColumnModel} to have appropriate {@link TableColumn}s,
 * as specified by a {@link ColumnLayout}.
 * 
 * Updates the columns until {@link #dispose()}d of.
 */
public class ColumnUpdater implements TableModelListener {

	private final TableModel tm;
	private final TableColumnModel cm;
	private final ColumnLayout layout;

	/**
	 * Create a {@link ColumnUpdater}, which will update the {@link TableColumnModel}
	 * as necessary until {@link #dispose()} is called.
	 * @param tm		The {@link TableModel} to be monitored
	 * @param cm		The {@link TableColumnModel} to be updated
	 * @param layout	The {@link ColumnLayout} used to convert the {@link TableModel}
	 * 					to {@link ColumnInfo}s used to create {@link TableColumn}s
	 */
	public ColumnUpdater(TableModel tm, TableColumnModel cm, ColumnLayout layout) {
		super();
		this.tm = tm;
		this.cm = cm;
		this.layout = layout;
		
		//Whenever tm changes, we may need to update columns
		tm.addTableModelListener(this);
		
		//Start out up to date
		update();
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		//If we have actual column changes, (re)create columns
		if (e.getFirstRow() == TableModelEvent.HEADER_ROW ||
				e.getLastRow() == TableModelEvent.HEADER_ROW) {
			update();
		}
	}

	/**
	 * (re)create columns in column model, from table model via layout
	 */
	private void update() {
        // Remove any current columns
        while (cm.getColumnCount() > 0) {
            cm.removeColumn(cm.getColumn(0));
        }

        //Query layout
        Iterable<ColumnInfo> infos = layout.layout(tm);
        int columnCount = tm.getColumnCount();

        //Convert to actual TableColumns and add them
        for (ColumnInfo info : infos) {
        	if (info.modelIndex() >= 0 && info.modelIndex() < columnCount) {
        		
                TableColumn newColumn = new TableColumn(info.modelIndex());
                
    			newColumn.setPreferredWidth(info.preferredWidth());
    			newColumn.setMinWidth(info.minWidth());
    			newColumn.setMaxWidth(info.maxWidth());
    			
    			
    			TableCellEditor cellEditor = info.editor();
    			if (cellEditor != null) {
    				newColumn.setCellEditor(cellEditor);
    			}
 
    			TableCellRenderer cellRenderer = info.renderer();
    			if (cellRenderer != null) {
    				newColumn.setCellRenderer(cellRenderer);
    			}

                addColumn(newColumn);
        		
        	}
        }
	}
	
	/**
	 * Add a column to column model, after overriding
	 * its header if necessary
	 * @param column		The column
	 */
	private void addColumn(TableColumn column) {
		int modelColumn = column.getModelIndex();

		if (column.getHeaderValue() == null) {
			String columnName = tm.getColumnName(
					modelColumn);
			column.setHeaderValue(columnName);
		}

		cm.addColumn(column);
	}

	/**
	 * Dispose of the updater. Performs a final update, then
	 * stops listening to the table model.
	 */
	public void dispose() {
		update();
		tm.removeTableModelListener(this);
	}
}
