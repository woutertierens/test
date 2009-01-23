package org.jpropeller.view.table.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.jpropeller.collection.ObservableList;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.reference.Reference;
import org.jpropeller.system.Props;
import org.jpropeller.view.JView;
import org.jpropeller.view.View;
import org.jpropeller.view.table.TableRowView;
import org.jpropeller.view.update.Updatable;
import org.jpropeller.view.update.UpdateManager;

/**
 * A {@link TableModel} displaying an {@link ObservableList},
 * accessed by a {@link Reference} to be compatible with use
 * by a {@link View}, where elements of the referenced 
 * {@link List} are displayed as rows, and the
 * columns of each row are given by a {@link TableRowView}
 * Uses {@link UpdateManager}, and in general provides the
 * "intelligence" required for a {@link JView} implemented
 * by simply displaying this {@link TableModel} in a 
 * {@link JTable}.
 * @author shingoki
 * @param <T> 
 * 		The type of data in the list
 */
public class ListTableModel<T> extends AbstractTableModel implements ChangeListener, Updatable {

	private Reference<? extends ObservableList<T>> model;
	private TableRowView<? super T> rowView;
	
	//Track whether we have had a complete change since last firing
	private boolean completeChange = false;
	
	private UpdateManager updateManager;
	private AtomicBoolean isFiring = new AtomicBoolean(false);
	
	/**
	 * Create a {@link ListTableModel}
	 * @param model
	 * 		The model, a {@link Reference} to the {@link ObservableList} we will display
	 * @param rowView
	 * 		The view of each row
	 */
	public ListTableModel(Reference<? extends ObservableList<T>> model, TableRowView<? super T> rowView) {
		super();
		
		updateManager = Props.getPropSystem().getUpdateManager();
		updateManager.registerUpdatable(this);
		
		this.rowView = rowView;
		this.model = model;
		
		//Listen to the value in our model
		model.value().features().addListener(this);
	}

	@Override
	public int getColumnCount() {
		return rowView.getColumnCount();
	}

	private ObservableList<T> list() {
		return model.value().get();
	}
	
	@Override
	public int getRowCount() {
		return list().size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return rowView.getColumn(list().get(rowIndex), columnIndex);
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
		return rowView.isEditable(list().get(rowIndex), columnIndex);
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		rowView.setColumn(list().get(rowIndex), columnIndex, value);
	}

	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		//FIXME fire finer-grained changes if possible
		
		//If the model has had a new instance set then we have a complete change
		if (!changes.get(model).sameInstances()) {
			handleChange(true);
			
		//If the model still points to the same list instance, see what change it has had
		} else {
			//If instances are all the same in the list, then contents have changed
			//only - we don't have more or less rows
			if (changes.get(list()).sameInstances()) {
				handleChange(false);
			//If the list itself has been edited, fire a change to number of rows
			} else {
				handleChange(true);
			}
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
		
		//TODOWe use an AtomicBoolean here - probably not necessary since the
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
		model.features().removeListener(this);
	}
	
}
