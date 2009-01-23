package org.jpropeller.properties.change.impl;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import org.jpropeller.concurrency.Responder;
import org.jpropeller.concurrency.impl.CoalescingResponder;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeDispatchSource;
import org.jpropeller.properties.change.ChangeDispatcher;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.ChangeSystem;
import org.jpropeller.properties.change.ChangeSystemListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.util.GeneralUtils;

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
	
	private List<ChangeSystemListener> changeSystemListeners = new LinkedList<ChangeSystemListener>();
	
	/**
	 * List of {@link Changeable}s that started changes, in order
	 */
	private List<Changeable> initial = new LinkedList<Changeable>();
	
	/**
	 * We use an {@link IdentityHashMap} so that we show the change to each INSTANCE,
	 * we don't care about equality, we need identicality
	 */
	private Map<Changeable, Change> changes = new IdentityHashMap<Changeable, Change>();

	//Make unmodifiable views of lists and maps to pass out (to ChangeDispatcher). We don't
	//want to let the dispatcher or the listeners themselves modify anything. Change instances
	//are already immutable.
	private List<Changeable> umInitial;
	private Map<Changeable, Change> umChanges;

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
		this.umChanges = Collections.unmodifiableMap(changes);
	}

	@Override
	public void prepareDispatch() {
		//Get main lock
		mainLock.lock();
		
		//Only one dispatcher - this thread
		dispatchingLock.lock();
	}

	@Override
	public void concludeDispatch() {
		try {
			//Clear the changes we have just dispatched
			initial.clear();
			changes.clear();
		} finally {		
			//Release locks in reverse order
			dispatchingLock.unlock();
			mainLock.unlock();
		}
	}
	
	@Override
	public void prepareChange(Changeable changed) {
		
		//Only the thread that is currently dispatching (if any) can hold the
		//dispatchingLock. Hence if we hold it here, it indicates that a we 
		//have dispatched a change to a ChangeListener, and it has responded
		//by attempting to make another change to a Changeable. This is illegal
		//since it can cause loops, out of order changes, etc.
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
		
		//We must have the necessary lock here
		if (!mainLock.isHeldByCurrentThread()) {
			throw new IllegalMonitorStateException("Cannot propagateChange without first calling prepareChange (i.e. lock is not held when calling propagateChange)");
		}
		
		//We expect an initial change - log and ignore if otherwise
		if (!change.initial()) {
			logger.severe("CONTRACT: Changeable " + changed + " attempted to startChange() with change " + change + ", which is not initial - this is against the contract for Changeable");
			return;
		}
		
		initial.add(changed);

		//Extend change to initial
		Change extendedInitialChange = extendChange(changed, change);

		//If the initial has actually got an extended change, start recursive processing from initial
		if (extendedInitialChange != null) { 
			processListeners(initial, changed, change);
		}
	}

	@Override
	public void concludeChange(Changeable changed) {
		//Release necessary lock
		mainLock.unlock();
		
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
			mainLock.unlock();
		}
	}
	
	private void processListeners(List<Changeable> initial, Changeable changed, Change change) {
		
		//Process each listener
		for (Changeable listener : changed.features().changeableListenerList()) {
			
			//Tell listener about the change to something it was listening to, 
			//and get any change it itself makes in response
			Change newChange = listener.features().internalChange(changed, change, initial, changes);

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

					//Extend the change for the listener to cover the new change as well
					Change extendedChange = extendChange(listener, newChange);
					
					//If we have extended the change then notify listeners of the listener
					if (extendedChange != null) {
						processListeners(initial, listener, newChange);
					}
				}
				
			}
		}
		
	}
	
	/**
	 * Extend the current change for a {@link Changeable} listener
	 * in the changes map.
	 * This either extends the current change if there is one, OR
	 * adds the new change if there is no current change.
	 * @param listener
	 * 		The listener whose change is to be extended
	 * @param newChange
	 * 		The new change for the listener
	 * @return
	 * 		If the change for the listener is extended, the
	 * change it was extended to is returned.
	 * If the existing change already covered the new change (so
	 * no extension needs to take place), null is returned
	 */
	private Change extendChange(Changeable listener, Change newChange) {
		//See if we have a current change for this listener
		Change currentChange = changes.get(listener);
		
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
			//Put the new change for listener
			changes.put(listener, changeToUse);
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
		mainLock.unlock();
	}

	@Override
	public void prepareRead(Changeable changeable) {
		mainLock.lock();
	}
	
	@Override
	public void concludeRead(Changeable changeable) {
		mainLock.unlock();
	}

	@Override
	public Map<Changeable, Change> changes() {
		return umChanges;
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
		mainLock.unlock();
		
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
		changeSystemListeners.remove(listener);
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
