package org.jpropeller.undo.delegates.impl;

import org.jpropeller.properties.change.Changeable;
import org.jpropeller.undo.delegates.UndoDelegate;

/**
 * An {@link UndoDelegate} suitable for any {@link Changeable} that
 * has no undoable state (for example, immutable and transient
 * {@link Changeable}s, or those that do not wish to have changes
 * undone). This simply provides null data when asked to 
 * {@link #save(Changeable)} any {@link Changeable}s,
 * and does nothing when asked to {@link #restore(Changeable, Object)}
 * them.  
 */
public class NullUndoDelegate implements UndoDelegate<Changeable> {

	@Override
	public Object save(Changeable changeable) {
		return null;
	}

	@Override
	public void restore(Changeable changeable, Object data) {
	}

}
