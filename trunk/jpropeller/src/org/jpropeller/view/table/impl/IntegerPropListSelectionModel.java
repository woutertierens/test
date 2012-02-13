package org.jpropeller.view.table.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jpropeller.collection.CCollection;
import org.jpropeller.concurrency.CancellableResponse;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.task.Task;
import org.jpropeller.task.impl.BuildTask;
import org.jpropeller.task.impl.SynchronousTaskExecutor;

/**
 * A {@link ListSelectionModel} for {@link JTable} row selection,
 * that uses the {@link Integer} value of a {@link Prop} as the 
 * single selected index.
 * 
 * TODO this does not handle filtering of the table, since then it cannot
 * select rows that have been hidden.
 */
class IntegerPropListSelectionModel implements ListSelectionModel {

	private Prop<Integer> indexProp;
	
	/**
	 * The delegate {@link ListSelectionModel} actual provides the state
	 * and most of the handling for this {@link ListSelectionModel}. We just
	 * perform two-way synchronisation between the {@link ListSelectionModel} and
	 * our indexProp.
	 */
	private DefaultListSelectionModel delegate;

	/**
	 * This filter determines, at any point in time, whether changes in the delegate
	 * {@link ListSelectionModel} will be passed through to the indexProp, or ignored.
	 */
	private SelectionSetFilter setFilter;
	
	/**
	 * The table we are working with, so we can translate row indices allowing for sorting
	 */
	private JTable table;

	private boolean adjustingDelegate = false;

	/**
	 * Create an {@link IntegerPropListSelectionModel}
	 * 
	 * @param indexProp		The {@link Prop} giving the single selected
	 * 						index, or -1 when there is no selection
	 * @param setFilter		A {@link SelectionSetFilter} that is checked 
	 * 						whenever an attempt is made to set a new selection
	 * 						 - if this returns true, the selection is set,
	 * 						otherwise the attempt is ignored 
	 * @param table			The {@link JTable} we are working with. Must be the
	 * 						table with which this model is used.
	 */
	IntegerPropListSelectionModel(Prop<Integer> indexProp, SelectionSetFilter setFilter, JTable table) {
		super();
		this.indexProp = indexProp;
		this.setFilter = setFilter;
		this.table = table;
		
		indexProp.features().addListener(new ChangeListener() {
			@Override
			public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
				handlePropChange();
			}
		});
		
