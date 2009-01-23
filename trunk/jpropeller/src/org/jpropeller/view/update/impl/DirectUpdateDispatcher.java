package org.jpropeller.view.update.impl;

import java.util.Collection;

import org.jpropeller.view.update.Updatable;
import org.jpropeller.view.update.UpdateDispatcher;

/**
 * An {@link UpdateDispatcher} that invokes the
 * {@link Updatable#update()} method of {@link Updatable}s immediately
 * when called.
 */
public class DirectUpdateDispatcher implements UpdateDispatcher {

	@Override
	public synchronized void dispatch(Updatable updatable) {
		updatable.update();
	}

	@Override
	public synchronized void dispatch(Collection<Updatable> updatables) {
		for (Updatable updatable : updatables) {
			updatable.update();
		}
	}
}
