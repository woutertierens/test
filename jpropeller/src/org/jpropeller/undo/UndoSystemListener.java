package org.jpropeller.undo;

/**
 * Listener to an {@link UndoSystem}
 */
public interface UndoSystemListener {

	/**
	 * Called when an {@link UndoSystem} undoes a change
	 * @param system
	 * 		The system that has undone a change
	 */
	public void undone(UndoSystem system);

	/**
	 * Called when an {@link UndoSystem} redoes a change
	 * @param system
	 * 		The system that has redone a change
	 */
	public void redone(UndoSystem system);
	
	/**
	 * Called when an {@link UndoSystem} changes state
	 * (for example, changing from being able
	 * to undo to not being able, or vice versa, etc.) 
	 * @param system
	 * 		The system that has changed
	 */
	public void changed(UndoSystem system);
	
}
