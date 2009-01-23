package org.jpropeller.util;

import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeListener;
import org.jpropeller.properties.change.Changeable;

/**
 * {@link ChangeListener} that counts events, and stores most recent one
 */
public class StoringChangeListener implements ChangeListener {

	int eventCount = 0;
	List<Changeable> initial;
	Map<Changeable, Change> changes;
	
	@Override
	public synchronized void change(List<Changeable> initial, Map<Changeable, Change> changes) {
		eventCount++;
		this.initial = new LinkedList<Changeable>(initial);
		this.changes = new IdentityHashMap<Changeable, Change>(changes);
	}

	/**
	 * Get the most recent initial {@link Changeable} since
	 * {@link #clear()}, or null if none received 
	 * @return	
	 * 		initial
	 */
	public List<Changeable> getInitial() {
		return initial;
	}

	/**
	 * Get the most recent map of changes since
	 * {@link #clear()}, or null if none received 
	 * @return	
	 * 		changes
	 */
	public Map<Changeable, Change> getChanges() {
		return changes;
	}

	/**
	 * Get the number of events received since last {@link #clear()} 
	 * @return
	 * 		Number of events
	 */
	public int getEventCount() {
		return eventCount;
	}
	
	/**
	 * Clear events
	 */
	public void clear() {
		eventCount = 0;
		initial = null;
		changes = null;
	}
	
}
