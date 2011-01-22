package org.jpropeller.view.table.impl;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.util.Listeners;
import org.jpropeller.view.table.TableRowView;
import org.jpropeller.view.table.TableRowViewListener;

/**
 * {@link TableRowView} with a single column, which displays
 * a boolean value that is true only if the row value is
 * identical to a value in a separate {@link Prop}
 * 
 * Editing the boolean value to true will set the separate
 * {@link Prop} to the value of the row, and editing to false
 * will null the separate {@link Prop} (if this feature is
 * enabled).
 * 
 * It may be useful in many cases to use a constraint to ensure
 * that the separate {@link Prop} contains a value that is present
 * in the list of values displayed by the table, but this is not
 * required.
 * 
 * @param <R>		The type of row
 */
public class SpecificInstanceRowView<R> implements TableRowView<R>, ChangeListener {

	private final static Logger logger = GeneralUtils.logger(SpecificInstanceRowView.class);
	
	private final Prop<R> specificInstance;

	private final Listeners<TableRowViewListener> listeners = new Listeners<TableRowViewListener>();

	private final boolean editable;
	private final boolean allowClearing;
	private final String columnName;
	
	/**
	 * Make a {@link SpecificInstanceRowView}
	 * @param specificInstance		The specific instance - only rows
	 * 					containing an identical value will be
	 * 					displayed as true
	 * @param editable		Whether values are editable
	 * @param allowClearing	Whether the rowview allows editing that
	 * 						clears the value to null.
	 * @param columnName	The name of the single column
	 */
	public SpecificInstanceRowView(
			Prop<R> specificInstance, boolean editable, boolean allowClearing, String columnName) {
		this.specificInstance = specificInstance;
		specificInstance.features().addListener(this);
		this.editable = editable;
		this.allowClearing = allowClearing;
		this.columnName = columnName;
	}
	
	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		fireChange();
	}
	
	private void fireChange() {
		for (TableRowViewListener listener : listeners) {
			//We don't have column changes, only row changes
			listener.tableRowViewChanged(this, false);
		}
	}
	
	@Override
	public Object getColumn(R row, int column) {

		if (column != 0) {
			return null;
		}
		
		R v = specificInstance.get();
		
		if (v == null) {
			return false;
		}
		
		return (v == row);
	}

	@Override
	public Class<?> getColumnClass(int column) {
		return Boolean.class;
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public String getColumnName(int column) {
		return columnName;
	}

	@Override
	public boolean isEditable(R row, int column) {
		return editable;
	}

	@Override
	public void setColumn(R row, int column, Object value) {
		if (!editable) return;
		
		boolean newState;
		if (value instanceof Boolean) {
			newState = (Boolean) value;
		} else {
			return;
		}
		
		if (!newState && !allowClearing) {
			return;
		}

		if (newState) {
			specificInstance.set(row);
		} else {
			specificInstance.set(null);
		}
	}

	@Override
	public void addListener(TableRowViewListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(TableRowViewListener listener) {
		if (!listeners.remove(listener)) {
			logger.log(Level.FINE, "Removed TableRowViewListener which was not registered.", new Exception("Stack Trace"));
		}
	}
	
	@Override
	public void dispose() {
		specificInstance.features().removeListener(this);
	}
}
