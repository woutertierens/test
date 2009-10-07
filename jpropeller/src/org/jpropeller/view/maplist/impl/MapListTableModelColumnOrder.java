package org.jpropeller.view.maplist.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.jpropeller.collection.CList;
import org.jpropeller.collection.CMap;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.system.Props;
import org.jpropeller.view.JView;
import org.jpropeller.view.table.FiringTableModel;
import org.jpropeller.view.table.TableRowView;
import org.jpropeller.view.table.TableRowViewListener;
import org.jpropeller.view.table.impl.ListTableModel;
import org.jpropeller.view.update.Updatable;
import org.jpropeller.view.update.UpdateManager;

/**
 * A {@link TableModel} displaying a map from keys to {@link CList} values, 
 * where elements of the referenced  {@link List}s are displayed 
 * as blocks of columns, and the columns of each row are given by a 
 * {@link TableRowView} applied to each element in turn.
 * 
 * Uses {@link UpdateManager}, and in general provides the
 * "intelligence" required for a {@link JView} implemented
 * by simply displaying this {@link TableModel} in a 
 * {@link JTable}.
 * 
 * Very similar to {@link ListTableModel}, but adding the extra
 * layer of having a map of lists.
 * 
 * @param <K>	The type of key
 * @param <V>	The type of value
 * @param <L>	The type of {@link CList} in the map 
 */
