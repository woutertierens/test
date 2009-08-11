package org.jpropeller.properties.list.selection.impl;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.jpropeller.collection.CollectionChangeType;
import org.jpropeller.collection.ListDelta;
import org.jpropeller.collection.CList;
import org.jpropeller.info.PropAccessType;
import org.jpropeller.info.PropEditability;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeType;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.ChangeableFeatures;
import org.jpropeller.properties.change.ListChange;
import org.jpropeller.properties.change.impl.ChangeDefault;
import org.jpropeller.properties.change.impl.ChangeableFeaturesDefault;
import org.jpropeller.properties.change.impl.InternalChangeImplementation;
import org.jpropeller.system.Props;
import org.jpropeller.util.GeneralUtils;

/**
 * An {@link Prop} which adjusts its own value
 * to maintain a selection index within a particular
 * instance of {@link CList}
 */
public class ListSelectionProp implements Prop<Integer> {

	private final static Logger logger = GeneralUtils.logger(ListSelectionProp.class);

	private ChangeableFeatures features;
	private PropName<Integer> name;
	private int index;
	private boolean postSelectOnDeletion = true;
	private CList<?> list;

	/**
	 * Create a prop with name "selection"
	 * @param list		The list in which a selection is tracked
	 */
	public ListSelectionProp(CList<?> list) {
		this(PropName.create("selection", Integer.class), list);
	}

	/**
	 * Create a prop
	 * @param name		The name of the prop
	 * @param list		The list in which a selection is tracked 
	 */
	public ListSelectionProp(PropName<Integer> name, CList<?> list) {
		this.index = 0;
		this.name = name;
		this.list = list;
		
		features = new ChangeableFeaturesDefault(new InternalChangeImplementation() {
			@Override
			public Change internalChange(Changeable changed, Change change,
					List<Changeable> initial, Map<Changeable, Change> changes) {
				return handleInternalChange(changed, change, initial, changes);
			}
		}, 
		this);
		
		//Listen to the list
		list.features().addChangeableListener(this);
	}

	private Change handleInternalChange(Changeable changed, Change change, List<Changeable> initial, Map<Changeable, Change> changes) {
		//We only see a change when our list has changed - then we just need to update the
		//selection according to the old selection and the list change.
		//We can do this safely here since it is quick, and only uses our own internal information
		//plus information from the change on the list
		
		int initialIndex = index;
		
		if (change.type() == ChangeType.LIST) {
			ListChange listChange = (ListChange) change;
			List<ListDelta> deltaList = listChange.getListDeltas();
			
			logger.finest("ListSelectionProp saw List change, index was " + index + ": " + deltaList);
			
			for (ListDelta delta : deltaList) {
				
				if (delta.getType() == CollectionChangeType.ALTERATION) {
					//Preserve selection even when altered
				} else if (delta.getType() == CollectionChangeType.CLEAR) {
					resetIndex();
				} else if (delta.getType() == CollectionChangeType.COMPLETE) {
					resetIndex();
				} else if (delta.getType() == CollectionChangeType.DELETION) {
					
					//No change at all if we are before the first changed index
					if (index < delta.getFirstChangedIndex()) {
						//No alteration
						
					//Work out whether we are AFTER the removed range - e.g. if first removed is
					//2, and change size is -3, we removed 2,3,4 and so everything from (2-(-3) = 5) inclusive
					//is still present. We just need to shift selection back to allow for removed indices
					} else if (index >= delta.getFirstChangedIndex() - delta.getChangeSize()) {
						index += delta.getChangeSize();
					//If we are in the removed range
					} else {
						
						//Select immediately after deletion if desired
						if (postSelectOnDeletion) {
							//If there is still an index after the deleted range, it will be
							//first changed index
							index = delta.getFirstChangedIndex();
							
							//If the deletion was up to the end of the list, index will be
							//outside list - in this case just use last index
							if (index >= delta.getNewSize()) {
								index = delta.getNewSize() - 1; 
							}
						//Otherwise just reset index
						} else {
							resetIndex();
						}
					}
				} else if (delta.getType() == CollectionChangeType.INSERTION) {
					if (index < delta.getFirstChangedIndex()) {
						//No alteration
					} else {
						index += delta.getChangeSize();
					}				
				}
			}
			
			if (initialIndex != index) {
				logger.finest("Changed index to " + index);
				return ChangeDefault.instance(false, false);
			} else {
				return null;
			}
		}
	
		return null;
	}

	
	private void resetIndex() {
		index = -1;
	}

	@Override
	public PropName<Integer> getName() {
		return name;
	}

	@Override
	public void set(Integer value) {
		Props.getPropSystem().getChangeSystem().prepareChange(this);
		
		try {
			//We accept any index value - we will correct and validate it when get() is called
			this.index = value;
			
			//Propagate the change we just made
			Props.getPropSystem().getChangeSystem().propagateChange(this, ChangeDefault.instance(
					true,	//Change IS initial 
					false	//Instance is NOT the same - new value set
					));
		} finally {
			Props.getPropSystem().getChangeSystem().concludeChange(this);
		}
	}

	@Override
	public Integer get() {
		Props.getPropSystem().getChangeSystem().prepareRead(this);
		try {
			//Check whether index is valid - if not, reset it first
			if (index < -1 || index >= list.size()) {
				resetIndex();
			}
			
			return index;
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(this);
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
		return "ListSelectionProp, selected index " + index;
	}
	
	/**
	 * Stop listening to the list and updating selection
	 */
	public void dispose() {
		list.features().removeChangeableListener(this);
	}
	
}
