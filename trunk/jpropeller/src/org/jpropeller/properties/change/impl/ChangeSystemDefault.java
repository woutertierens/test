package org.jpropeller.properties.change.impl;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jpropeller.collection.impl.IdentityHashSet;
import org.jpropeller.concurrency.Responder;
import org.jpropeller.concurrency.impl.CoalescingResponder;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeDispatchSource;
import org.jpropeller.properties.change.ChangeDispatcher;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.ChangeSystem;
import org.jpropeller.properties.change.ChangeSystemListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.task.Task;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.util.Listeners;

/**
 * Default implementation of {@link ChangeSystem}
 * Uses a single lock to synchronise all reading, writing of
 * {@link Changeable} state, and all addition/removal of listeners.
 * Also enforces rule that {@link ChangeListener}s must NOT change
 * {@link Changeable} state from within their {@link ChangeListener#change(List, Map)}
 * methods (since this can cause cycles, as well as problems with locking behaviour etc.)
 */
public class ChangeSystemDefault implements ChangeSystem, ChangeDispatchSource {

	private final static Logger logger = GeneralUtils.logger(ChangeSystemDefault.class);
	
	private Listeners<ChangeSystemListener> changeSystemListeners = new Listeners<ChangeSystemListener>();
	
	/**
	 * List of {@link Changeable}s that started changes, in order
	 */
	private List<Changeable> initial = new LinkedList<Changeable>();
	
	/**
	 * This stores the changes made during the CURRENT propagation process, while that
	 * propagation is being performed. Since this map is cleared after the end of
	 * each propagation event, and is used to work out when propagation is complete,
	 * it ensures that even when we coalesce the changes caused by multiple propagations,
	 * we still fully propagate each one.
	 * To give an example, say we have a {@link Changeable} X, and a {@link Changeable} C
	 * that is calculated based on X. Now we do the following:
	 * 
	 * 1. Change X
	 * 2. Read C
	 * 3. Change X again
	 * 4. Read C
	 * 
	 * If these actions are all done quickly, then changes will be coalesced - the changes
	 * from step 2 and step 4 will be added together in the allChanges map. This is required
	 * since we want to dispatch at some stage after 4, passing all changes that have happened
	 * up to that point.
	 * 
	 * Note how C works - when stage 2 occurs, we will cause C to recalculate itself (it will have noticed
	 * the change in X at stage 1, invalidating its state, so 2 causes recalculation). At this point,
	 * C now has a valid cache. 
	 * 
	 * Now consider what can go wrong. When stage 3 occurs - if we were to look at ONLY the allChanges map,
	 * we would now see that we already had a change to X, so we don't bother to propagate the new
	 * change. Hence C never sees the new change, and does not invalidate its cache again at stage 3.
	 * Now when we read C at stage 4, it gives us the old, out of date value it calculated at stage 2,
	 * NOT the new value from the value of X set in stage 3.
	 * 
	 * To avoid this, we start with a fresh, empty currentChanges map on every propagation. Then in
	 * stage 3, this map does NOT have a change for X, and when we get the new change to X, we propagate
	 * it fully. As we do this, the new changes that are part of the propagation are built up in the
	 * currentChanges map. When the propagation is complete, these new changes are then used to extend
	 * the coalesced changes in allChanges. The contract for {@link Change} specifies that change extension
	 * must be associative, so it is safe to combine the changes in the "wrong" operation order (obviously NOT
	 * in the wrong order of operands, since change extension is NOT required to be commutative).
	 * 
	 * We use an {@link IdentityHashMap} so that we show the change to each INSTANCE,
	 * we don't care about equality, we need identicality
	 */
	private Map<Changeable, Change> currentChanges = 
		new IdentityHashMap<Changeable, Change>();
	
	/**
	 * This stores all changes made to each {@link Changeable} since the last successful
	 * dispatch. Where the changes from multiple propagations (of multiple initial changes)
	 * are coalesced, the merging of changes is performed into this map, from currentChanges,
	 * at the end of propagation. See the docs for the currentChanges map for a more detailed
	 * explanation of this process, and the reason it is necessary. 
	 * 
	 * We use an {@link IdentityHashMap} so that we show the change to each INSTANCE,
	 * we don't care about equality, we need identicality
	 */
	private Map<Changeable, Change> allChanges =
		new IdentityHashMap<Changeable, Change>();

