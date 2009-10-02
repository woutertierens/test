package org.jpropeller.properties.change;

import java.util.concurrent.locks.Lock;

import org.jpropeller.task.Task;

/**
 * Manages the changes of a set of {@link Changeable} instances,
 * handling any synchronisation of changes, propagation of changes
 * through the {@link Changeable} system and then firing of changes
 * to any {@link ChangeListener}s
 */
public interface ChangeSystem {

	/**
	 * Commit a change, that was prepared for via
	 * {@link #prepareChange(Changeable)}, and start propagation
	 * Note that you must still also conclude the change with
	 * {@link #concludeChange(Changeable)}
	 * @param changed
	 * 		The {@link Changeable} that has changed
	 * @param change
	 * 		The {@link Change} it has made
	 */
	public void propagateChange(Changeable changed, Change change);

	/**
	 * Conclude a change that was prepared for using {@link #prepareChange(Changeable)},
	 * and possibly propagated via {@link #propagateChange(Changeable, Change)}
	 * @param changed
	 * 		The changeable that prepared the change 
	 */
	public void concludeChange(Changeable changed);
	
	/**
	 * Prepare to make a change. This must be called before
	 * any {@link Changeable} changes any of its state visible
	 * via normal means.
	 * 
	 * Next the {@link Changeable} should make any state changes it
	 * plans to make. If any changes are
	 * made, then {@link #propagateChange(Changeable, Change)} must be 
	 * called to propagate them.
	 * 
	 * Finally, {@link #concludeChange(Changeable)} must be called to
	 * mark the change as finished.
	 * 
	 * @param changed
	 * 		The changeable preparing the change
	 */
	public void prepareChange(Changeable changed);

	/**
	 * Must be called by a {@link Changeable} before it reads the state
	 * of itself or any other {@link Changeable}. This should be the
	 * first code in any "get" methods of a {@link Changeable}.
	 * {@link #concludeRead(Changeable)} should be called in a "finally"
	 * clause for the getter. 
	 * @param changeable
	 * 		The changeable to be read
	 */
	public void prepareRead(Changeable changeable);

	/**
	 * Must be called as the final code in any "get" method of a
	 * {@link Changeable}, in a "finally" clause
	 * @param changeable
	 */
	public void concludeRead(Changeable changeable);

	/**
	 * Must be called by a {@link Changeable} before it changes its list
	 * of {@link Changeable} listeners OR {@link ChangeListener}s.
	 * This should be the first code in any {@link ChangeableFeatures#addChangeableListener(Changeable)},
	 * {@link ChangeableFeatures#removeChangeableListener(Changeable)},
	 * {@link ChangeableFeatures#addListener(ChangeListener)} or
	 * {@link ChangeableFeatures#removeListener(ChangeListener)} methods.
	 * {@link #concludeListenerChange(Changeable)} should be called in a "finally"
	 * clause for the methods. 
	 * @param changeable
	 * 		The changeable to have listeners changed
	 */
	public void prepareListenerChange(Changeable changeable);

	/**
	 * Must be called as the final code in any method of a
	 * {@link ChangeableFeatures} that modifies the listener lists, 
	 * in a "finally" clause
	 * @param changeable
	 * 		The changeable to have listeners changed
	 */
	public void concludeListenerChange(Changeable changeable);

	/**
	 * Acquire global lock on change system, to perform atomic changes.
	 * While this lock is held (before {@link #release()} is called),
	 * only the thread that called {@link #acquire()} can get or set
	 * the state of {@link Changeable}s. Note that changes will also
	 * not be dispatched while this lock is held - they will be coalesced
	 * with any existing changes, and then dispatched when {@link #release()}
	 * is called, OR possibly later.
	 * <br/>
	 * <br/>
	 * This lock MUST be used responsibly, to perform deterministic, rapid
	 * changes which do not depend on other threads, etc. The ideal use
	 * of the lock is to be held while state is read to a "buffer" or written
	 * from one, and then released immediately afterwards. Used in this way
	 * the lock is a safe and effective way of achieving "atomic" changes to
	 * the entire set of {@link Changeable} instances, to avoid making an
	 * inconsistent state visible (and to avoid listeners being notified of it
	 * during the change).
	 * <br/>
	 * <br/>
	 * It is recommended that a structure as follows is used, in the same way
	 * as for a {@link Lock}:
	 * <br/>
	 * <br/>
	 * <pre>
	 *changeSystem.acquire();
	 *try {
	 *	//Read and write Changeables here
	 *} finally {
	 *	changeSystem.release();
	 *}
	 * </pre>
	 * <br />
	 * Abusing this lock may very well lead to deadlock of the change system - 
	 * use it only if you need it and understand the requirements - very often
	 * the built-in synchronisation of {@link Changeable}s is all you require, since
	 * individual changes to {@link Changeable}s are already thread safe - this
	 * lock is required ONLY for atomic execution of multiple changes.
	 */
	public void acquire();
	
	/**
	 * Release global lock on change system - see notes for {@link #acquire()}
	 */
	public void release();
	
	/**
	 * Add a {@link ChangeSystemListener}, to be notified of events in this
	 * {@link ChangeSystem}.
	 * 
	 * Please see {@link ChangeSystemListener} for a detailed explanation of
	 * the uses of this interface - it is for specialised use only, and is NOT
	 * the generally recommended way of listening for changes - instead, use
	 * a {@link ChangeListener}
	 * 
	 * @param listener
	 * 		The listener to be added
	 */
	public void addChangeSystemListener(ChangeSystemListener listener);
	
	/**
	 * Remove a {@link ChangeSystemListener}, no longer to be notified of events in this
	 * {@link ChangeSystem}.
	 * 
	 * Please see {@link ChangeSystemListener} for a detailed explanation of
	 * the uses of this interface - it is for specialised use only, and is NOT
	 * the generally recommended way of listening for changes - instead, use
	 * a {@link ChangeListener}
	 * 
	 * @param listener
	 * 		The listener to be removed
	 */
	public void removeChangeSystemListener(ChangeSystemListener listener);

	/**
	 * Add a task to be run before next dispatch to {@link ChangeListener}s
	 * Note that any given task will be run only once per dispatch, even if
	 * added multiple times before that dispatch
	 * @param task		The {@link Task} to execute
	 */
	public void addTask(Task task);
	
}