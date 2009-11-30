package org.jpropeller.view.table.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.jpropeller.collection.CList;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.reference.Reference;
import org.jpropeller.system.Props;
import org.jpropeller.view.JView;
import org.jpropeller.view.View;
import org.jpropeller.view.table.FiringTableModel;
import org.jpropeller.view.table.TableRowView;
import org.jpropeller.view.table.TableRowViewListener;
import org.jpropeller.view.update.Updatable;
import org.jpropeller.view.update.UpdateManager;

/**
 * A {@link TableModel} displaying an {@link CList},
 * accessed by a {@link Reference} to be compatible with use
 * by a {@link View}, where elements of the referenced 
 * {@link List} are displayed as rows, and the
 * columns of each row are given by a {@link TableRowView}
 * Uses {@link UpdateManager}, and in general provides the
 * "intelligence" required for a {@link JView} implemented
 * by simply displaying this {@link TableModel} in a 
 * {@link JTable}.
 * @param <T> 
 * 		The type of data in the list
 */
public class ListTableModel<T> 	extends AbstractTableModel 
								implements FiringTableModel, ChangeListener, Updatable, TableRowViewListener {

	private Prop<? extends CList<T>> list;
	private TableRowView<? super T> rowView;
	
	//Track whether we have had a complete change and/or column change since last firing
	private boolean completeChange = false;
	private boolean columnChange = false;
	
	private UpdateManager updateManager;
	private AtomicBoolean isFiring = new AtomicBoolean(false);
	
	private final int indexColumn;
	private final String indexName;
	private final int firstRowViewColumn;
	private final int indexBase;

	/**
	 * Create a {@link ListTableModel} that doesn't show the row index as a column
	 * @param model		The model, a {@link Reference} to the {@link CList} we will display
	 * @param rowView	The view of each row. SHould only be provided to ONE {@link ListTableModel}
	 */
	public ListTableModel(Reference<? extends CList<T>> model, TableRowView<? super T> rowView) {
		this(model, rowView, false, "", 0);
	}
	
	/**
	 * Create a {@link ListTableModel} that doesn't show the row index as a column
	 * @param list		A {@link Prop} containing the {@link CList} we will display
	 * @param rowView	The view of each row. SHould only be provided to ONE {@link ListTableModel}
	 */
	public ListTableModel(Prop<? extends CList<T>> list, TableRowView<? super T> rowView) {
		this(list, rowView, false, "", 0);
	}
	
	/**
	 * Create a {@link ListTableModel}
	 * @param model		The model, a {@link Reference} to the {@link CList} we will display
	 * @param rowView	The view of each row
	 * @param indexColumn		True if the first column should show the row index 
	 * @param indexName 		The name for first column, if indexColumn is true
	 * @param indexBase 		The base for row index numbering - 0 to be 0-based, 0, 1, 2...
	 * 							1 to be 1-based, 1, 2, 3...
	 */
	public ListTableModel(
			Reference<? extends CList<T>> model, 
			TableRowView<? super T> rowView,
			boolean indexColumn,
			String indexName,
			int indexBase) {
		this(model.value(), rowView, indexColumn, indexName, indexBase);
	}

	
	/**
	 * Create a {@link ListTableModel}
	 * @param list				A {@link Prop} containing the {@link CList} we will display
	 * @param rowView			The view of each row
	 * @param indexColumn		True if the first column should show the row index 
	 * @param indexName 		The name for first column, if indexColumn is true
	 * @param indexBase 		The base for row index numbering - 0 to be 0-based, 0, 1, 2...
	 * 							1 to be 1-based, 1, 2, 3...
	 */
	public ListTableModel(
			Prop<? extends CList<T>> list, 
			TableRowView<? super T> rowView,
			boolean indexColumn,
			String indexName,
			int indexBase) {
		super();
		
		updateManager = Props.getPropSystem().getUpdateManager();
		updateManager.registerUpdatable(this);
		
		this.rowView = rowView;
		
		rowView.addListener(this);
		
		this.list = list;
		
		if (indexColumn) {
			this.indexColumn = 0;
			this.firstRowViewColumn = 1;
		} else {
			this.indexColumn = -1;
			this.firstRowViewColumn = 0;			
		}
		
		this.indexName = indexName;
		this.indexBase = indexBase;
		
		//Listen to the value prop of our model
		list.features().addListener(this);
	}
	
	@Override
	public int getColumnCount() {
		return rowView.getColumnCount() + firstRowViewColumn;
	}

	private CList<T> list() {
		return list.get();
	}
	
	@Override
	public int getRowCount() {
		CList<T> list = list();
		if (list == null) {
			return 0;
		} else {
			return list.size();			
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		CList<T> list = list();
		if (list == null) {
			return null;
		} else {
			if (columnIndex == indexColumn) {
				return rowIndex + indexBase;
			} else {
				if (rowIndex >= list.size()) {
					return null;
				}
				T value = list.get(rowIndex);
				if (value == null) {
					return null;
				}
				return rowView.getColumn(value, columnIndex - firstRowViewColumn);
			}
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == indexColumn) {
			return Integer.class;
		} else {
			return rowView.getColumnClass(columnIndex - firstRowViewColumn);
		}
	}

	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex == indexColumn) {
			return indexName;
		} else {
			return rowView.getColumnName(columnIndex - firstRowViewColumn);
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		CList<T> list = list();
		if (list == null) {
			return false;
		} else {
			if (columnIndex == indexColumn) {
				return false;
			} else {
				return rowView.isEditable(list.get(rowIndex), columnIndex - firstRowViewColumn);
			}
		}
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		CList<T> list = list();
		if (list != null) {
			if (columnIndex == indexColumn) {
				return;
			} else {
				rowView.setColumn(list.get(rowIndex), columnIndex - firstRowViewColumn, value);
			}
		}
	}

	@Override
	public void tableRowViewChanged(TableRowView<?> tableRowView) {
		handleChange(true, true);
	}

	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		//FIXME fire finer-grained changes if possible
		
		//If the prop has had a new instance set then we have a complete change
		if (!changes.get(list).sameInstances()) {
			handleChange(true, false);
			
		//If the prop still points to the same list instance, see what change it has had
		} else {
			//If instances are all the same in the list, then contents have changed
			//only - we don't have more or less rows
			Change change = changes.get(list());
			if (change == null || change.sameInstances()) {
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
		rowView.removeListener(this);
	}

}