	//Make unmodifiable views of lists and maps to pass out (to ChangeDispatcher). We don't
	//want to let the dispatcher or the listeners themselves modify anything. Change instances
	//are already immutable.
	private List<Changeable> umInitial;
	private Map<Changeable, Change> umAllChanges;

	private IdentityHashSet<Task> pendingTasks = new IdentityHashSet<Task>();
	
	/**
	 * User to actually call change method on {@link ChangeListener}s
	 */
	private ChangeDispatcher dispatcher;
	
	/**
	 * This lock is always held during dispatching.
	 * This is used to detect when a thread is dispatching, and
	 * calls prepareChange. This is the same as detecting when a {@link ChangeListener} responds
	 * to a {@link ChangeListener#change(List, Map)} by trying (possibly indirectly) to
	 * change a {@link Changeable} - which is prohibited.
	 */
	private final ReentrantLock dispatchingLock = new ReentrantLock();

	/**
	 * This lock is required to read, write, change listeners on any {@link Changeable},
	 * and to dispatch changes, or obviously to prevent other threads from doing these things. 
	 */
	public final ReentrantLock mainLock = new ReentrantLock();
	
	/**
	 * Update by dispatching
	 */
	private final Responder dispatchUpdater = new CoalescingResponder(new Runnable() {
		@Override
		public void run() {
			dispatch();
		}
	});
	
	/**
	 * Create a {@link ChangeSystemDefault} 
	 * @param dispatcher
	 * 		The {@link ChangeDispatcher} used to actually pass changes to {@link ChangeListener}s
	 */
	public ChangeSystemDefault(ChangeDispatcher dispatcher) {
		super();
		this.dispatcher = dispatcher;
		dispatcher.setSource(this);
		this.umInitial = Collections.unmodifiableList(initial);
		this.umAllChanges = Collections.unmodifiableMap(allChanges);
	}

	private void leaveMainLock() {
		
		//We run tasks only just before we completely
		//release the main lock. This means tasks
		//do NOT run until release() is called by external code,
		//allowing processes that lock for large changes
		//to be sure that tasks are not running during the 
		//changes. The lock IS still held during task execution
		//though, so that any other threads will see the complete
		//change after tasks have run.
		//Note that we MUST release the lock, even if the pending tasks
		//produce an error (we also catch exceptions in runPendingTasks).
		try {
			if (mainLock.getHoldCount() == 1)
				runPendingTasks();
		} finally {
			mainLock.unlock();
		}
	}

	private void runPendingTasks() {
		boolean tasksClear = false;
		while(!tasksClear) {
			Task task = null;

			synchronized (pendingTasks) {
				if (pendingTasks.isEmpty()) {
					tasksClear = true;
				} else {
					Iterator<Task> iterator = pendingTasks.iterator();
					task = iterator.next();
					iterator.remove();
				}
			}
			
			if (task!=null) {
				//We cannot reasonably deal with task exceptions,
				//so we will print and log them, but then just carry
				//on to the next task
				try {
					task.respond(new AtomicBoolean(false));
				} catch (Exception e) {
					e.printStackTrace();
					logger.log(Level.SEVERE, "Exception executing synchronous task", e);
				}
			}
		}
	}

	@Override
	public void prepareDispatch() {
		
		//Get main lock
		mainLock.lock();
		
		//Only one dispatcher - this thread
		dispatchingLock.lock();
	}
	
	@Override
	public void addTask(Task task) {
		synchronized (pendingTasks) {
			pendingTasks.add(task);
		
			//We now need to check whether we should run the task(s)
			//immediately, or leave them to be run when the mainlock
			//is next unlocked
			
			//Try to get the mainLock - this will succeed
			//if the lock is not held at all, OR we already 
			//hold the lock in this thread
			boolean canHold = mainLock.tryLock(); 
			if (canHold) {
				try {
					//We got the lock - if we hold it once only then
					//it wasn't held before, and we need to run the tasks
					//immediately.
					//Otherwise, the lock was already held by this thread, and
					//the task will be run when we release it again
					if (mainLock.getHoldCount() == 1) {
						runPendingTasks();
					}
				} finally {
					mainLock.unlock();
				}
			} else {
				//If some other thread has the lock, then we don't need to do anything,
				//since when the lock is released the task will be run
			}
		}
	}
	
