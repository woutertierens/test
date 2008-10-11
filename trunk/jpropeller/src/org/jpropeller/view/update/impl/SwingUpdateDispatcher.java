package org.jpropeller.view.update.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.jpropeller.view.update.UpdatableView;
import org.jpropeller.view.update.UpdateDispatcher;

/**
 * An {@link UpdateDispatcher} that provides invokes the
 * {@link UpdatableView#update()} method of views in the
 * Swing thread (EDT).
 * 
 * {@link SwingUtilities#invokeLater(Runnable)} is used to ensure
 * that updates are made at some point soon after they are dispatched,
 * but in the Swing thread. Multiple dispatches may be coalesced into one
 * update, since any dispatches added while the invokeLater request is
 * pending will be dealt with at once. 
 * 
 * Note that if this dispatcher has {@link #dispatch(Collection)} or 
 * {@link #dispatch(UpdatableView)} called from the EDT itself, it will
 * not use {@link SwingUtilities#invokeLater(Runnable)} but will still
 * function. It may be very slightly less efficient than another
 * {@link UpdateDispatcher} since it will check
 * whether it is in the Swing thread once per dispatch call.
 * 
 */
public class SwingUpdateDispatcher implements UpdateDispatcher {

	//Store views waiting for update
	private Set<UpdatableView<?>> pendingViewsReceive = new HashSet<UpdatableView<?>>(200);
	private Set<UpdatableView<?>> pendingViewsTransmit = new HashSet<UpdatableView<?>>(200);
	
	//Store whether we have a swing invokeLater pending
	private boolean invokePending = false;
	
	@Override
	public synchronized void dispatch(UpdatableView<?> view) {
		pendingViewsReceive.add(view);
		invoke();
	}

	@Override
	public synchronized void dispatch(Collection<UpdatableView<?>> views) {
		pendingViewsReceive.addAll(views);
		invoke();
	}

	//Invoke updatePending later in the swing thread, must be called when synchronized on pendingViews
	//This will do nothing if there is already an invokeLater pending that will service the request
	private synchronized void invoke() {
		if (invokePending) return;
		
		//Note we set pending here in case we are in the EDT and hence 
		//run updatePending() immediately. If we set invokePending at
		//the end of this incoke() method, we would set invokePending to
		//true AFTER the updatePending() had actually been called.
		invokePending = true;
		
		//If we are in the EDT already, we can just run directly, otherwise
		//we need to invoke later
		if (SwingUtilities.isEventDispatchThread()) {
			updatePending();
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					updatePending();
				}
			});
		}
	}

	//Update all pending views
	private synchronized void updatePending() {
			
		//Keep dispatching while we have any pending views
		while ((!pendingViewsReceive.isEmpty()) || (!pendingViewsTransmit.isEmpty())) {
			//Swap sets so that we will have previously received views in transmit set,
			//and so that the OTHER set will be receiving any new updates
			swapSets();
			
			//Dispatch the transmit set, and clear it. Note that any extra
			//requests for updates during the dispatch will be added to the
			//receive set
			if (!pendingViewsTransmit.isEmpty()) {
				for (UpdatableView<?> view : pendingViewsTransmit) {
					view.update();
				}
				pendingViewsTransmit.clear();
			}
		}

		/*
		while (!pendingViews.isEmpty()) {
			for (Iterator<UpdatableView<?>> it = pendingViews.iterator(); it.hasNext();) {
				UpdatableView<?> view = it.next();
				view.update();
				//logger.finest("update(" + view + ")");
				it.remove();
			}
		}
		*/
		
		invokePending = false;
	}
	
	//Swap the receive and transmit changed views set
	private synchronized void swapSets() {
		Set<UpdatableView<?>> temp = pendingViewsReceive;
		pendingViewsReceive = pendingViewsTransmit;
		pendingViewsTransmit = temp;
	}

}
