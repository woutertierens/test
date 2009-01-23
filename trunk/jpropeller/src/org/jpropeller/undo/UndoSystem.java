package org.jpropeller.undo;

import org.jpropeller.properties.change.Changeable;

/**
 * A system allowing for changes to a system of {@link Changeable}s
 * to be undone and redone
 */
public interface UndoSystem {

	/**
	 * Check whether we can undo
	 * @return
	 * 		True if we can currently undo, false otherwise. Note that
	 * it is possible for this state to change before {@link #undo()}
	 * can be called - in this case {@link #undo()} will just do nothing
	 */
	public boolean canUndo();

	/**
	 * Check whether we can redo
	 * @return
	 * 		True if we can currently redo, false otherwise. Note that
	 * it is possible for this state to change before {@link #redo()}
	 * can be called - in this case {@link #redo()} will just do nothing
	 */
	public boolean canRedo();

	/**
	 * If possible, undo a change. If there are no changes to undo, do nothing.
	 */
	public void undo();

	/**
	 * If possible, redo a change
	 */
	public void redo();

	/**
	 * Clear all undo/redo history
	 */
	public void clear();

	/**
	 * Add a listener to be notified of changes
	 * @param listener
	 * 		The listener
	 */
	public void addListener(UndoSystemListener listener);

	/**
	 * Remove a listener, no longer to be notified of changes
	 * @param listener
	 * 		The listener
	 */
	public void removeListener(UndoSystemListener listener);

}