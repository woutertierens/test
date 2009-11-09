package org.jpropeller.view.table.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.jpropeller.collection.CList;
import org.jpropeller.collection.CMap;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.system.Props;
import org.jpropeller.view.table.FiringTableModel;
import org.jpropeller.view.table.TableRowView;
import org.jpropeller.view.table.TableRowViewListener;
import org.jpropeller.view.update.Updatable;
import org.jpropeller.view.update.UpdateManager;

/**
 * A {@link TableModel} displaying data from a {@link CList}
 * and a {@link CMap}.
 * 
 * Each row displays an object by:
 * 	firstly, getting a key from the {@link CList} entry 
 *   corresponding to the row, then
 *  secondly, looking up the object from the {@link CMap}
 *  
 * Where the list and/or map don't contain the required values,
 * the row contains only null values.
 * 
 * Objects are converted into the
 * columns of each row are by a {@link TableRowView}
 * 
 * @param <K> The type of data in the list, and keys in the map
 * @param <V> The type of value in the map
 */
public class ListAnnotationTableModel<K, V> extends AbstractTableModel 
								implements FiringTableModel, ChangeListener, Updatable, TableRowViewListener {

	private Prop<? extends CList<K>> list;
	private Prop<? extends CMap<K, V>> map;
	private TableRowView<? super V> rowView;
	
	//Track whether we have had a complete change and/or column change since last firing
	private boolean completeChange = false;
	private boolean columnChange = false;
	
	private UpdateManager updateManager;
	private AtomicBoolean isFiring = new AtomicBoolean(false);
	
	/**
	 * Create a {@link ListAnnotationTableModel}
	 * @param list		The list, containing keys to display on each row
	 * @param map		The map, containing the value to display on each row
	 * @param rowView	The view of each row. Should only be provided to ONE table model
	 */
	public ListAnnotationTableModel(
			Prop<? extends CList<K>> list,
			Prop<? extends CMap<K, V>> map,
			TableRowView<? super V> rowView) {
		super();
		
		updateManager = Props.getPropSystem().getUpdateManager();
		updateManager.registerUpdatable(this);
		
		this.rowView = rowView;
		rowView.addListener(this);
		
		this.map = map;
		this.list = list;
		
		//Listen to the values
		map.features().addListener(this);
		list.features().addListener(this);
	}

	@Override
	public int getColumnCount() {
		return rowView.getColumnCount();
	}

	@Override
	public int getRowCount() {
		if (list.get() == null) {
			return 0;
		} else {
			return list.get().size();			
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		V value = getRow(rowIndex);
		if (value == null) {
			return null;
		}
		return rowView.getColumn(value, columnIndex);
	}

	/**
	 * Get the value on a given rwo, or null if there
	 * is no value
	 * @param rowIndex		The row index
	 * @return				The value, or null if none available
	 */
	private V getRow(int rowIndex) {
		CList<K> cList = list.get();
		if (cList == null) {
			return null;
		} else {
			if (rowIndex >= cList.size()) {
				return null;
			}
			K key = cList.get(rowIndex);
			V value = map.get().get(key);
			return value;
		}
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return rowView.getColumnClass(columnIndex);
	}

	@Override
	public String getColumnName(int columnIndex) {
		return rowView.getColumnName(columnIndex);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		V value = getRow(rowIndex);
		if (value == null) {
			return false;
		} else {
			return rowView.isEditable(value, columnIndex);
		}
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		V row = getRow(rowIndex);
		if (value == null) {
			return;
		} else {
			rowView.setColumn(row, columnIndex, value);
		}
	}

	@Override
	public void tableRowViewChanged(TableRowView<?> tableRowView) {
		handleChange(true, true);
	}

	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		//FIXME fire finer-grained changes if possible
		
		//If the list prop has had a new CList instance set then we have a complete change
		Change listPropChange = changes.get(list);
		if (listPropChange != null && !listPropChange.sameInstances()) {
			handleChange(true, false);
			
		//If list still points to the same CList instance, see what change it has had
		} else {
			//If instances are all the same in the list, then contents have changed
			//only - we don't have more or less rows
			Change listChange = changes.get(list.get());
			if (listChange == null || listChange.sameInstances()) {
				handleChange(false, false);
			//If the list itself has been edited, fire a change to number of rows
			} else {
				handleChange(true, false);
			}
		}
	}
	
	private synchronized void handleChange(boolean newChangeIsComplete, boolean newChangeOnColumns) {
		//Track whether we have had a complete change since last firing
		if (newChangeIsComplete) {
			completeChange = true;
		}
		if (newChangeOnColumns) {
			columnChange = true;
		}

		//Ask for an update
		updateManager.updateRequiredBy(this);
	}

	public synchronized void update() {
		
		//TODO We use an AtomicBoolean here - probably not necessary since the
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

	@Override
	public boolean isFiring() {
		return isFiring.get();
	}
	
	@Override
	public void dispose() {
		updateManager.deregisterUpdatable(this);
		list.features().removeListener(this);
		map.features().removeListener(this);
		rowView.removeListener(this);
	}

}
