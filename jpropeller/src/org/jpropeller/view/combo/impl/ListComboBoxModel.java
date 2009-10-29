/*
 * Created on 07-Jan-2005
 */
package org.jpropeller.view.combo.impl;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.ListModel;

import org.jpropeller.collection.CList;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.reference.Reference;
import org.jpropeller.system.Props;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.view.combo.ListComboBoxReference;
import org.jpropeller.view.update.Updatable;
import org.jpropeller.view.update.UpdateManager;

/**
 * Presents a {@link CList} in a {@link Reference} as a {@link ComboBoxModel}.
 * This works around the inadequacies of {@link ListModel}, etc.
 * in order to present a reasonable display of a {@link CList} in a combo box.
 * 
 * @param <T> 	The type of element in the list
 */
public class ListComboBoxModel<T> extends AbstractListModel implements ComboBoxModel, ChangeListener, Updatable {

	/**
	 * Types of change we can have to the list we display
	 */
	private enum ListChangeType {
		//No change
		NONE,
		
		//Deep change
		DEEP,
		
		//Complete change
		COMPLETE
	}
	
	private static Logger logger = GeneralUtils.logger(ListComboBoxModel.class);    
    
	private ListComboBoxReference<T> model;

	//Track what type of list change we have had since last update
	private ListChangeType listChange = ListChangeType.NONE;
	
	//Track whether we have had a change to the selection property
	//since last update
	private boolean selectionChange = false;

	private int reportSize = -1;
	
	private Class<T> selectionClass;
	
	private UpdateManager updateManager;
	
	/**
	 * Create {@link ComboBoxModel} using data in a {@link Reference}
	 * @param model					The model to display
	 * @param selectionClass		The class to be accepted for selections, 
	 * 								allows for type safe selection methods
	 */
	public ListComboBoxModel(ListComboBoxReference<T> model, Class<T> selectionClass) {
		super();
		
		updateManager = Props.getPropSystem().getUpdateManager();
		updateManager.registerUpdatable(this);

		this.model = model;
		this.selectionClass = selectionClass;
		
		//Report correct size of list initially
		CList<T> list = model.value().get();
		reportSize = (list == null ? 0 : list.size());

		//Listen to our model
		model.features().addListener(this);
	}
	
	private CList<T> list() {
		return model.value().get();
	}
	
	private int listSize() {
		CList<T> list = model.value().get();
		return (list == null ? 0 : list.size());
	}
	
	public Object getElementAt(int index) {
		CList<T> list = list();
		if (index >= 0 && index < reportSize && index < list.size()) {
			return (list == null ? null : list.get(index));
		} else {
			logger.log(Level.WARNING, "Invalid index " + index + " outside reported size " + reportSize);
			return null;
		}
	}

	public int getSize() {
		return reportSize;
	}

	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {

		//Deal with any change to the value (the list)
		Change valueChange = changes.get(model.value());
		if (valueChange != null) {
			
			//If the value itself has changed, we have a complete change
			if (!valueChange.sameInstances()) {
				listChange = ListChangeType.COMPLETE;
				
			//We are still looking at the same list, see how it has been edited
			} else {
				//If instances are all the same in the list, then contents have changed
				//only - we don't have more or less rows
				Change change = changes.get(list());
				if (change == null || change.sameInstances()) {
					if (listChange == ListChangeType.NONE) listChange = ListChangeType.DEEP;
				//If the list itself has been edited, fire a change to number of rows
				} else {
					listChange = ListChangeType.COMPLETE;
				}
			}
		}
		
		//Now look at editing on the selection
		if (changes.get(model.selection()) != null) {
			selectionChange = true;
		}
		
		//Ask for an update if anything has changed
		if (listChange != ListChangeType.NONE || selectionChange) {
			updateManager.updateRequiredBy(this);
		}
	}
	
	@Override
	public synchronized void update() {
		
		//Deal with changes to the list
		//If we have just had some contents change, fire a change on all indices
		if (listChange == ListChangeType.DEEP) {
			int listSize = listSize();
			if (listSize == 0) {
				fireContentsChanged(this, 0, 0);				
			} else {
				fireContentsChanged(this, 0, listSize - 1);
			}
			
		//Otherwise we need to persuade listeners that anything might have changed - 
		//contents and/or size of list
		} else if (listChange == ListChangeType.COMPLETE){
			//This is the "safe" way to use the horribly broken ListModel change notification,
			//by pretending to be empty, telling listeners we ARE empty, then showing
			//the REAL list state, and telling listeners the entire list has just been added.
			//This resynchronizes the listeners' local storage of our size (if any) on all changes,
			//by avoiding the incremental changes that result using interval additions and removals.
			//It does mean that the GUI may redraw twice for each change, blanking then reappearing,
			//but really TableModel should be used wherever possible, and this effect should not
			//be visible in combo boxes, for example, where list models must be used.
			logger.log(Level.FINEST, "fullUpdate");
			int oldSize = reportSize;
			reportSize = 0;
			fireIntervalRemoved(this, 0, oldSize > 0 ? oldSize - 1 : 0);
			
			reportSize = listSize();
			fireIntervalAdded(this, 0, reportSize > 0 ? reportSize - 1 : 0);
		}

		//Fire selection change if we have had one
		if (selectionChange) {
			fireSelectionChange();
		}

		//We now haven't had a change since the last update
		listChange = ListChangeType.NONE;
		selectionChange = false;
	}

	public T getSelectedItem() {
		return model.selection().get();
	}

	//We suppress unchecked cast, since we check it is correct using selectionClass
	@SuppressWarnings("unchecked")
	public void setSelectedItem(Object anObject) {
		
		T selection = model.selection().get();
		
		//Only consider new selection if it is different from old one
		if ((selection != null && (!selection.equals( anObject ))) ||
				(selection == null && anObject != null)) {

			//Reject selection of a non-null type not allowed in the list
			if ((anObject != null) && (!selectionClass.isAssignableFrom(anObject.getClass()))) return;
			
			//Commit to the selection in the model reference
			model.selection().set((T)anObject);
		}
	}
	
	/**
	 * Notify listeners that our selection has changed (either a new
	 * item selected, or a change within the selected item itself)
	 */
	private void fireSelectionChange() {
		//Notify listeners of change in a strange way, taken from
		//DefaultComboBoxModel but not found in API documentation,
		//presumably this is what JComboBox expects
		fireContentsChanged(this, -1, -1);
	}

	@Override
	public void dispose() {
		updateManager.deregisterUpdatable(this);
		model.features().removeListener(this);
	}
	
	/**
	 * The underlying reference displayed by this {@link ComboBoxModel}
	 * @return		Model
	 */
	public ListComboBoxReference<T> getModel() {
		return model;
	}
}
