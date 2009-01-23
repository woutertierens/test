package org.jpropeller.properties.list.impl;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.jpropeller.collection.ObservableList;
import org.jpropeller.info.PropInfo;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.GenericProp;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.ChangeableFeatures;
import org.jpropeller.properties.change.impl.ChangeDefault;
import org.jpropeller.properties.change.impl.ChangeableFeaturesDefault;
import org.jpropeller.properties.change.impl.InternalChangeImplementation;
import org.jpropeller.system.Props;
import org.jpropeller.util.GeneralUtils;

/**
 * An {@link EditableProp} which adjusts its own value
 * to maintain a selection index within the current
 * {@link ObservableList} value of a {@link GenericProp}
 * @param <T>
 * 		The type of element in the {@link ObservableList} 
 */
public class ListSelectionReferenceProp<T> implements EditableProp<Integer> {

	private final static Logger logger = GeneralUtils.logger(ListSelectionReferenceProp.class);

	private ChangeableFeatures features;
	private PropName<EditableProp<Integer>, Integer> name;
	private GenericProp<ObservableList<T>> listProp;
	private Map<ObservableList<T>, ListSelection> selectionMap;
	
	/**
	 * Create a prop
	 * @param name
	 * 		The name of the prop
	 * @param listProp
	 * 		The prop containing the list in which a selection is tracked 
	 */
	public ListSelectionReferenceProp(PropName<EditableProp<Integer>, Integer> name, GenericProp<ObservableList<T>> listProp) {
		this.name = name;
		this.listProp = listProp;
		
		//Need to track by identity - real list hashes/equality change with contents
		selectionMap = new IdentityHashMap<ObservableList<T>, ListSelection>();
		
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
		
		//Changes to anything we listen to (our listProp and ListSelections) could indicate a change
		return ChangeDefault.instance(false, false);
	}
	
	@Override
	public PropName<? extends EditableProp<Integer>, Integer> getName() {
		return name;
	}

	@Override
	public void set(Integer value) {
		Props.getPropSystem().getChangeSystem().prepareChange(this);
		try {

			//Set the selection
			currentSelection().selection().set(value);

			//Do not propagate the change we just made - it will be propagated by the currentSelection,
			//and we will then notice this change and show we have changed too. This avoids having two initial changes
			//from one set()
			
		} finally {
			Props.getPropSystem().getChangeSystem().concludeChange(this);
		}
	}

	private ListSelection currentSelection() {
		
		
		//Make sure we have a ListSelection on the current list
		ObservableList<T> currentList = listProp.get();

		ListSelection currentSelection = selectionMap.get(currentList);
		
		//If we don't have a selection for this list, we need to make a new one, and get rid of any old ones
		if (currentSelection == null) {
			
			//We need to get old selection(s) to stop listening to lists, or they will hang around as long
			//as the list does
			for (ListSelection oldSelection : selectionMap.values()) {
				oldSelection.dispose();
			}			
			selectionMap.clear();
			
			currentSelection = new ListSelection(currentList);
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
			return currentSelection().selection().get();
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
		}
	}

	@Override
	public PropInfo getInfo() {
		return PropInfo.EDITABLE;
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
