package org.jpropeller.constraint;

import java.util.Set;

import org.jpropeller.bean.Bean;
import org.jpropeller.collection.CList;
import org.jpropeller.collection.impl.IdentityHashSet;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.calculated.impl.CalculatedProp;
import org.jpropeller.properties.change.ChangeSystem;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.constrained.impl.ConstrainedProp;

/**
 * A constraint based on a set of {@link Changeable}s
 * 
 * This assists in the management of values that can be
 * set, but must be constrained in a complex way depending
 * on other {@link Changeable} values.
 * 
 * Such values may be set, but may not accept a new value,
 * or may modify that value when read.
 * 
 *  As an example, consider an Integer property X that is settable, 
 *  but must always be less than another Integer property Y. 
 *  When we set X to 5, it will store the number 5, but not 
 *  recalculate or reject the value. The value 5 is only 
 *  checked against Y when the value of X is read. Equally, 
 *  if Y changes, X will propagate a change, but is not 
 *  recalculated until it is read - at this point, it can 
 *  consider its previous value AND the value of Y, and 
 *  adjust as appropriate - for example by clipping the value.
 *  
 *  This system will work for any single separate change to 
 *  the Changeable System, the only possible problem is 
 *  when a system is being changed from one valid state 
 *  to another valid state, via invalid states. For 
 *  example, X may be 5 and Y 10 - this is valid. 
 *  But suppose that when created, X defaults to 
 *  0 and Y to 0. If we attempt to change X first, 
 *  to 5, then read it, it will modify itself back 
 *  to 0, since it may not be more than Y, which 
 *  is still 0. Then we change Y to 10, and end up in 
 *  an incorrect state where X is 0 and Y is 10. If we 
 *  perform the change in the other order, by setting 
 *  Y to 10 then X to 5, we will see the correct state.
 *  
 *  To ensure the correct results, users of {@link Constraint}s,
 *  for example via {@link ConstrainedProp}, should lock the
 *  change system via {@link ChangeSystem#acquire()} before
 *  making all changes requried to achieve another consistent
 *  state, then release the lock via {@link ChangeSystem#release()}.
 *  This will make sure that all {@link Constraint}s are then
 *  evaluated with the whole new consistent state, the next time
 *  state is read.
 *  
 *  This is the way the persistence and undo systems work, 
 *  for example. It is also in general the most efficient way
 *  to make multiple associated changes, and has other benefits.
 *   
 * <p/>
 * NOTE: {@link Constraint}s MUST NOT modify
 * any {@link Changeable} state during the calculation.
 * It is acceptable (and often inevitable) to produce
 * completely new {@link Changeable} state, but this
 * state must not be modified. This is because {@link Constraint}s
 * may be triggered as {@link Changeable} state is being
 * read (for example via {@link CalculatedProp}), and
 * it is illegal to change {@link Changeable} state as
 * it is read. So for example, it is acceptable to call
 * {@link Prop} constructors, for example via {@link Bean}
 * constructors, but not to then call {@link Prop#set(Object)}
 * on those {@link Prop}s. This is safe since it ensures
 * that no {@link Changeable} state that anything could
 * possibly be listening to will change. The new {@link Changeable}
 * state can only be accessed via the reading that is about to be
 * performed. This also makes sense in terms of the next note,
 * since ideally a {@link Constraint} should only produce
 * new immutable data, which obviously avoids calling any setters
 * by definition.
 * <p/> 
 * 
 * @param <T>
 * 		The type of value calculated
 */
public interface Constraint<T> {

	/**
	 * Get the set of properties used in the constraint
	 * 
	 * This must not change after the first time it is
	 * called - {@link Constraint}s may not change their sources.
	 * 
	 * Note that care should be taken to use the
	 * correct set implementation - for example it is probably best
	 * to use {@link IdentityHashSet} to store the sources
	 * by reference - otherwise two different but equal
	 * sources (e.g. two empty {@link CList} instances)
	 * can be added to the set and one of them ignored.
	 * @return
	 * 		The set of properties used
	 */
	public Set<? extends Changeable> getSources();
	
	/**
	 * Perform the constraint
	 * @param	setValue 	The most recently set value - the value requested
	 * 						of the constraint
	 * @return	The constrained result, based on setValue and
	 * 			the state of {@link Changeable}s in {@link #getSources()}
	 */
	public T constrain(T setValue);
	
}
