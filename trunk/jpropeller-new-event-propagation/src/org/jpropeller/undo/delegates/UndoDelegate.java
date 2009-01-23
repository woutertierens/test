package org.jpropeller.undo.delegates;

import org.jpropeller.properties.change.Changeable;

/**
 * Provides services to allow undo/redo of changes to a {@link Changeable}
 * @param <T>
 * 		The type of {@link Changeable} this delegate will save and restore
 */
public interface UndoDelegate<T extends Changeable> {

	/**
	 * Save a {@link Changeable}
	 * @param changeable
	 * 		The {@link Changeable} to save
	 * @return
	 * 		An object storing the data necessary to restore
	 * the {@link Changeable} to its current (shallow) state, when
	 * passed to {@link #restore(Changeable, Object)}. This may
	 * be null, if no data is needed to restore the state, or
	 * if the state should not be restored.
	 */
	public Object save(T changeable);
	
	/**
	 * Restore a {@link Changeable}
	 * @param changeable
	 * 		The {@link Changeable} to restore - should
	 * be the same instance that was passed to {@link #save(Changeable)}
	 * to produce the data.
	 * @param data
	 * 		An object storing the data necessary to restore
	 * the {@link Changeable} to a previous (shallow) state.
	 * Should have been provided by a previous invocation of
	 * the {@link #save(Changeable)} method on the same
	 * {@link Changeable}
	 */
	public void restore(T changeable, Object data);
	
}
