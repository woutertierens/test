package org.jpropeller.view.maplist.impl;

import java.util.LinkedList;
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
import org.jpropeller.view.table.TableRowView;
import org.jpropeller.view.table.TableRowViewListener;
import org.jpropeller.view.table.impl.ListTableModel;
import org.jpropeller.view.update.Updatable;
import org.jpropeller.view.update.UpdateManager;

/**
 * A {@link TableModel} displaying a map from keys to {@link CList} values, 
 * where elements of the referenced  {@link List}s are displayed 
 * as rows, and the columns of each row are given by a 
 * {@link TableRowView}
 * 
 * Uses {@link UpdateManager}, and in general provides the
 * "intelligence" required for a {@link JView} implemented
 * by simply displaying this {@link TableModel} in a 
 * {@link JTable}.
 * 
 * Very similar to {@link ListTableModel}, but adding the extra
 * layer of having a map of lists.
 * @param <K>	The type of key
 * @param <V>	The type of value
 * @param <L>	The type of {@link CList} in the map 
 */
public class MapListTableModelRowOrder<K, V, L extends CList<V>> extends AbstractTableModel 
		implements ChangeListener, Updatable, TableRowViewListener {

	private final Prop<CMap<K, L>> map;
	private final Prop<CList<K>> keys;
	private TableRowView<? super V> rowView;
	
	//Track whether we have had a complete change since last firing
	private boolean completeChange = false;
	private boolean columnChange = false;
	
	private UpdateManager updateManager;
	private AtomicBoolean isFiring = new AtomicBoolean(false);
	
	//Cached sizes, etc.
	private List<Integer> listSizes = new LinkedList<Integer>();
	private int rowCount = 0;
	
	private final int keyColumn;
	private final String keyName;
	private Class<?> keyClass;
	private final int indexColumn;
	private final String indexName;
	
	private final int firstRowViewColumn;
	
	private final int indexBase;
	
	
	/**
	 * Create a {@link MapListTableModelRowOrder}, which displays neither
	 * the index nor the key column
	 * @param map			The map of keys to lists of data
	 * @param keys			The list of keys whose mapped lists will be displayed
	 * @param rowView		The view of each row
	 */
	public MapListTableModelRowOrder(
			Prop<CMap<K, L>> map,
			Prop<CList<K>> keys, 
			TableRowView<? super V> rowView) {
		this(map, keys, rowView, false, "", null, false, "", 0);
	}
	
	/**
	 * Create a {@link MapListTableModelRowOrder}
	 * @param map			The map of keys to lists of data
	 * @param keys			The list of keys whose mapped lists will be displayed
	 * @param rowView		The view of each row
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
	public MapListTableModelRowOrder(
			Prop<CMap<K, L>> map,
			Prop<CList<K>> keys,
			TableRowView<? super V> rowView,
			boolean keyColumn,
			String keyName,
			Class<?> keyClass,
			boolean indexColumn,
			String indexName, 
			int indexBase) {
		super();
		
		updateManager = Props.getPropSystem().getUpdateManager();
		updateManager.registerUpdatable(this);
		
		this.rowView = rowView;
		
		rowView.addListener(this);
		
		this.map = map;
		this.keys = keys;
		this.indexBase = indexBase;
		
		int index = 0;
		if (keyColumn) {
			this.keyColumn = index;
			index++;
		} else {
			this.keyColumn = -1;
		}
		if (indexColumn) {
			this.indexColumn = index;
			index++;
		} else {
			this.indexColumn = -1;
		}
		this.firstRowViewColumn = index;
		
		this.keyName = keyName;
		this.indexName = indexName;
		this.keyClass = keyClass;
		
		//we need to see changes to
		//both keys prop and value prop
		map.features().addListener(this);
		keys.features().addListener(this);
		
		handleChange();
	}

	@Override
	public int getColumnCount() {
		return firstRowViewColumn + rowView.getColumnCount();
	}

	@Override
	public int getRowCount() {
		return rowCount;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == keyColumn) {
			return getKeyAtIndex(rowIndex);
		} else if (columnIndex == indexColumn) {
			return getListIndexAtRowIndex(rowIndex) + indexBase;
		} else {
			
			V value = getValueAtIndex(rowIndex);
			if (value == null) {
				return null;
			} else {
				return rowView.getColumn(value, columnIndex - firstRowViewColumn);
			}
		}
	}

	private V getValueAtIndex(int index) {
		
		//If index is too high, return null
		if (index >= rowCount) {
			return null;
		}
		
		//Get the keys and map of lists
		CList<K> currentKeys = keys.get();
		CMap<K, ? extends CList<V>> currentMap = map.get();
		
		//Track where we are up to in combined index
		int indexSum = 0;
		
		//Check whether we are in the list for each key
		for (int keyIndex = 0; keyIndex < currentKeys.size(); keyIndex++) {
			K key = currentKeys.get(keyIndex);
			CList<V> list = currentMap.get(key);
			
			//Ignore null lists
			if (list != null) {
				//If this list contains the relevant index,
				//return the appropriate value
				int nextIndexSum = indexSum + list.size();
				if (index >= indexSum && index < nextIndexSum) {
					int indexOffset = index - indexSum;
					return list.get(indexOffset);
				}
				
				//Move on to the next list
				indexSum = nextIndexSum;
			}
		}
		
		return null;
	}
	
	private K getKeyAtIndex(int index) {
		
		//If index is too high, return null
		if (index >= rowCount) {
			return null;
		}
		
		//Get the keys and map of lists
		CList<K> currentKeys = keys.get();
		CMap<K, ? extends CList<V>> currentMap = map.get();
		
		//Track where we are up to in combined index
		int indexSum = 0;
		
		//Check whether we are in the list for each key
		for (int keyIndex = 0; keyIndex < currentKeys.size(); keyIndex++) {
			K key = currentKeys.get(keyIndex);
			CList<V> list = currentMap.get(key);
			
			//Ignore null lists
			if (list != null) {
				//If this list contains the relevant index,
				//return the appropriate value
				int nextIndexSum = indexSum + list.size();
				if (index >= indexSum && index < nextIndexSum) {
					return key;
				}
				
				//Move on to the next list
				indexSum = nextIndexSum;
			}
		}
		
		return null;
	}
	
	/**
	 * Get the <b>0-based</b> index within the appropriate
	 * list, at specified row index
	 * @param index		The row index in this table model
	 * @return			The list index within the list displayed
	 * 					on specified row
	 */
	private int getListIndexAtRowIndex(int index) {
		
		//If index is too high, return null
		if (index >= rowCount) {
			return -1;
		}
		
		//Get the keys and map of lists
		CList<K> currentKeys = keys.get();
		CMap<K, ? extends CList<V>> currentMap = map.get();
		
		//Track where we are up to in combined index
		int indexSum = 0;
		
		//Check whether we are in the list for each key
		for (int keyIndex = 0; keyIndex < currentKeys.size(); keyIndex++) {
			K key = currentKeys.get(keyIndex);
			CList<V> list = currentMap.get(key);
			
			//Ignore null lists
			if (list != null) {
				//If this list contains the relevant index,
				//return the appropriate value
				int nextIndexSum = indexSum + list.size();
				if (index >= indexSum && index < nextIndexSum) {
					int indexOffset = index - indexSum;
					return indexOffset;
				}
				
				//Move on to the next list
				indexSum = nextIndexSum;
			}
		}
		
		return -1;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == keyColumn) {
			return keyClass;
		} else if (columnIndex == indexColumn) {
			return Integer.class;
		} else {
			return rowView.getColumnClass(columnIndex - firstRowViewColumn);
		}
	}

	@Override
	public String getColumnName(int column) {
		if (column == keyColumn) {
			return keyName;
		} else if (column == indexColumn) {
			return indexName;
		} else {
			return rowView.getColumnName(column - firstRowViewColumn);
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == keyColumn) {
			return false;
		} else if (columnIndex == indexColumn) {
			return false;
		} else {
			V value = getValueAtIndex(rowIndex);
			if (value == null) {
				return false;
			} else {
				return rowView.isEditable(value, columnIndex - firstRowViewColumn);
			}
		}
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if (columnIndex == keyColumn) {
			return;
		} else if (columnIndex == indexColumn) {
			return;
		} else {
			V rowValue = getValueAtIndex(rowIndex);
			if (rowValue != null) {
				rowView.setColumn(rowValue, columnIndex - firstRowViewColumn, value);
			}
		}
	}

	@Override
	public void tableRowViewChanged(TableRowView<?> tableRowView) {
		columnChange = true;

		//Ask for an update
		updateManager.updateRequiredBy(this);
	}
	
	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		//FIXME fire finer-grained changes if possible
		handleChange();
	}
	
	private synchronized void handleChange() {
		//Update our cached sizes
		int oldRowCount = rowCount;
		rowCount = 0;
		listSizes.clear();
		
		//Can't display if any values are null
		if (keys == null || keys.get() == null) {
			rowCount = 0;
		} else {
			for (K key : keys.get()) {
				CList<V> list = map.get().get(key);
				//Ignore null lists
				if (list != null) {
					int size = list.size();
					listSizes.add(size);
					rowCount += size;
				}
			}
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
			fireTableRowsUpdated(0, getRowCount()-1);
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
		keys.features().removeListener(this);
		map.features().removeListener(this);
		rowView.removeListener(this);
	}
	
}
