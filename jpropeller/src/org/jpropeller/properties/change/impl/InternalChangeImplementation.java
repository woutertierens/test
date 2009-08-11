package org.jpropeller.properties.change.impl;

import java.util.List;
import java.util.Map;

import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.Changeable;

/**
 * An implementation of response to internal changes,
 * to be used by {@link ChangeableFeaturesDefault}
 */
public interface InternalChangeImplementation {

	/**
	 * Respond to an internal change
	 * @param changed
	 * 		The {@link Changeable} that has changed 
	 * @param change 
	 * 		The {@link Change} that has been made
	 * @param initial
	 * 		The initial change in the change set
	 * @param changes
	 * 		Map from all changed {@link Changeable} instances to
	 * the {@link Change}s they made
	 * @return
	 * 		Any change made by this {@link Changeable} in response to the changes.
	 */
	public Change internalChange(Changeable changed, Change change, List<Changeable> initial,
			Map<Changeable, Change> changes);
}