public class MapListTableModelColumnOrder<K, V, L extends CList<V>> extends AbstractTableModel 
		implements Updatable, TableRowViewListener, FiringTableModel {

	private final Prop<? extends CMap<K, L>> map;
	private final Prop<? extends CList<K>> keys;
	private final TableRowView<? super V> rowView;
	
	//Track whether we have had a complete change since last firing
	private boolean completeChange = false;
	private boolean columnChange = false;
	
	private UpdateManager updateManager;
	private AtomicBoolean isFiring = new AtomicBoolean(false);
	
	//Number of columns in table row view
	private int viewColumnCount = 0;
	
	//The number of rows
	private int rowCount = 0;
	
	//The number of columns, NOT including the index column
	private int columnCount = 0;
	
	//Column name and index (in the table) of the index column (the one displaying the row index)
	private final String indexName;
	private final int indexColumn;

	//The table column index for first actual data column
	private final int firstRowViewColumn;
	
	//The base for row numbers displayed in index column
	private final int indexBase;
	
	private final ChangeListener mapListener = new ChangeListener() {
		@Override
		public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
			mapChange(initial, changes);
		}
	};

	private final ChangeListener keysListener = new ChangeListener() {
		@Override
		public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
			keysChange(initial, changes);
		}
	};

	/**
	 * Create a {@link MapListTableModelRowOrder} with no index column
	 * @param map			The map of keys to lists of data
	 * @param keys			The list of keys whose mapped lists will be displayed
	 * @param rowView		The view of each element as a row
	 */
	public MapListTableModelColumnOrder(
			Prop<? extends CMap<K, L>> map,
			Prop<? extends CList<K>> keys, 
			TableRowView<? super V> rowView
			){
		this(map, keys, rowView, false, "", 0);
	}
	
	
	/**
	 * Create a {@link MapListTableModelColumnOrder}
	 * @param map			The map of keys to lists of data
	 * @param keys			The list of keys whose mapped lists will be displayed
	 * @param rowView		The view of each element as a row
	 * @param indexColumn 	True to display a column showing the index
	 * @param indexName 	The name of the column used to show index (if any)
	 * @param indexBase		The offset for displayed indices - e.g. 0 to
	 * 						display 0-based indices 0, 1, 2 ... or
	 * 						1 to display 1-based indices 1, 2, 3 ...
	 */
	public MapListTableModelColumnOrder(
			Prop<? extends CMap<K, L>> map,
			Prop<? extends CList<K>> keys, 
			TableRowView<? super V> rowView,
			boolean indexColumn,
			String indexName, 
			int indexBase) {
		super();
		
		updateManager = Props.getPropSystem().getUpdateManager();
		updateManager.registerUpdatable(this);
		
		this.rowView = rowView;
		
		//Watch for row view changes - will need to update columns
		rowView.addListener(this);
		
		this.map = map;
		this.keys = keys;
		this.indexBase = indexBase;
		
		this.indexColumn = indexColumn ? 0 : -1;
		this.firstRowViewColumn = this.indexColumn + 1;
		this.indexName = indexName;
		
		//We need to see changes to
		//both keys prop and value prop
		map.features().addListener(mapListener);
		keys.features().addListener(keysListener);
		
		//Handle changes to update to initial state, with column change
		columnChange = true;
		handleChange();
		
	}

	@Override
	public int getColumnCount() {
		return firstRowViewColumn + columnCount;
	}

	@Override
	public int getRowCount() {
		return rowCount;
	}

	/**
	 * Find the value and rowview column index displayed at 0-based row 
	 * and column index (note that indices are already corrected for whether 
	 * we have an index column or not, etc.)
	 * @param rowIndex		The row index - index into the appropriate list
	 * @param columnIndex	The column index - corrected for any index column,
	 * 						so 0 is the first column of the first mapped value.
	 * @return				The object to display
	 */
	private ValueAndColumn getValueAndColumnAt(int rowIndex, int columnIndex) {
		
		//Outside data range, return null
		if (columnIndex >= columnCount || rowIndex >= rowCount) {
			return null;
		}

		K key = getKeyAt(columnIndex);

		CMap<K, ? extends CList<V>> currentMap = map.get();

		if (currentMap == null) {
			return null;
		}
		
		//Look up actual displayed list
		CList<V> list = currentMap.get(key);
		if (list == null) {
			return null;
		}
		
		//Outside list, return null (some lists may be shorter than max)
		if (rowIndex >= list.size()) {
			return null;
		}
		
		//Find actual value
		V value = list.get(rowIndex);
		if (value == null) {
			return null;
		}
		
		int columnInView = columnIndex % viewColumnCount;
		return new ValueAndColumn(value, columnInView);
	}
	
	private K getKeyAt(int columnIndex) {
		CList<K> currentKeys = keys.get();
		
		if (currentKeys == null) {
			return null;
		}
		
		//Find which key index we are in, using the size of the row view
		int keyIndex = columnIndex / viewColumnCount;
		
		//Outside key list, return null
		if (keyIndex >= currentKeys.size()) {
			return null;
		}
		
		return currentKeys.get(keyIndex);
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		//Index column just counts rows from indexBase
		if (columnIndex == indexColumn) {
			return rowIndex + indexBase;
			
		//Other columns must find the displayed value
		} else {
			ValueAndColumn vc = getValueAndColumnAt(rowIndex, columnIndex - firstRowViewColumn);
			if (vc == null) {
				return null;
			} else {
				return rowView.getColumn(vc.getValue(), vc.getColumn());			
			}
		}
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == indexColumn) {
			return Integer.class;
		} else {
			return rowView.getColumnClass((columnIndex - firstRowViewColumn) % viewColumnCount);
		}
	}

	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex == indexColumn) {
			return indexName;
		} else {
			K k = getKeyAt(columnIndex - firstRowViewColumn);
			String base = k != null ? k + ", " : "";
			return base + rowView.getColumnName((columnIndex - firstRowViewColumn) % viewColumnCount);
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == indexColumn) {
			return false;
		} else {
			ValueAndColumn vc = getValueAndColumnAt(rowIndex, columnIndex - firstRowViewColumn);
			if (vc == null) {
				return false;
			} else {
				return rowView.isEditable(vc.getValue(), vc.getColumn());			
			}
		}
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if (columnIndex == indexColumn) {
			return;
		} else {
			ValueAndColumn vc = getValueAndColumnAt(rowIndex, columnIndex - firstRowViewColumn);
			if (vc != null) {
				rowView.setColumn(vc.getValue(), vc.getColumn(), value);			
			}
		}
	}

	@Override
	public synchronized void tableRowViewChanged(TableRowView<?> tableRowView) {
		columnChange = true;
		handleChange();
	}
	
	private void mapChange(List<Changeable> initial, Map<Changeable, Change> changes) {
		//FIXME fire finer-grained changes if possible
		handleChange();
	}

	private void keysChange(List<Changeable> initial, Map<Changeable, Change> changes) {
		//FIXME fire finer-grained changes if possible
		columnChange = true;
		handleChange();
	}

	private synchronized void handleChange() {
		//Update our cached sizes
		int oldRowCount = rowCount;
		rowCount = 0;
		columnCount = 0;

		viewColumnCount = rowView.getColumnCount();
		
		//Can't display if any values are null
		if (keys == null || keys.get() == null) {
			rowCount = 0;
			columnCount = 0;
		} else {
			for (K key : keys.get()) {
				CList<V> list = map.get().get(key);
				//Ignore null lists
				if (list != null) {
					int size = list.size();
					if (size > rowCount) {
						rowCount = size;
					}
				}
			}
			columnCount = keys.get().size() * viewColumnCount;
		}
		
		//If row count changes, we will fire a complete change
		if (oldRowCount != rowCount) {
			completeChange = true;
		}
		
		//Ask for an update
		updateManager.updateRequiredBy(this);
	}

	public synchronized void update() {
		
		//TODOWe use an AtomicBoolean here - probably not necessary since the
		//value will only ever be checked from the swing thread
		isFiring.set(true);
		
		//Fire appropriate change
		if (columnChange) {
			fireTableStructureChanged();
		} else if (completeChange) {
			fireTableDataChanged();			
		} else {
			if (getRowCount() == 0) {
				fireTableDataChanged();							
			} else {
				fireTableRowsUpdated(0, getRowCount()-1);
			}
		}
		
		isFiring.set(false);
		
		//We now haven't had a complete change since the last firing
		completeChange = false;
		columnChange = false;
	}

	/**
	 * Check whether the model is currently firing a table change. Should only be called
	 * from the Swing EDT, where the value is relevant.
	 * @return
	 * 		True if the model is currently firing a table change event
	 */
	public boolean isFiring() {
		return isFiring.get();
	}
	
	@Override
	public void dispose() {
		updateManager.deregisterUpdatable(this);
		map.features().removeListener(mapListener);
		keys.features().removeListener(keysListener);
		rowView.removeListener(this);
	}

	/**
	 * A value and column reference
	 */
	private class ValueAndColumn {
		private V value;
		private int column;
		private ValueAndColumn(V value, int column) {
			super();
			this.value = value;
			this.column = column;
		}
		private V getValue() {
			return value;
		}
		private int getColumn() {
			return column;
		}
	}
	
}
