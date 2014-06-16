package org.jpropeller.properties.change.impl;

import java.util.Set;

import javax.swing.SwingUtilities;

import org.jpropeller.collection.impl.IdentityHashSet;
import org.jpropeller.properties.change.ChangeDispatchSource;
import org.jpropeller.properties.change.ChangeDispatcher;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;

/**
 * Sends changes on to {@link ChangeListener}s by 
 * invoking a {@link Runnable} in the Swing thread (EDT)
 * to call all change methods. This uses 
 * {@link SwingUtilities#invokeLater(Runnable)},
 * and so the dispatch does not necessarily occur 
 * before {@link #dispatch()} returns.
 */
public class ChangeDispatcherSwing implements ChangeDispatcher {

	private ChangeDispatchSource source;
	
	private Runnable doDispatchRunnable = new Runnable() {
		@Override
		public void run() {
			doDispatch();
		}
	};
	
	@Override
	public void setSource(ChangeDispatchSource source) {
		this.source = source;
	}
	
	@Override
	public void dispatch() {

		//If we are in the swing thread, dispatch directly
		if (SwingUtilities.isEventDispatchThread()) {
			doDispatch();
			
		//Otherwise, we need to dispatch in the swing thread
		} else {
			SwingUtilities.invokeLater(doDispatchRunnable);
		}
		
	}

	private synchronized void doDispatch() {
		Set<ChangeListener> affectedListeners = new IdentityHashSet<ChangeListener>();

		//This is expected to lock the source so we are allowed to dispatch, and the changes/initial will not change
		source.prepareDispatch();
		try {
			//Find the set of affected listeners, with no identical repeats (we accept equal repeats)
			affectedListeners.clear();
			for (Changeable changeable : source.changes().keySet()) {
				for (ChangeListener listener : changeable.features().listenerList()) {
					affectedListeners.add(listener);
				}
			}

			//Call change on all listeners
			for (ChangeListener listener : affectedListeners) {
				listener.change(source.initial(), source.changes());
			}
			
		//Always conclude the dispatch
		} finally {
			source.concludeDispatch();
		}
	}
	
}
