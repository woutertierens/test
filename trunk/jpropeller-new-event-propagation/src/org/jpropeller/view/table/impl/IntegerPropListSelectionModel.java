package org.jpropeller.view.table.impl;

import java.util.List;
import java.util.Map;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jpropeller.properties.GenericEditableProp;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;

/**
 * A {@link ListSelectionModel} that uses the {@link Integer} value of
 * a {@link GenericEditableProp} as the single selected index.
 * @author shingoki
 */
public class IntegerPropListSelectionModel implements ListSelectionModel {

	private GenericEditableProp<Integer> indexProp;
	
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
	 * Create an {@link IntegerPropListSelectionModel}
	 * @param indexProp
	 * 		The {@link GenericEditableProp} giving the single selected
	 * index, or -1 when there is no selection
	 * @param setFilter
	 * 		A {@link SelectionSetFilter} that is checked whenever an attempt is made
	 * to set a new selection - if this returns true, the selection is set,
	 * otherwise the attempt is ignored 
	 */
	public IntegerPropListSelectionModel(GenericEditableProp<Integer> indexProp, SelectionSetFilter setFilter) {
		super();
		this.indexProp = indexProp;
		this.setFilter = setFilter;
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

		//FIXME Check this works - not sure it does
		//Perform initial sync from prop to delegate
		handlePropChange();
	}

	/**
	 * Respond to a change to the delegate {@link ListSelectionModel}
	 */
	private void handleDelegateChange() {
		//Update state of prop to match selection delegate, if possible,
		//or update delegate to match prop otherwise.
		
		//If we can set selection, update the prop to match the delegate
		if (setFilter.canSet()) {
			updatePropRunnable.run();
			
		//If we are ignoring changes to the delegate, then we need to 
		//revert the delegate back to mirroring the prop
		} else {
			handlePropChange();
		}
	}
	private Runnable updatePropRunnable = new Runnable() {
		@Override
		public void run() {
			int index = delegate.getMinSelectionIndex();
			if (indexProp.get() != index) {
				indexProp.set(index);
			} 
		}
	};
	
	/**
	 * Respond to a change to the prop  
	 */
	private void handlePropChange() {
		int index = indexProp.get();
		//If necessary, update the delegate to match the indexProp
		if (delegate.getMinSelectionIndex() != index) {
			if (index >= 0) {
				delegate.setSelectionInterval(index, index);				
			} else {
				delegate.clearSelection();
			}
		}
	}

	public String toString() {
		return "IntegerPropDefaultListSelectionModel using delegate: " + delegate.toString();
	}
	
	//Delegated ListSelectionModel methods
	
	public void addListSelectionListener(ListSelectionListener l) {
		delegate.addListSelectionListener(l);
	}

	public void addSelectionInterval(int index0, int index1) {
		delegate.addSelectionInterval(index0, index1);
	}

	public void clearSelection() {
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
		delegate.insertIndexInterval(index, length, before);
	}

	public boolean isSelectedIndex(int index) {
		return delegate.isSelectedIndex(index);
	}

	public boolean isSelectionEmpty() {
		return delegate.isSelectionEmpty();
	}


	public void removeIndexInterval(int index0, int index1) {
		delegate.removeIndexInterval(index0, index1);
	}

	public void removeListSelectionListener(ListSelectionListener l) {
		delegate.removeListSelectionListener(l);
	}

	public void removeSelectionInterval(int index0, int index1) {
		delegate.removeSelectionInterval(index0, index1);
	}

	public void setAnchorSelectionIndex(int anchorIndex) {
		delegate.setAnchorSelectionIndex(anchorIndex);
	}

	public void setLeadSelectionIndex(int leadIndex) {
		delegate.setLeadSelectionIndex(leadIndex);
	}

	public void setSelectionInterval(int index0, int index1) {
		delegate.setSelectionInterval(index0, index1);
	}

	public void setSelectionMode(int selectionMode) {
		delegate.setSelectionMode(selectionMode);
	}

	public void setValueIsAdjusting(boolean isAdjusting) {
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
