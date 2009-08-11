package org.jpropeller.task;

import java.util.Set;

import org.jpropeller.collection.CList;
import org.jpropeller.collection.impl.IdentityHashSet;
import org.jpropeller.properties.change.ChangeSystem;
import org.jpropeller.properties.change.Changeable;

/**
 * <p>
 * A task triggered by changes in a set of {@link Changeable}s,
 * which executes based on information from just those
 * {@link Changeable}s.
 * </p>
 * 
 * <p>
 * The task must assume it will be run in one thread
 * at a time, but possibly in different threads from run
 * to run, where the thread for each run is suitable
 * for making changes to {@link Changeable} state.
 * </p>
 * 
 * <p>
 * It may NOT assume that it will be run after every single
 * change to the {@link Changeable}s, and of course unless
 * prevented by use of {@link ChangeSystem#acquire()}, changes
 * may occur in {@link Changeable}s while the task is executing -
 * it should manage for itself the process of acquiring the
 * lock as necessary, and checking for changes when the lock
 * is not held.
 * </p>
 * 
 * <p>
 * However, for every change that occurs in the source {@link Changeable}s,
 * the task WILL be run at some later point in time. That is, multiple
 * changes may be handled by a single run of the task, but it is
 * guaranteed that if enough time passes after any given change,
 * the task will be run.
 * </p>
 * 
 * <p>
 * Tasks are not informed of the changes that have occured,
 * and must actually query the state to check this. However they
 * can obviously assume that at least one of the {@link Changeable}s
 * they depend on has changed.
 * </p>
 * 
 * <p>
 * In most cases, the simplest way to do this is to:
 * <ol>
 * <li>
 * Be run (obviously the first stage is that the task
 * will be run at some point after some of its source
 * Changeables have changed, possibly with multiple changes
 * having occurred)
 * </li>
 * 
 * <li>
 * Acquire lock, <b>quickly</b> make all necessary changes
 * </li>
 * 
 * <li>
 * Release lock, terminate
 * </li>
 * </ol>
 * </p>
 * 
 * <p>
 * Where calculations need to be made that take a longer time,
 * the process should generally be:
 * <ol>
 * <li>
 * Be run (obviously the first stage is that the task
 * will be run at some point after some of its source
 * Changeables have changed, possibly with multiple changes
 * having occurred)
 * </li>
 * 
 * <li>
 * Acquire lock, <b>quickly</b> copy the necessary state - this
 * gives a consistent, instantaneous set of state for calculations.
 * </li>
 * 
 * <li>
 * Release lock, perform calculations - which may take as long as 
 * necessary
 * </li>
 * 
 * <li>
 * Acquire lock, <b>quickly</b> check that the state is still 
 * consistent so that calculations are still valid, then make
 * necessary changes. If calculations are NOT valid, then simply
 * don't make the changes - we can expect to be called again
 * as a result of the new changes that made the previous calculation
 * invalid.
 * </li>
 * 
 * <li>
 * Release lock, terminate
 * </li>
 * </ol>
 * </p>
 */
public interface Task extends Runnable {

	/**
	 * Get the set of properties used in the task
	 * 
	 * This must not change after the first time it is
	 * called - {@link Task}s may not change their sources.
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
	 * Run the task.
	 * 
	 * Method must only use state from a {@link Changeable} in
	 * the set returned by {@link #getSources()}
	 * 
	 * This may be called from different threads, but never from
	 * more than one thread at a time, and always from a thread
	 * that may change {@link Changeable} state and may try
	 * to call {@link ChangeSystem#acquire()}
	 * 
	 * See {@link Task} class docs for (much) more information.
	 */
	public void run();
}
