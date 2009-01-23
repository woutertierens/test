package org.jpropeller.properties.change;

import java.util.List;

import org.jpropeller.collection.ListDelta;

/**
 * A {@link Change} applying to a {@link List}, and hence
 * having a {@link ListDelta}.
 * Must return {@link ChangeType#LIST} from {@link #type()}
 */
public interface ListChange extends Change {

	/**
	 * Get a list of deltas showing the changes to the list - 
	 * which element(s) have changed and how
	 * The list is in the order deltas were made - first element
	 * occurred first, etc.
	 * @return
	 * 		The {@link ListDelta}
	 */
	public List<ListDelta> getListDeltas();
	
}
