package org.jpropeller.properties.list.selection.impl;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.jpropeller.collection.CList;
import org.jpropeller.info.PropAccessType;
import org.jpropeller.info.PropEditability;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.ChangeableFeatures;
import org.jpropeller.properties.change.impl.ChangeDefault;
import org.jpropeller.properties.change.impl.ChangeableFeaturesDefault;
import org.jpropeller.properties.change.impl.InternalChangeImplementation;
import org.jpropeller.system.Props;
import org.jpropeller.util.GeneralUtils;

/**
 * An {@link Prop} which adjusts its own value
 * to maintain a selection index within the current
 * {@link CList} value of a {@link Prop}
 * @param <T>
 * 		The type of element in the {@link CList} 
 */
public class ListSelectionReferenceProp<T> implements Prop<Integer> {

	private final static Logger logger = GeneralUtils.logger(ListSelectionReferenceProp.class);

	private ChangeableFeatures features;
	private PropName<Integer> name;
	private Prop<CList<T>> listProp;
	private Map<CList<T>, ListSelectionProp> selectionMap;
	
	/**
	 * Create a prop
	 * @param name
	 * 		The name of the prop
	 * @param listProp
	 * 		The prop containing the list in which a selection is tracked 
	 */
	public ListSelectionReferenceProp(PropName<Integer> name, Prop<CList<T>> listProp) {
		this.name = name;
		this.listProp = listProp;
		
		//Need to track by identity - real list hashes/equality change with contents
		selectionMap = new IdentityHashMap<CList<T>, ListSelectionProp>();
		
		features = new ChangeableFeaturesDefault(new InternalChangeImplementation() {
			@Override
			public Change internalChange(Changeable changed, Change change,
					List<Changeable> initial, Map<Changeable, Change> changes) {
				return handleInternalChange(changed, change, initial, changes);
			}
		}, 
		this);
		
		//Listen to the list prop
		listProp.features().addChangeableListener(this);
	}

	private Change handleInternalChange(Changeable changed, Change change, List<Changeable> initial, Map<Changeable, Change> changes) {

		//If all we have is a deep change to the listProp, this indicates that
		//we still have the same CList instance in the listProp, and the 
		//listSelections have not altered so the same index is selected.
		//In this case there is no change to the selection index.
		boolean significant = false;
		
		//Check for any changes in our selections - if there are any,
		//then we may have a new index selected
		for (ListSelectionProp selection : selectionMap.values()) {
			if (changes.containsKey(selection)) {
				significant = true;
			}
		}

		//Check for a shallow listProp change - if we have a new
		//actual list in the listProp, this will reset our selection
		if (changed == listProp) {
			if (!change.sameInstances()) {
				significant = true;
			}
		}
		
		//If the change is significant, respond
		if (significant) {
			return ChangeDefault.instance(false, false);
		} else {
			return null;
		}
		
		//OPTIMISATION The above method is an optimisation to propagate less changes.
		//The unoptimised version is:
		//
		//return ChangeDefault.instance(false, false);
		
	}
	
	@Override
	public PropName<Integer> getName() {
		return name;
	}

	private ListSelectionProp currentSelection() {
		
		
		//Make sure we have a ListSelection on the current list
		CList<T> currentList = listProp.get();

		//If the list is null, just skip
		if (currentList == null) return null;
		
		ListSelectionProp currentSelection = selectionMap.get(currentList);
		
		//If we don't have a selection for this list, we need to make a new one, and get rid of any old ones
		if (currentSelection == null) {
			
			//We need to get old selection(s) to stop listening to lists, or they will hang around as long
			//as the list does. We also don't need to listen to them any more.
			for (ListSelectionProp oldSelection : selectionMap.values()) {
				oldSelection.dispose();
				oldSelection.features().removeChangeableListener(this);
			}			
			selectionMap.clear();
			
			currentSelection = new ListSelectionProp(currentList);
			selectionMap.put(currentList, currentSelection);
			
			//Listen to the new selection, so we can change when it does
			currentSelection.features().addChangeableListener(this);
			
			logger.finest("Made a new selection " + currentSelection);

		} else {
			logger.finest("Reusing selection " + currentSelection);
		}
		
		return currentSelection;
	}
	
	@Override
	public Integer get() {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		try {
			ListSelectionProp currentSelection = currentSelection();
			if (currentSelection == null) {
				return -1;
			} else {
				return currentSelection.get();
			}
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
		}
	}

	@Override
	public void set(Integer value) {
		Props.getPropSystem().getChangeSystem().prepareChange(this);
		try {

			//Set the selection
			ListSelectionProp currentSelection = currentSelection();
			if (currentSelection != null) {
				currentSelection.set(value);
			}

			//Do not propagate the change we just made - it will be propagated by the currentSelection,
			//and we will then notice this change and show we have changed too. This avoids having two initial changes
			//from one set()
			
		} finally {
			Props.getPropSystem().getChangeSystem().concludeChange(this);
		}
	}
	
	@Override
	public PropEditability getEditability() {
		return PropEditability.EDITABLE;
	}

	@Override
	public PropAccessType getAccessType() {
		return PropAccessType.SINGLE;
	}

	@Override
	public ChangeableFeatures features() {
		return features;
	}

	@Override
	public String toString() {
		return "ListSelectionReferenceProp, selected index " + get();
	}
	
}