	@Override
	public void concludeDispatch() {
		try {
			//Clear the changes we have just dispatched
			initial.clear();
			allChanges.clear();
		} finally {		
			//Release locks in reverse order
			dispatchingLock.unlock();
			leaveMainLock();
		}
	}
	
	@Override
	public void prepareChange(Changeable changed) {
		
		//Only the thread that is currently dispatching (if any) can hold the
		//dispatchingLock. Hence if we hold it here, it indicates that a we 
		//have dispatched a change to a ChangeListener, and it has responded
		//by attempting to make another change to a Changeable. This is illegal
		//since it can cause loops, out of order changes, etc.
		//NOTE we can check this without further locking since it
		//is only checking for THIS thread holding the lock, so no
		//other concurrent thread can affect this between us checking, and 
		//acquiring the mainLock
		if (dispatchingLock.isHeldByCurrentThread()) {
			throw new IllegalArgumentException("Must not attempt to change a Changeable in response to a change - this can cause cycles and is prohibited.");
		}

		//Acquire necessary lock
		mainLock.lock();
		
		//Notify listeners
		firePrepareChange(changed);
	}

	//This method and firePendingChanges() are both synchronized, since they manipulate the pending changes
	@Override
	public void propagateChange(Changeable changed, Change change) {

		//TODO use specific exception here
		//We must have the necessary lock here
		if (!mainLock.isHeldByCurrentThread()) {
			throw new IllegalMonitorStateException("Cannot propagateChange without first calling prepareChange (i.e. lock is not held when calling propagateChange)");
		}
		
		//We expect an initial change - log and ignore if otherwise
		if (!change.initial()) {
			logger.severe("CONTRACT: Changeable " + changed + " attempted to startChange() with change " + change + ", which is not initial - this is against the contract for Changeable");
			return;
		}
		
		//TODO use specific exception here
		//Check that the current changes map is empty - should have 
		//been cleared at end of last propagation
		if (!currentChanges.isEmpty()) {
			logger.severe("IMPLEMENTATION: Non-empty current changes when propagation started - invalid state.");
			throw new IllegalArgumentException("Non-empty current changes when propagation started - invalid state.");
		}
		
		initial.add(changed);

		//Extend change to initial, using current changes map
		Change extendedInitialChange = extendChange(changed, change, currentChanges);

		//If the initial has actually got an extended change, start recursive processing from initial
		if (extendedInitialChange != null) { 
			processListeners(changed, change);
		}
		
		//Propagation is complete - we need to coalesce the currentChanges into allChanges
		currentChangesToAllChanges();
	}

	private void currentChangesToAllChanges() {
		
		for (Changeable key : currentChanges.keySet()) {
			Change currentChange = currentChanges.get(key);
			
			//Extend the change in the allChanges map
			extendChange(key, currentChange, allChanges);
		}
		
		//We have now coalesced the changes, can clear current changes map
		currentChanges.clear();
	}
	
	@Override
	public void concludeChange(Changeable changed) {
		
		//Release necessary lock
		leaveMainLock();
		
		//dispatch();
		//If we have left mainLock completely, it is safe to
		//request a dispatch. Note that there is no reason to request a
		//dispatch yet if we still hold the mainLock - we will request
		//one when we DO release the mainLock
		if (!mainLock.isHeldByCurrentThread()) {
			dispatchUpdater.request();
		}

		//Notify listeners
		fireConcludeChange(changed);
	}

	/**
	 * Dispatch any changes to listeners
	 */
	private void dispatch() {
		//Make sure we are locked - prevents any more changes occuring while dispatcher dispatches
		mainLock.lock();
		try {
			//Dispatch changes to listener, if we have any
			if (!initial.isEmpty()) {
				dispatcher.dispatch();
			}
		} finally {
			leaveMainLock();
		}
	}
	
