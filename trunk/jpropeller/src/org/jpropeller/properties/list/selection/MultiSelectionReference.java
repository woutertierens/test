package org.jpropeller.properties.list.selection;

import org.jpropeller.collection.CCollection;
import org.jpropeller.collection.CList;
import org.jpropeller.properties.Prop;
import org.jpropeller.reference.Reference;

/**
 * A {@link Reference} to an {@link CList}, which also has an additional
 * selection property, giving the selected indices
 * @param <T>
 * 		The type of element in the list 
 */
public interface MultiSelectionReference<T> extends Reference<CList<T>>{

	/**
	 * A collection of the selected indices in the list 
	 * @return The selected indices
	 */
	public Prop<? extends CCollection<Integer>> selection();

}