		//Set up the delegate - single selection only
		delegate = new DefaultListSelectionModel();
		delegate.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		//Listen to the delegate - when it changes, update the prop
		//as appropriate
		delegate.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				//Don't respond if selection is just adjusting
				if (!e.getValueIsAdjusting()) {
					handleDelegateChange();
				}
			}
		});

		//Perform initial sync from prop to delegate
		handlePropChange();
	}

	/**
	 * Respond to a change to the delegate {@link ListSelectionModel}
	 */
	private void handleDelegateChange() {
		
		//Skip when we are actually adjusting delegate in this class, so we
		//don't respond to our own changes.
		if (adjustingDelegate) return;
		
		//Update state of prop to match selection delegate, if possible,
		//or update delegate to match prop otherwise.
		
		//If we can set selection, update the prop to match the delegate
		if (setFilter.canSet()) {
			int index = table.convertRowIndexToModel(delegate.getMinSelectionIndex());
			if (indexProp.get() != index) {
				indexProp.set(index);
			} 
		//If we are ignoring changes to the delegate, then we need to 
		//revert the delegate back to mirroring the prop
		} else {
			handlePropChange();
		}
	}
	
	/**
	 * Respond to a change to the prop  
	 */
	private void handlePropChange() {
		adjustingDelegate = true;
		try {
	
			int index = -1;
			try {
				index = table.convertRowIndexToView(indexProp.get());
			} catch (IndexOutOfBoundsException ioobe) {
				//If index is out of bounds, treat as no selection
			}
	
			//Note that index may be -1 if the model row is not visible
			//in the table, due to filtering. However this still works since
			//index = -1 is used to indicate no selection, and will be handled
			//appropriately.
	
			//If necessary, update the delegate to match the indexProp
			if (delegate.getMinSelectionIndex() != index) {
				if (index >= 0) {
					delegate.setSelectionInterval(index, index);				
				} else {
					delegate.clearSelection();
				}
			}
		} finally {
			adjustingDelegate = false;
		}
	}

	public String toString() {
		return "IntegerPropDefaultListSelectionModel using delegate: " + delegate.toString();
	}
	
	/**
	 * Create a {@link SynchronousTaskExecutor} executing a task that
	 * will update the specified index to always have a selection
	 * when a collection contains entries.
	 * @param index				The index to update
	 * @param collection		The collection index is a selection within
	 * @return	A new {@link SynchronousTaskExecutor} - make sure to retain a reference
	 * 			to allow the task to keep executing.
	 */
	public final static SynchronousTaskExecutor makeEnforcedSelectionTask(final Prop<Integer> index, final Prop<? extends CCollection<?>> collection) {
		
		//Make task to do the resize 
		Task task = BuildTask.on(index, collection).withResponse(new CancellableResponse() {
			@Override
			public void respond(AtomicBoolean shouldCancel) {
				int size = collection.get().size();
				int ind = index.get();
				
				if (ind == -1 && size != 0) {
					index.set(0);
				}
			}
		});
		return new SynchronousTaskExecutor(task);
	}
	
	//Delegated ListSelectionModel methods
	
	public void addListSelectionListener(ListSelectionListener l) {
		delegate.addListSelectionListener(l);
	}

	public void addSelectionInterval(int index0, int index1) {
		if (!setFilter.canSet()) return;
		delegate.addSelectionInterval(index0, index1);
	}

	public void clearSelection() {
		if (!setFilter.canSet()) return;
		delegate.clearSelection();
	}

	public int getAnchorSelectionIndex() {
		return delegate.getAnchorSelectionIndex();
	}

	public int getLeadSelectionIndex() {
		return delegate.getLeadSelectionIndex();
	}

	public int getMaxSelectionIndex() {
		return delegate.getMaxSelectionIndex();
	}

	public int getMinSelectionIndex() {
		return delegate.getMinSelectionIndex();
	}

	public int getSelectionMode() {
		return delegate.getSelectionMode();
	}

	public boolean getValueIsAdjusting() {
		return delegate.getValueIsAdjusting();
	}

	public void insertIndexInterval(int index, int length, boolean before) {
		if (!setFilter.canSet()) return;
		delegate.insertIndexInterval(index, length, before);
	}

	public boolean isSelectedIndex(int index) {
		return delegate.isSelectedIndex(index);
	}

	public boolean isSelectionEmpty() {
		return delegate.isSelectionEmpty();
	}


	public void removeIndexInterval(int index0, int index1) {
		if (!setFilter.canSet()) return;
		delegate.removeIndexInterval(index0, index1);
	}

	public void removeListSelectionListener(ListSelectionListener l) {
		delegate.removeListSelectionListener(l);
	}

	public void removeSelectionInterval(int index0, int index1) {
		if (!setFilter.canSet()) return;
		delegate.removeSelectionInterval(index0, index1);
	}

	public void setAnchorSelectionIndex(int anchorIndex) {
		if (!setFilter.canSet()) return;
		delegate.setAnchorSelectionIndex(anchorIndex);
	}

	public void setLeadSelectionIndex(int leadIndex) {
		if (!setFilter.canSet()) return;
		delegate.setLeadSelectionIndex(leadIndex);
	}

	public void setSelectionInterval(int index0, int index1) {
		if (!setFilter.canSet()) return;
		delegate.setSelectionInterval(index0, index1);
	}

	public void setSelectionMode(int selectionMode) {
		if (!setFilter.canSet()) return;
		delegate.setSelectionMode(selectionMode);
	}

	public void setValueIsAdjusting(boolean isAdjusting) {
		if (!setFilter.canSet()) return;
		delegate.setValueIsAdjusting(isAdjusting);
	}
	
	//Methods in delegate but not in ListSelectionModel
//	public boolean isLeadAnchorNotificationEnabled() {
//		return delegate.isLeadAnchorNotificationEnabled();
//	}
//	public void moveLeadSelectionIndex(int leadIndex) {
//		delegate.moveLeadSelectionIndex(leadIndex);
//	}
//	public void setLeadAnchorNotificationEnabled(boolean flag) {
//		delegate.setLeadAnchorNotificationEnabled(flag);
//	}
	
}
