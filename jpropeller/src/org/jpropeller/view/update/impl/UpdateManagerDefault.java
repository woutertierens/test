package org.jpropeller.view.update.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jpropeller.concurrency.DaemonThreadFactory;
import org.jpropeller.view.update.Updatable;
import org.jpropeller.view.update.UpdateDispatcher;
import org.jpropeller.view.update.UpdateManager;


/**
 * Default implementation of {@link UpdateManager}
 * This coalesces repeated events that occur at too low a time interval,
 * while also ensuring that updates occur with at least a maximum
 * interval. To reduce latency, the first event after a sufficient
 * interval is sent through immediately - subsequent events after the
 * first may then be coalesced.
 * 
 * @author shingoki
 *
 */
public class UpdateManagerDefault implements UpdateManager {

	//private final static Logger logger = Logger.getLogger(UpdateManagerDefault.class.getCanonicalName());
	
	//Time of last change in any view
	private long lastChange = 0;
	
	//When events are seen less than this time in ms apart, they
	//may be coalesced
	private long fusionInterval;
	
	//A set to receive incoming requests for updates. Swapped with changedViewsTransmit.
	private Set<Updatable> changedViewsReceive = new HashSet<Updatable>(200);
	
	//A set to read from for dispatching updates. Swapped with changedViewsReceive.
	private Set<Updatable> changedViewsTransmit = new HashSet<Updatable>(200);

	//Executor used to schedule regular update
	private ScheduledExecutorService executor;
	
	//Dispatcher used to actually call update on the views
	private UpdateDispatcher dispatcher;
	
	/**
	 * Create an {@link UpdateManagerDefault} with default timings
	 * of 50ms fusion interval, and a regular update every
	 * 100ms, using a dispatcher that will update views in the Swing thread
	 * This also starts the regular update task
	 */
	public UpdateManagerDefault() {
		this(50, 100, new SwingUpdateDispatcher());
	}
	
	/**
	 * Create an {@link UpdateManagerDefault}
	 * This also starts the regular update task
	 * 
	 * @param fusionInterval 
	 * When events are seen less than this time in ms apart, they 
	 * may be coalesced
	 * 
	 * @param regularUpdateInterval
	 * Updates will be sent out with at most this time between them,
	 * when changes are being seen
	 * 
	 * @param dispatcher
	 * Used to actually send updates to the views
	 */
	public UpdateManagerDefault(long fusionInterval, long regularUpdateInterval, UpdateDispatcher dispatcher) {
		super();
		this.fusionInterval = fusionInterval;
		this.dispatcher = dispatcher;

		//Schedule a regularUpdate at regularUpdateInterval
		executor = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory());
		executor.scheduleAtFixedRate(
				new Runnable() {
					@Override
					public void run() {
						regularUpdate();
					}
				}, 
				regularUpdateInterval, regularUpdateInterval, TimeUnit.MILLISECONDS);
	}

	private synchronized void regularUpdate() {
		//We need to fire the update using update dispatcher
		fireUpdate();
	}
	
	@Override
	public synchronized void registerView(Updatable view) {
		//Nothing much to do as yet
	}

	@Override
	public synchronized void deregisterView(Updatable view) {
		//Nothing much to do as yet
	}

	@Override
	public synchronized void updateRequiredBy(Updatable view) {
		
		//logger.finest("updateRequiredBy(" + view + ")");
		
		//This view has changed
		changedViewsReceive.add(view);
		
		//Work out timing
		long time = System.currentTimeMillis();
		long interval = time - lastChange;
		lastChange = time;
		
		//If we can't coalesce with the last change, then fire an update immediately
		//Otherwise, the regular update will catch it and fire an update sometime soon
		if (interval > fusionInterval) {
			fireUpdate();
		}
	}

	/**
	 * Update all changed views
	 */
	private synchronized void fireUpdate() {

		//Keep dispatching while we have any changed views
		while ((!changedViewsReceive.isEmpty()) || (!changedViewsTransmit.isEmpty())) {
			//Swap sets so that we will have previously received events in transmit set,
			//and so that the OTHER set will be receiving any new updates
			swapSets();
			
			//Dispatch the transmit set, and clear it. Note that any extra
			//requests for updates during the dispatch will be added to the
			//receive set
			if (!changedViewsTransmit.isEmpty()) {
				dispatcher.dispatch(changedViewsTransmit);
				changedViewsTransmit.clear();
			}
		}

	}
	
	//Swap the receive and transmit changed views set
	private synchronized void swapSets() {
		Set<Updatable> temp = changedViewsReceive;
		changedViewsReceive = changedViewsTransmit;
		changedViewsTransmit = temp;
	}
	
}
