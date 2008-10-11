package org.jpropeller.collection;

import java.util.List;

import org.jpropeller.bean.Bean;
import org.jpropeller.map.PropMap;
import org.jpropeller.properties.UneditableProp;

/**
 * A ListBean is a Bean with a single property, an UneditableProp<Long>
 * called "modifications", which must be accessible in the Bean {@link PropMap},
 * and also via a method "modifications()"
 * 
 * The modifications property must return the number of modifications via
 * calling of List methods or by internal changes, since the List was created.
 * 
 * Whenever the contents of the List are directly changed, either internally or via
 * List methods, the modifications property must be incremented, and then 
 * a shallow ListPropEvent fired showing the change, and also carrying a ListChange
 * describing the change to the list. This ListChange MUST be at least "as 
 * large" as the actual change - that is, if some elements of the list have
 * changed, the ListChange must be at least an ALTERATION change covering all
 * changed elements (and possibly some non-changed elements - overstating
 * the change is allowable). For some changes, a COMPLETE list change will have
 * to be used, for example if multiple non-contiguous elements are 
 * inserted/deleted/changed
 * 
 * Whenever a DEEP change occurs in the list, a DEEP ListPropEvent must be fired
 * with a valid ListChange - this should always be either an ALTERATION change
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
	 * A Property holding the number of modifications made to the list,
	 * this is also the number of PropEvents this Prop has fired
	 * @return
	 * 		modification count
	 */
	public UneditableProp<Long> modifications();

	/**
	 * Clear all list values, then replace them with the new contents,
	 * in the order returned by the iterable. This is done as an "atomic"
	 * change, so only one large change will occur
	 * @param newContents
	 * 		The new contents
	 */
	public void replace(Iterable<E> newContents);
}