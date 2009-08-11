package org.jpropeller.collection;

import java.util.Set;

import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.SetChange;

/**
 * An {@link CSet} is a {@link Set} that implements {@link Changeable} to
 * allow tracking of changes to the {@link Set} and its contents.
 * When any {@link Set} method is used to change the {@link Set} contents, 
 * the {@link CSet} will propagate a {@link Change}. 
 * This {@link Change} is specifically a {@link SetChange}
 * giving details of the changes that have occurred. A {@link SetChange} is also
 * propagated when a {@link Changeable} contained in the {@link Set} has a {@link Change}.
 * The {@link SetDelta} of the {@link SetChange} MUST be at least "as 
 * large" as the actual change - that is, if some elements of the {@link Set} have
 * changed, the {@link SetDelta} must be at least an ALTERATION change covering all
 * changed elements (and possibly some non-changed elements - overstating
 * the change is allowable). For some changes, a COMPLETE set change will have
 * to be used, for example if multiple elements are 
 * inserted/deleted/changed
 * 
 * Whenever a DEEP change occurs in the set, a {@link SetChange} must be started, 
 * with {@link SetChange#sameInstances()} returning true,
 * with a valid {@link ListDelta} - this should always be either an ALTERATION change
 * covering the elements that have changed in a deep way,
 * or a COMPLETE change if a more specific range cannot be identified.
 * 
 * Note that DEEP changes are only noticed for set elements implementing {@link Changeable}
 * 
 * @param <E>
 * 		The type of element in the {@link Set}
 * 
 */
public interface CSet<E> extends Changeable, Set<E>, CCollection<E> {
	
}