package org.jpropeller.view.table.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.system.Props;
import org.jpropeller.view.table.FiringTableModel;
import org.jpropeller.view.update.Updatable;
import org.jpropeller.view.update.UpdateManager;

/**
 * A {@link TableModel} that displays a single
 * column, with {@link Boolean} values, where only
 * the row with index matching a {@link Prop}
 * of {@link Integer} value is true, all others are
 * false.
 *
 * When a row is set to true, the {@link Prop} value
 * is set to that row index. 
 * When a row is set to false, either
 * the {@link Prop} value is set to -1 for no selection,
 * or nothing happens, depending on configuration.
 * 
 * This allows for a composite table to include this {@link TableModel}
 * for the purposes of editing  a choice
 * of a row of the composite table - this is distinct from selection
 * of that row, which works differently (and possibly alongside
 * this row view).
 * 
 * The column could be displayed as a checkbox or even a radio
 * button style circle, according to preference.
 * 
 */
public class IndexTableModel extends AbstractTableModel  implements FiringTableModel, ChangeListener, Updatable{

	private final Prop<Integer> value;
	private final Prop<Integer> size;
	private final Prop<Boolean> locked;
	private final String columnName;
	
	//Track whether we have had a complete change since last firing of table model change
	private boolean completeChange = false;
	
	private UpdateManager updateManager;
	private AtomicBoolean isFiring = new AtomicBoolean(false);
	private final boolean editable;
	private final boolean allowClearing;

	/**
	 * Create an {@link IndexTableModel}
	 * @param value			The integer value of the selected row.
	 * @param size			The total number of rows. Note that if this has a
	 * 						null or negative value, size 0 will be used instead.
	 * @param locked		When this {@link Prop} has value {@link Boolean#TRUE},
	 * 						no editing is allowed on the value. Null {@link Prop}
	 * 						is treated as unlocked (false).
	 * @param columnName	The name of the single column
	 * @param editable		True to accept editing, which will modify value
	 * @param allowClearing	If true, editing a "true" row (the one corresponding to value)
	 * 						to false will clear the value back to -1 (for "no selection").
	 * 						If false, cannot clear the value, it will only ever be set
	 * 						to a row index from 0 to (table row count - 1).
	 */
	public IndexTableModel(Prop<Integer> value, Prop<Integer> size, Prop<Boolean> locked, String columnName, boolean editable, boolean allowClearing) {
		super();

		this.value = value;
		this.size = size;
		this.locked = locked;
		this.columnName = columnName;
		this.editable = editable;
		this.allowClearing = allowClearing;

		updateManager = Props.getPropSystem().getUpdateManager();
		updateManager.registerUpdatable(this);
		
		value.features().addListener(this);
		size.features().addListener(this);
	}
	
	//Single boolean column
	@Override
	public int getColumnCount() {
		return 1;
	}
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return Boolean.class;
	}
	@Override
	public String getColumnName(int columnIndex) {
		return columnName;
	}
	
	@Override
	public int getRowCount() {
		Integer s = size.get();
		if (s == null || s < 0) {
			return 0;
		} else {
			return s;			
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Integer v = value.get();
		if (v == null) {
			return false;
		} else {
			return (rowIndex == v.intValue());
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		//Not editable if locked
		if (Props.isTrue(locked)) {
			return false;
		}
		return editable;
	}

	@Override
	public void setValueAt(Object newValue, int rowIndex, int columnIndex) {
		//Shouldn't happen, but skip editing anyway
		if (!editable) {
			return;
		}
		//Not editable if locked
		if (Props.isTrue(locked)) {
			return;
		}

		if (newValue instanceof Boolean) {
			//Setting a row to true changes the value to that row's index.
			if (((Boolean) newValue).booleanValue()) {
				value.set(rowIndex);
			//Setting a row to false can optionally clear the value to -1
			} else {
				if (allowClearing && new Integer(rowIndex).equals(value.get())) {
					value.set(rowIndex);
				}
			}
		}
	}

	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		boolean newChangeIsComplete = false;
		boolean newChange = false;
		
		//If the value prop has changed, we have at least a non-complete change
		if (changes.containsKey(value)) {
			newChange = true;
		}
		
		//If the size prop has changed, we have a complete change
		if (changes.containsKey(size)) {
			newChange = true;
			newChangeIsComplete = true;
		}
		
		if (newChange) {
			handleChange(newChangeIsComplete);
		}
	}
	
	private synchronized void handleChange(boolean newChangeIsComplete) {
		//Track whether we have had a complete change since last firing
		if (newChangeIsComplete) {
			completeChange = true;
		}

		//Ask for an update
		updateManager.updateRequiredBy(this);
	}

	public synchronized void update() {
		
		//TODO We use an AtomicBoolean here - probably not necessary since the
		//value will only ever be checked from the swing thread
		isFiring.set(true);
		
		//Fire appropriate change
		if (completeChange) {
			fireTableDataChanged();			
		} else {
			fireTableRowsUpdated(0, getRowCount()-1);
		}
		
		isFiring.set(false);
		
		//We now haven't had a complete change since the last firing
		completeChange = false;
	}

	@Override
	public boolean isFiring() {
		return isFiring.get();
	}
	
	@Override
	public void dispose() {
		updateManager.deregisterUpdatable(this);
		value.features().removeListener(this);
		size.features().removeListener(this);
	}
}
