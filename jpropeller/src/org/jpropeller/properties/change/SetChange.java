package org.jpropeller.properties.change;

import java.util.List;
import java.util.Set;

import org.jpropeller.collection.SetDelta;

/**
 * A {@link Change} applying to a {@link Set}, and hence
 * having a {@link SetDelta}.
 * Must return {@link ChangeType#SET} from {@link #type()}
 */
public interface SetChange extends Change {

	/**
	 * Get a list of deltas showing the changes to the set.
	 * The list is in the order deltas were made - first element
	 * occurred first, etc.
	 * @return
	 * 		The {@link SetDelta}s
	 */
	public List<SetDelta> getSetDeltas();
	
}
