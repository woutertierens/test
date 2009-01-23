package org.jpropeller.properties.list.impl;

import org.jpropeller.bean.Bean;
import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.collection.ObservableList;
import org.jpropeller.name.PropName;

/**
 * A {@link Bean} that has a single {@link ListSelectionProp}, tracking a selection
 * in a list
 * @author shingoki
 */
public class ListSelection extends BeanDefault {

	ListSelectionProp selection;

	/**
	 * Make a new {@link ListSelection}
	 * @param list
	 * 		The list in which we are tracking a selection
	 */
	public ListSelection(ObservableList<?> list) {
		selection = addProp(new ListSelectionProp(PropName.editable("selection", Integer.class), list));
	}
	
	/**
	 * The selection index in the list - can be set and got,
	 * and will move itself to track the same element in the list
	 * when possible. Will reset itself to -1 when it cannot track
	 * the element (e.g. element is removed, list is cleared or
	 * completely changed, etc.)
	 * @return
	 * 		The selection index
	 */
	public ListSelectionProp selection() {
		return selection;
	}

	/**
	 * Stop listening to the list and updating selection
	 */
	public void dispose() {
		selection.dispose();
	}
	
	@Override
	public String toString() {
		return "Selected index " + selection().get();
	}
}
