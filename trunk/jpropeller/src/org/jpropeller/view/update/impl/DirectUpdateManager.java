package org.jpropeller.view.update.impl;

import org.jpropeller.view.update.Updatable;
import org.jpropeller.view.update.UpdateManager;


/**
 * Implementation of {@link UpdateManager} that just directly
 * calls back to {@link Updatable#update()} whenever
 * {@link #updateRequiredBy(Updatable)} is called.
 * 
 */
public class DirectUpdateManager implements UpdateManager {

	/**
	 * Create a default {@link DirectUpdateManager}
	 */
	public DirectUpdateManager() {
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
		updatable.update();
	}
	
}
