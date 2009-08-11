package org.jpropeller.view.combo;

import org.jpropeller.collection.CList;
import org.jpropeller.properties.Prop;
import org.jpropeller.reference.Reference;

/**
 * A {@link Reference} that also has a {@link Prop} for
 * the single selected item in a combobox-type display
 * @param <T>		The type of element in the list, and the 
 * 					type of the selection 
 */
public interface ListComboBoxReference<T> extends Reference<CList<T>>{

	/**
	 * The selection from the combo box, or null
	 * Note that this is not necessarily actually present
	 * in the referenced {@link CList} - combo boxes
	 * may allow selections outside the list.
	 * @return	Selection
	 */
	public Prop<T> selection();
	
}
