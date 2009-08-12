package org.jpropeller.view.table.impl;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jpropeller.collection.CCollection;
import org.jpropeller.collection.CList;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.view.View;

/**
 * A {@link ListSelectionModel} that uses the {@link Integer}s in
 * a {@link CCollection} of {@link Integer} as the selected indices.
 * Changes are reflected both ways - when the selection model changes,
 * the {@link CList} is updated, and vice versa. Thus this is essentially
 * a {@link ListSelectionModel} as a {@link View} of a {@link CList}
 * 
 * TODO this does not handle filtering of the table, since then it cannot
 * select rows that have been hidden.
 */
class IntegersListSelectionModel implements ListSelectionModel {

	private Prop<? extends CCollection<Integer>> indicesProp;
	
	/**
	 * The delegate {@link ListSelectionModel} actual provides the state
	 * and most of the handling for this {@link ListSelectionModel}. We just
	 * perform two-way synchronisation between the {@link ListSelectionModel} and
	 * our indexProp.
	 */
	private DefaultListSelectionModel delegate;

	/**
	 * Maintain an up to date mirror of the delegate's selection as a set
	 * of integers in MODEL numbering. It's a shame we have to do this, but we need to use
	 * a delegate to inherit the various weird behaviour of ListSelectionModel,
	 * so we must accept a little extra memory use.
	 * Sorting means that if we are handling a CList prop, we put the
	 * indices in the list in order, which may be convenient for users.
	 */
	private final SortedSet<Integer> selection = new TreeSet<Integer>();
	
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
	 * Create an {@link IntegersListSelectionModel}
	 * 
	 * @param indicesProp	The {@link Prop} giving the selected
	 * 						indices, or empty when there is no selection
	 * @param setFilter		A {@link SelectionSetFilter} that is checked 
	 * 						whenever an attempt is made to set a new selection
	 * 						 - if this returns true, the selection is set,
	 * 						otherwise the attempt is ignored 
	 * @param table			The {@link JTable} we are working with. Must be the
	 * 						table with which this model is used.
	 */
	IntegersListSelectionModel(
			Prop<? extends CCollection<Integer>> indicesProp, 
			SelectionSetFilter setFilter,
			JTable table) {
		super();
		this.indicesProp = indicesProp;
		this.setFilter = setFilter;
		this.table = table;

		indicesProp.features().addListener(new ChangeListener() {
			@Override
			public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
				handlePropChange();
			}
		});

		delegate = new DefaultListSelectionModel();
		
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
		
		//Skip when we are actually adjusting delegate, so we
		//don't respond to our own changes.
		if (adjustingDelegate) return;
		
		//Refresh our selection set
		selection.clear();
		if (!delegate.isSelectionEmpty()) {
			for (int i = delegate.getMinSelectionIndex(); i <= delegate.getMaxSelectionIndex(); i++) {
				if (delegate.isSelectedIndex(i)) {
					selection.add(table.convertRowIndexToModel(i));
				}
			}
		}

		//If we can set selection, update the prop to match the delegate
		if (setFilter.canSet()) {
			
			//If the indexProp does not contain exactly the same selection set,
			//replace the props selection set (as a single operation)
			if (indicesProp.get().size() != selection.size() || !indicesProp.get().containsAll(selection)) {
				indicesProp.get().replace(selection);
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
			//StopWatch time = new StopWatch();
	
			//If the index prop has actually changed to be different from the selection,
			//then update the selection
			if (indicesProp.get().size() != selection.size() || !indicesProp.get().containsAll(selection)) {
				
				//FIXME Can we find a more elegant way to do this?
				
				//Make change as an adjustment - this may make it more efficient (and also prevents this
				//class from responding to the changes). Would be better to
				//do this as a "batched" change, but it is hard to see how to do this
				delegate.setValueIsAdjusting(true);
	
				int originalAnchor = delegate.getAnchorSelectionIndex();
				
				delegate.clearSelection();
				
				for (Integer i : indicesProp.get()) {
					int tableRow = table.convertRowIndexToView(i);
					//Only select a row if it is visible
					if (tableRow != -1) {
						delegate.addSelectionInterval(tableRow, tableRow);
					}
				}
				
				//Restore the original anchor if it is still selected
				if (delegate.isSelectedIndex(originalAnchor)) {
					delegate.setAnchorSelectionIndex(originalAnchor);
				}
				
				delegate.setValueIsAdjusting(false);
			}
		} finally {
			adjustingDelegate = false;
		}
		
		//System.out.println("prop change took " + time.stop());

	}

	public String toString() {
		return "IntegersListSelectionModel using delegate: " + delegate.toString();
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
