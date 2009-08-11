package org.jpropeller.collection;

import java.util.Collection;
import java.util.Set;

import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.ListChange;

/**
 * A {@link CCollection} is a {@link Collection} that also implements
 * {@link Changeable} to allow tracking of changes to the {@link Collection}
 * and its contents.
 *
 * <p/>
 * When any {@link Collection} method is used to change the contents, the 
 * {@link CCollection} will propagate a {@link Change}. 
 * This {@link Change} is often a specific type of {@link Change} 
 * giving details of the scope and type of change that has occurred,
 * in terms of the {@link Collection} interface(s).
 * A {@link ListChange} is also propagated when a {@link Changeable} 
 * contained in the {@link Collection} has a {@link Change}.
 * The {@link Change} must have {@link Change#sameInstances()}
 * set to return false, indicating the list itself has changed.
 * <p/> 
 * Whenever a DEEP change occurs in the {@link Collection} contents, 
 * a {@link Change} must be started, 
 * with {@link Change#sameInstances()} returning true.
 * <p/>
 * Note that DEEP changes are only noticed for {@link Collection} 
 * contents implementing  {@link Changeable}
 * <p/>
 * See {@link CList}, {@link CSet}
 * 
 * @param <E>		The type of element in the {@link Collection}
 */
public interface CCollection<E> extends Changeable, Collection<E> {
	
	/**
	 * Clear all {@link Set} values, then replace them with the new contents,
	 * in the order returned by the iterable. This is done as an "atomic"
	 * change, so only one large change will occur
	 * @param newContents
	 * 		The new contents
	 */
	public void replace(Iterable<E> newContents);
}
