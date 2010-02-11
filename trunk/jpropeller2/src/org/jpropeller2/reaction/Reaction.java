package org.jpropeller2.reaction;

import java.util.Map;

import org.jpropeller2.box.Box;
import org.jpropeller2.change.Change;
import org.jpropeller2.universe.Universe;

/**
 * A {@link Reaction} reacts to changes in any of
 * a set of input {@link Box}es. 
 * 
 * When reacting, a {@link Reaction} may do anything
 * it wants outside the {@link Universe}, but inside
 * the {@link Universe} it may react by declaring it
 * will change one or more output {@link Box}es (which may
 * be the same or different from input {@link Box}es).
 * It may also declare it intends to change an 
 * unknown set of output {@link Box}es, where it cannot predict
 * in advance which ones.
 * 
 * Later (but before any reading of any of the 
 * output {@link Box}es) the {@link Reaction}
 * will be asked to actually apply the changes it declared.
 * When doing this, it may read any of its input {@link Box}es,
 * and write to any of the declared output {@link Box}es, but
 * must not read or write any other {@link Box}es.
 * 
 * A {@link Reaction}'s input {@link Box}es must be
 * immutable (the set of {@link Box}es does NOT change
 * after construction).
 * 
 * A {@link Reaction}'s output {@link Box}es can be different
 * each time a {@link Reaction} is notified of a change.
 *
 * Implementations of {@link Reaction} will be most efficient when
 * they declare known output {@link Box}es, since they can then
 * be applied lazily. When output {@link Box}es are unknown,
 * the {@link Reaction} will be applied before ANY {@link Box}
 * in the {@link Universe} is read, and this is less efficient.
 * 
 * A {@link Reaction} may hold state, but only to allow it to apply
 * itself more efficiently - it MUST only set output {@link Box}es
 * based on the CURRENT value of input {@link Box}es. Such state
 * should be carefully handled to prevent any side effects, use
 * of stale data, etc. Nearly all {@link Reaction}s should only have
 * immutable state, but some complex provided implementations (e.g.
 * for paths) have internal state used to filter changes and reduce
 * the number of times the {@link Reaction} is applied.
 */
public interface Reaction {

	//TODO react (give back a map of Box to Change we intend to apply) and apply (actually do the changes - need to
	//distinguish between applying with and without propagation, for known versus unknown output boxes).
	
	/**
	 * React to {@link Change}s by declaring what {@link Change}s the {@link Reaction} will make when applied.
	 */
	public Map<Box, Change> react(Map<Box, Change> changes);
	
}
