package org.jpropeller.view.update.impl;

import java.util.Set;

import org.jpropeller.collection.impl.IdentityHashSet;
import org.jpropeller.concurrency.Responder;
import org.jpropeller.concurrency.impl.DirectResponder;
import org.jpropeller.view.update.Updatable;
import org.jpropeller.view.update.UpdateDispatcher;
import org.jpropeller.view.update.UpdateManager;


/**
 * Default implementation of {@link UpdateManager}
 * <br />
 * Uses an {@link UpdateDispatcher} to send updates to
 * {@link Updatable}s.
 * <br />
 * Uses an {@link Responder} to actually trigger dispatches
 * (possibly coalescing them, etc.)
 * 
 */
public class UpdateManagerDefault implements UpdateManager {

	//FIXME Ideally updatables should only be stored via weak references,
	//since there is no point keeping an Updatable around just for the sake of updating it,
	//similarly to use of Listeners. This is less important, since leaking views is
	//harder than leaking elements of model (views tend to be less dynamic).
	
	//A set to receive incoming requests for updates. Swapped with changedUpdatablesTransmit.
	private Set<Updatable> changedUpdatablesReceive = new IdentityHashSet<Updatable>(200);
	
	//A set to read from for dispatching updates. Swapped with changedUpdatablesReceive.
	private Set<Updatable> changedUpdatablesTransmit = new IdentityHashSet<Updatable>(200);

	//Dispatcher used to actually call update on the updatables
	private UpdateDispatcher dispatcher;

	/**
	 * {@link Responder} used to schedule updates
	 */
	private Responder updater;
	
	final Runnable updateRunnable = new Runnable() {
		@Override
		public void run() {
			fireUpdate();
		}
	};
	
	/**
	 * Create a default {@link UpdateManagerDefault}
	 * using a {@link DirectUpdateDispatcher}
	 */
	public UpdateManagerDefault() {
		this(new DirectUpdateDispatcher());
	}
	
	/**
	 * Create an {@link UpdateManagerDefault}
	 * 
	 * @param dispatcher
	 * 		Used to actually send updates to the {@link Updatable}s
	 */
	public UpdateManagerDefault(UpdateDispatcher dispatcher) {
		super();
		
		//Use a direct updater - would want to use a CoalescingUpdater if
		//many updates were expected - however the ChangeSystemDefault 
		//already coalesces changes, so this is probably not required now
		this.updater = new DirectResponder(updateRunnable);
		this.dispatcher = dispatcher;
	}

	@Override
	public synchronized void registerUpdatable(Updatable updatable) {
		//Nothing much to do as yet
	}

	@Override
	public synchronized void deregisterUpdatable(Updatable updatable) {
		//Nothing much to do as yet
	}

	@Override
	public synchronized void updateRequiredBy(Updatable updatable) {
		//This view has changed
		changedUpdatablesReceive.add(updatable);
		
		//Dispatch at some point in the future
		updater.request();
	}

	/**
	 * Update all changed views
	 */
	private synchronized void fireUpdate() {

		//Keep dispatching while we have any changed views
		while ((!changedUpdatablesReceive.isEmpty()) || (!changedUpdatablesTransmit.isEmpty())) {
			//Swap sets so that we will have previously received events in transmit set,
			//and so that the OTHER set will be receiving any new updates
			swapSets();
			
			//Dispatch the transmit set, and clear it. Note that any extra
			//requests for updates during the dispatch will be added to the
			//receive set
			if (!changedUpdatablesTransmit.isEmpty()) {
				dispatcher.dispatch(changedUpdatablesTransmit);
				changedUpdatablesTransmit.clear();
			}
		}

	}
	
	//Swap the receive and transmit changed updatables set
	private synchronized void swapSets() {
		Set<Updatable> temp = changedUpdatablesReceive;
		changedUpdatablesReceive = changedUpdatablesTransmit;
		changedUpdatablesTransmit = temp;
	}
	
}
