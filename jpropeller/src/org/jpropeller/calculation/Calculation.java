package org.jpropeller.calculation;

import java.util.Set;

import org.jpropeller.bean.Bean;
import org.jpropeller.collection.CList;
import org.jpropeller.collection.impl.IdentityHashSet;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.calculated.impl.CalculatedProp;
import org.jpropeller.properties.change.Changeable;

/**
 * A calculation based on a set of {@link Changeable}s
 * <p/>
 * NOTE: {@link Calculation}s MUST NOT modify
 * any {@link Changeable} state during the calculation.
 * It is acceptable (and often inevitable) to produce
 * completely new {@link Changeable} state, but this
 * state must not be modified. This is because {@link Calculation}s
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
 * since ideally a {@link Calculation} should only produce
 * new immutable data, which obviously avoids calling any setters
 * by definition.
 * <p/> 
 * NOTE: {@link Calculation}s SHOULD produce
 * values that are immutable (in the sense that
 * they never change state in any detectable way after
 * creation). This is because the results of calculations
 * are considered to be transient - they can always be 
 * recreated by recalculating based on the same inputs.
 * If a mutable result is produced, and it is mutated,
 * then any state in it will be lost whenever the
 * calculation is repeated. In addition, code using
 * the results of {@link Calculation} will not expect
 * changes to be made, and so may not listen to results
 * even if they are {@link Changeable}.
 * 
 * @param <T>
 * 		The type of value calculated
 */
public interface Calculation<T> {

	/**
	 * Get the set of properties used in the calculation
	 * 
	 * This must not change after the first time it is
	 * called - {@link Calculation}s may not change their sources.
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
	 * Perform the calculation
	 * @return
	 * 		The calculation result
	 */
	public T calculate();
	
}
