package org.jpropeller.collection;

import java.util.List;

import org.jpropeller.bean.Bean;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ListChange;

/**
 * An {@link ObservableList} is a {@link Bean} that is also a {@link List}.
 * It is an unusual (but perfectly valid) {@link Bean} in that it has
 * no props. Instead its state is just the contents of the {@link List}. As such,
 * when any {@link List} method is used to change those contents, the {@link ObservableList}
 * will propagate a {@link Change}. This {@link Change} is specifically a {@link ListChange}
 * giving details of the changes that have occurred. A {@link ListChange} is also
 * propagated when a {@link Bean} contained in the {@link List} has a {@link Change}.
 * The {@link ListChange} must have {@link ListChange#sameInstances()}
 * set to return false, indicating the list itself has changed.
 * The {@link ListDelta} of the {@link ListChange} MUST be at least "as 
 * large" as the actual change - that is, if some elements of the list have
 * changed, the {@link ListDelta} must be at least an ALTERATION change covering all
 * changed elements (and possibly some non-changed elements - overstating
 * the change is allowable). For some changes, a COMPLETE list change will have
 * to be used, for example if multiple non-contiguous elements are 
 * inserted/deleted/changed
 * 
 * Whenever a DEEP change occurs in the list, a {@link ListChange} must be started, 
 * with {@link ListChange#sameInstances()} returning true,
 * with a valid {@link ListDelta} - this should always be either an ALTERATION change
 * covering the indices of the list elements that have changed in a deep way,
 * or a COMPLETE change if a more specific range cannot be identified.
 * 
 * Note that DEEP changes are only noticed for list elements implementing {@link Bean}
 * 
 * @author shingoki
 *
 * @param <E>
 * 		The type of element in the list
 * 
 */
public interface ObservableList<E> extends Bean, List<E> {

	/**
	 * Clear all list values, then replace them with the new contents,
	 * in the order returned by the iterable. This is done as an "atomic"
	 * change, so only one large change will occur
	 * @param newContents
	 * 		The new contents
	 */
	public void replace(Iterable<E> newContents);
}