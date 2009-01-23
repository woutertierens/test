package org.jpropeller.properties.change;

import java.util.List;
import java.util.Map;

import org.jpropeller.properties.calculated.impl.CalculatedProp;

/**
 * A listener to be notified of sets of changes that have occurred to
 * one or more {@link Changeable} instances. This notification is always
 * made AFTER all changes have been propagated, so that the bean layer
 * is in a consistent state.
 */
public interface ChangeListener {

	/**
	 * Called when a {@link Changeable} we are listening to changes.
	 * Note that this method may be called only once, even when we are
	 * listening to multiple {@link Changeable} instances, more than one
	 * of which may have changed. This is because the system may choose to
	 * make only one {@link #change(List, Map)} invocation per 
	 * complete propagation of events.
	 * <br />
	 * Further, it may be possible for the system to call {@link #change(List, Map)}
	 * once to notify the {@link ChangeListener} of multiple changes - in this case
	 * the list of initial {@link Changeable}s contains an entry for each initial
	 * change, and the changes map contains the effects of ALL changes.
	 * <br />
	 * By checking the changes map, it can be seen which {@link Changeable}
	 * instances have actually changed (the ones in the keyset of changes),
	 * and what {@link Change}s they made.
	 * <br />
	 * First VERY important consideration - the changes map is only valid during
	 * this method - it may be modified later, and so if it needs to be retained, it
	 * should be copied. This is because the changes map may well be updated in
	 * future to reflect a new set of changes.
	 * <br />
	 * Second VERY important consideration - it is illegal to attempt to change
	 * the state of any {@link Changeable} from within this {@link #change(List, Map)} 
	 * method. Any changes to {@link Changeable}s that are required to occur automatically
	 * when other changes occur should be implemented within the {@link Changeable} model
	 * itself - for example using {@link CalculatedProp}s, or similar.
	 * {@link ChangeListener}s are expected to respond to the changes only. Any changes they
	 * make to {@link Changeable}s must be made OUTSIDE this {@link #change(List, Map)} method,
	 * for example in response to user input, etc.
	 * <br />
	 * This serves several purposes - firstly it prevents cycles of alterations where a 
	 * change causes a response, which causes another change, etc. These cases are handled much
	 * better inside the {@link Changeable} model than in {@link ChangeListener}s. Secondly,
	 * it ensures that each {@link ChangeListener} sees the same consistent state of the
	 * {@link Changeable}s, with only the notified changes made. If one {@link ChangeListener} is
	 * allowed to change the state of {@link Changeable}s, then what changes should the next
	 * {@link ChangeListener} see?
	 * <br />
	 * Third VERY important consideration - while {@link ChangeListener}s are being notified
	 * of changes (during "change dispatch"), only the thread that is used to call the
	 * {@link #change(List, Map)} methods of {@link ChangeListener}s has access to do anything
	 * with {@link Changeable}s - this includes getting their state (reading), setting state (writing)
	 * and changing listeners. This is only a problem if a {@link ChangeListener} tries to wait for
	 * another thread that is in turn waiting to access a {@link Changeable}. This is an unusual thing
	 * to do, and must NOT be done, since it can result in deadlocks. In the default configuration,
	 * the Swing/EDT thread will be used for change dispatch, and so Swing views and editors can
	 * simply be written in a "naive" way, assuming they will only ever be used from the Swing thread,
	 * and not using other threads to access {@link Changeable}s.
	 * @param initial
	 * 		The map represents the results of propagating one or more initial
	 * changes. This list contains the changeables that had these initial changes,
	 * in the order the changes occurred.
	 * @param changes
	 * 		The changes, as a map from each changed {@link Changeable} to a 
	 * {@link Change} that encompasses the change that occurred to it. Note
	 * that each {@link Change} may be an OVERestimate - it may indicate that
	 * more may have changed than actually has, but will not be an UNDERestimate.
	 */
	public void change(List<Changeable> initial, Map<Changeable, Change> changes);
	
}
