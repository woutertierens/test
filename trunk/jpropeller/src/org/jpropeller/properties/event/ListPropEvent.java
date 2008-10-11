package org.jpropeller.properties.event;

import org.jpropeller.collection.ListChange;
import org.jpropeller.info.PropAccessType;

/**
 * A PropEvent for a prop change in a ListBean
 * 
 * Implementations must return {@link PropAccessType#LIST} from {@link #getType()}
 * 
 * @author shingoki
 *
 * @param <T>
 * 		The type of Prop that has changed
 */
public interface ListPropEvent<T> extends PropEvent<T> {

	/**
	 * Get a list change describing the scope of the changes to the list
	 * @return
	 * 		The ListChange
	 */
	public ListChange getListChange();
	
}
