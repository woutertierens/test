package org.jpropeller.undo.impl;

import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.ChangeSystem;
import org.jpropeller.properties.change.ChangeSystemListener;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.system.Props;
import org.jpropeller.undo.UndoSystem;
import org.jpropeller.undo.UndoSystemListener;
import org.jpropeller.undo.delegates.UndoDelegate;
import org.jpropeller.undo.delegates.UndoDelegateSource;
import org.jpropeller.undo.delegates.UndoDelegateSourceException;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.util.Listeners;

/**
 * Default implementation of an undo system for {@link Changeable}s
 */
public class UndoSystemDefault implements ChangeListener, ChangeSystemListener, UndoSystem {

	private final static Logger logger = GeneralUtils.logger(UndoSystemDefault.class); 
	
	private UndoDelegateSource delegateSource;
	private Map<Changeable, State> knownPreStates = new IdentityHashMap<Changeable, State>();

	private List<UndoRedoStates> past = new LinkedList<UndoRedoStates>();
	private List<UndoRedoStates> future = new LinkedList<UndoRedoStates>();
	
	private Listeners<UndoSystemListener> listeners = new Listeners<UndoSystemListener>();
	
	private boolean acting = false;
	
	private boolean missingDelegate = false;
	
	/**
	 * Create an undo system, handling all {@link Changeable}s referenced
	 * from the given root {@link Changeable}
	 * @param root
	 * 		The root {@link Changeable} - any changes in this {@link Changeable}
	 * or {@link Changeable}s referenced from it will be tracked for undo/redo
	 * functionality
	 * @param delegateSource
	 * 		Source of {@link UndoDelegate}s 
	 */
	public UndoSystemDefault(Changeable root, UndoDelegateSource delegateSource) {
		super();
		this.delegateSource = delegateSource;
		
		Props.getPropSystem().getChangeSystem().addChangeSystemListener(this);
		root.features().addListener(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		
		//If we were missing a delegate for a change, we cannot undo, so clear history
		if (missingDelegate) {
			missingDelegate = false;
			clear();
			knownPreStates.clear();
			fireChanged();
			return;
		}
		
		//To undo the change, we need to a list of pre-change-states of the "initial" changeables
		//To redo the change, we need list of states after the change
		List<State> undoStates = new LinkedList<State>();
		List<State> redoStates = new LinkedList<State>();
		
		//We store the states of all objects that have changed (except those that were not
		//recorded since we triggered the changes by undoing/redoing). This change method
		//is just used to make sure that we create an undo/redo state only when a change occurs
		//in the root or changeables referenced from it. Hence the UndoSystem is acting on all
		//Changeable state in the ChangeSystem
		for (Changeable changed : knownPreStates.keySet()) {
			
			//Add prestate to states needed to undo change
			State preState = knownPreStates.get(changed);
			undoStates.add(preState);
			
			//Add post-state to states needed to redo the change
			Object postStateData = preState.getDelegate().save(changed);
			State postState = new State(changed, preState.getDelegate(), postStateData);
			redoStates.add(postState);
				
		}

		//If we have no undo/redo states, this means that all the changes we have just seen
		//are a result of our actions (undoing/redoing), or cannot be undone, so we don't 
		//need another state - we will keep the prestates so that when we DO create an
		//undo state, it includes all changed state since the last undo (even outside
		//the root-referenced state).
		if (undoStates.isEmpty()) return;
		
		//Make the total states
		UndoRedoStates undoRedoStates = new UndoRedoStates(undoStates, redoStates);
		
		//If we had any future states, we can't use them now
		future.clear();
		
		//Add past state
		past.add(undoRedoStates);
		
		//We've seen and dealt with the change now, so we don't need the pre-states
		knownPreStates.clear();
		
		//Tell listeners
		fireChanged();
	}

	public boolean canUndo() {
		Props.getPropSystem().getChangeSystem().acquire();
		try {
			return !past.isEmpty();
		} finally {
			Props.getPropSystem().getChangeSystem().release();
		}
	}

	public boolean canRedo() {
		Props.getPropSystem().getChangeSystem().acquire();
		try {
			return !future.isEmpty();
		} finally {
			Props.getPropSystem().getChangeSystem().release();
		}
	}

	private void undoPending() {
		//FIXME This is not the correct order - shouldn't matter for valid props, but
		//might be nice to use correct order
		for (State state : knownPreStates.values()) {
			state.restore();
		}
	}
	
	public void undo() {

		Props.getPropSystem().getChangeSystem().acquire();
		try {

			if (past.isEmpty()) return;
		
			//Get the most recent past state - we will undo this one
			UndoRedoStates undo = past.remove(past.size()-1);
			
			//This now becomes a future state
			future.add(0, undo);
			
			//Actually undo, making sure we don't track any changes
			acting = true;
			//First, undo any pending changes (these must be changes to the
			//non-root-referenced state)
			undoPending();
			undo.undo();
			acting = false;
			
			knownPreStates.clear();

			fireUndone();
			fireChanged();
			
		} finally {
			Props.getPropSystem().getChangeSystem().release();
		}
	}

	public void redo() {		
		Props.getPropSystem().getChangeSystem().acquire();
		try {
			
			if (future.isEmpty()) return;
			
			
			//Get the first future state - we will redo this one
			UndoRedoStates redo = future.remove(0);
			
			//This now becomes a past state
			past.add(redo);
			
			//Actually redo, making sure we don't track any changes
			acting = true;
			//First, undo any pending changes (these must be changes to the
			//non-root-referenced state)
			undoPending();
			redo.redo();
			acting = false;

			knownPreStates.clear();

			fireRedone();
			fireChanged();
			
		} finally {
			Props.getPropSystem().getChangeSystem().release();
		}
	}

	public void clear() {
		Props.getPropSystem().getChangeSystem().acquire();
		try {
			past.clear();
			future.clear();
			
			fireChanged();
		} finally {
			Props.getPropSystem().getChangeSystem().release();
		}
	}
	
	@Override
	public void concludeChange(ChangeSystem system, Changeable changed) {
		//Ignore conclusion of changes
	}

	//We can only get raw UndoDelegates from the source - we just need to trust
	//that the source will get this right
	@SuppressWarnings("unchecked")
	@Override
	public void prepareChange(ChangeSystem system, Changeable changed) {
		
		//If we are currently undoing or redoing, then
		//we ignore changes - since we caused them ourself
		if (acting) return;
		
		//If we have a state already, keep it - we want the EARLIEST
		//state before changes, in case we have multiple changes to the
		//same changeable
		if (knownPreStates.containsKey(changed)) {
			return;
		}
		
		//If the changed is annotated as not undoable, ignore it
		if (changed.features().hasMetadata(Changeable.DO_NOT_UNDO)) {
			return;
		}
		
		//If we don't have a state already, try to save state of changed
		UndoDelegate delegate;
		try {
			delegate = delegateSource.get(changed);
			Object state = delegate.save(changed);
			
			//Don't create a state if there is no data - this indicates there
			//is nothing to undo
			if (state != null) {
				knownPreStates.put(changed, new State(changed, delegate, state));
				//System.out.println("Added " + changed + ", class " + changed.getClass() + " to " + state);
			}
		} catch (UndoDelegateSourceException e) {
			//If we fail to save the state, then we need to make sure we don't
			//allow undoing of the next change, since we may get it wrong
			missingDelegate = true;
			logger.severe("Could not find an UndoDelegate for " + changed + ", will clear undo history, and will not undo this stage");
		}		
	}

	/**
	 * A list of states to be restored to undo a change,
	 * and another list to be restored to redo a change.
	 */
	private class UndoRedoStates {
		List<State> undoStates;
		List<State> redoStates;
		
		/**
		 * Create an {@link UndoRedoStates}
		 * @param undoStates
		 * 		The states to be restored, in order, to undo the change
		 * @param redoStates
		 * 		The states to be restored, in order, to redo the change
		 */
		public UndoRedoStates(List<State> undoStates, List<State> redoStates) {
			super();
			this.undoStates = undoStates;
			this.redoStates = redoStates;
		}
		
		/**
		 * Undo the change
		 */
		public void undo() {
			//FIXME This might not be the correct order - shouldn't matter for valid props, but
			//might be nice to use correct order
			for (State state : undoStates) {
				state.restore();
			}
		}
		
		/**
		 * Redo the change
		 */
		public void redo() {
			//FIXME This might not be the correct order - shouldn't matter for valid props, but
			//might be nice to use correct order
			for (State state : redoStates) {
				state.restore();
			}
		}
	}
	
	//All suppressed warnings are since we can't reliably determine matching types - 
	//this is all due to relying on the undo delegate source to provide the right types
	/**
	 * A state that can be restored
	 */
	private class State {
		Changeable changeable;
		@SuppressWarnings("unchecked")
		UndoDelegate delegate;
		Object state;
		@SuppressWarnings("unchecked")
		public State(Changeable changeable,
				UndoDelegate delegate, Object state) {
			super();
			this.changeable = changeable;
			this.delegate = delegate;
			this.state = state;
		}

		@SuppressWarnings("unchecked")
		public void restore() {
			//System.out.println("Restored " + changeable + ", class " + changeable.getClass() + " to " + state);
			delegate.restore(changeable, state);
		}

		@SuppressWarnings("unchecked")
		public UndoDelegate getDelegate() {
			return delegate;
		}
	}

	@Override
	public void addListener(UndoSystemListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(UndoSystemListener listener) {
		if (!listeners.remove(listener)) {
			logger.log(Level.FINE, "Removed UndoSystemListener which was not registered.", new Exception("Stack Trace"));
		}
	}
	
	private void fireUndone() {
		for (UndoSystemListener listener : listeners) {
			listener.undone(this);
		}
	}

	private void fireRedone() {
		for (UndoSystemListener listener : listeners) {
			listener.redone(this);
		}
	}

	private void fireChanged() {
		for (UndoSystemListener listener : listeners) {
			listener.changed(this);
		}
	}

}