	private void processListeners(Changeable changed, Change change) {
		
		//Process each listener
		for (Changeable listener : changed.features().changeableListenerList()) {
			
			//Tell listener about the change to something it was listening to, 
			//and get any change it itself makes in response
			Change newChange = listener.features().internalChange(changed, change, initial, currentChanges);

			//If the listener has actually changed, deal with the change
			if (newChange != null) {

				//Note that we reject initial changes, by definition
				if (newChange.initial()) {
					//FIXME should probably be a specific runtime exception for this - should we throw an
					//exception instead of just logging and ignoring?
					//throw new IllegalArgumentException("Cannot accept an initial change as a result of change propagation");
					logger.severe("CONTRACT: Changeable has returned an initial change in response to an internalChange call - this is against the contract for Changeable");
					
				//Deal with valid changes
				} else {

					//Extend the change for the listener to cover the new change as well, using current changes map
					Change extendedChange = extendChange(listener, newChange, currentChanges);
					
					//If we have extended the change then notify listeners of the listener
					if (extendedChange != null) {
						processListeners(listener, newChange);
					}
				}
				
			}
		}
		
	}
	
	/**
	 * Extend the current change for a {@link Changeable}
	 * in the changes map.
	 * This either extends the current change if there is one, OR
	 * adds the new change if there is no current change.
	 * @param changeable
	 * 		The {@link Changeable} whose {@link Change} is to be extended
	 * @param newChange
	 * 		The new {@link Change} for the {@link Changeable}
	 * @param changes
	 * 		The {@link Change} map
	 * @return
	 * 		If the change for the listener is extended, the
	 * change it was extended to is returned.
	 * If the existing change already covered the new change (so
	 * no extension needs to take place), null is returned
	 */
	private static Change extendChange(Changeable changeable, Change newChange, Map<Changeable, Change> changes) {
		//See if we have a current change for this listener
		Change currentChange = changes.get(changeable);
		
		//Make the change that we will use for the listener - if
		//we have no current change, just use the new one, otherwise
		//extend the existing one
		Change changeToUse;
		if (currentChange == null){
			changeToUse = newChange;
		} else {
			changeToUse = newChange.extend(currentChange);
		}
		
		//If we have extended the change (or we are using the new one directly)
		//then put it in the map
		if (changeToUse != null) {
			//Put the new change for changeable
			changes.put(changeable, changeToUse);
		}
		
		//Return the extended change if there is one, or null if the existing change is large enough
		return changeToUse;
	}

	//TODO we can use the changeable passed to methods to check for correct locking/unlocking
	//Might be best if this is optional for use during testing. For example we can store the 
	//changeable that locks the lock, and check it is the same one that unlocks the lock.
	
	@Override
	public void prepareListenerChange(Changeable changeable) {
		mainLock.lock();
	}

	@Override
	public void concludeListenerChange(Changeable changeable) {
		leaveMainLock();
	}

	@Override
	public void prepareRead(Changeable changeable) {
		mainLock.lock();
	}
	
	@Override
	public void concludeRead(Changeable changeable) {
		leaveMainLock();
	}

	@Override
	public Map<Changeable, Change> changes() {
		return umAllChanges;
	}

	@Override
	public List<Changeable> initial() {
		return umInitial;
	}

	@Override
	public void acquire() {
		mainLock.lock();
	}

	@Override
	public void release() {
		leaveMainLock();
		
		//We need to request dispatch since it may not have been requested
		//while the mainLock was held
		if (!mainLock.isHeldByCurrentThread()) {
			dispatchUpdater.request();
		}
	}

	@Override
	public void addChangeSystemListener(ChangeSystemListener listener) {
		changeSystemListeners.add(listener);
	}

	@Override
	public void removeChangeSystemListener(ChangeSystemListener listener) {
		if (!changeSystemListeners.remove(listener)) {
			logger.log(Level.FINE, "Removed ChangeSystemListener which was not registered.", new Exception("Stack Trace"));
		}
	}
	
	private void firePrepareChange(Changeable changed) {
		for (ChangeSystemListener listener : changeSystemListeners) {
			listener.prepareChange(this, changed);
		}
	}

	private void fireConcludeChange(Changeable changed) {
		for (ChangeSystemListener listener : changeSystemListeners) {
			listener.concludeChange(this, changed);
		}
	}

}
