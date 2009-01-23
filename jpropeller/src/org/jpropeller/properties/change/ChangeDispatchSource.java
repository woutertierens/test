package org.jpropeller.properties.change;

import java.util.List;
import java.util.Map;

/**
 * A source from which events are dispatched
 * @author shingoki
 *
 */
public interface ChangeDispatchSource {

	/**
	 * Must be called by a {@link ChangeDispatcher} before dispatching
	 * a set of changes. Must call {@link #concludeDispatch()} when this is finished - 
	 * preferably using a try...finally block as for locks. 
	 */
	public void prepareDispatch();
	
	/**
	 * Must be called by a {@link ChangeDispatcher} after dispatching
	 * a set of changes. Must have previously called {@link #prepareDispatch()} before starting dispatch - 
	 * preferably using a try...finally block as for locks.
	 */
	public void concludeDispatch();

	/**
	 * List of {@link Changeable}s that started changes, in order
	 * This should be called only after {@link #prepareDispatch()}, to get
	 * the "initial" to dispatch
	 * @return
	 * 		{@link Changeable}s list 
	 */
	public List<Changeable> initial();
	
	/**
	 * Map from each changed {@link Changeable} to the {@link Change} it has
	 * made
	 * This should be called only after {@link #prepareDispatch()}, to get
	 * the "changes" to dispatch
	 * @return
	 * 		Map of changes 
	 */
	public Map<Changeable, Change> changes();
	
}
