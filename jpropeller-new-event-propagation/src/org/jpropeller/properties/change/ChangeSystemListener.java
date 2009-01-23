package org.jpropeller.properties.change;

/**
 * A listener to a {@link ChangeSystem}, to be notified whenever that change system
 * reaches key points in the process of reading and changing {@link Changeable} state.
 * 
 * Note that this is NOT the usual interface for listening for changes to a {@link Changeable} - 
 * for most uses, the appropriate route is to implement {@link ChangeListener}, then 
 * listen to a {@link Changeable} using {@link Changeable#features()} to get its
 * {@link ChangeableFeatures}, then calling {@link ChangeableFeatures#addListener(ChangeListener)}.
 * When this is done, the {@link ChangeListener} will receive only changes to that {@link Changeable},
 * and any {@link Changeable}s it references.
 * 
 * A {@link ChangeSystemListener} is different in that it receives notification directly 
 * from the {@link ChangeSystem} when ANY {@link Changeable} is being read or changed,
 * at key stages in the process. This is useful only in special cases - for example
 * for implementing an undo system, debugging/logging, etc. In general, if you do not
 * know exactly what this is for already, you are using the wrong interface!
 */
public interface ChangeSystemListener {

//	/**
//	 * Called when {@link ChangeSystem#propagateChange(Changeable, Change)} has been
//	 * invoked, but before it performs propagation. All relevant locks are already held.
//	 * @param system
//	 * 		The {@link ChangeSystem} on which method was called
//	 * @param changed
//	 * 		See {@link ChangeSystem}
//	 * @param change
//	 * 		See {@link ChangeSystem}
//	 */
//	public void propagateChange(ChangeSystem system, Changeable changed, Change change);

	/**
	 * Called when {@link ChangeSystem#concludeChange(Changeable)} has been
	 * invoked, but before it concludes the change. All relevant locks are already held.
	 * @param system
	 * 		The {@link ChangeSystem} on which method was called
	 * @param changed
	 * 		See {@link ChangeSystem}
	 */
	public void concludeChange(ChangeSystem system, Changeable changed);
	
	/**
	 * Called when {@link ChangeSystem#prepareChange(Changeable)} has been
	 * invoked, but before it prepares the change. All relevant locks are already held.
	 * @param system
	 * 		The {@link ChangeSystem} on which method was called
	 * @param changed
	 * 		See {@link ChangeSystem}
	 */
	public void prepareChange(ChangeSystem system, Changeable changed);
	
//
//	/**
//	 * Called when {@link ChangeSystem#prepareRead(Changeable)} has been
//	 * invoked, but before it prepares the read. All relevant locks are already held.
//	 * @param system
//	 * 		The {@link ChangeSystem} on which method was called
//	 * @param changeable
//	 * 		See {@link ChangeSystem}
//	 */
//	public void prepareRead(ChangeSystem system, Changeable changeable);
//
//	/**
//	 * Called when {@link ChangeSystem#concludeRead(Changeable)} has been
//	 * invoked, but before it concludes the read. All relevant locks are already held.
//	 * @param system
//	 * 		The {@link ChangeSystem} on which method was called
//	 * @param changeable
//	 * 		See {@link ChangeSystem}
//	 */
//	public void concludeRead(ChangeSystem system, Changeable changeable);
//
//	/**
//	 * Called when {@link ChangeSystem#prepareListenerChange(Changeable)} has been
//	 * invoked, but before it prepares for the change. All relevant locks are already held.
//	 * @param system
//	 * 		The {@link ChangeSystem} on which method was called
//	 * @param changeable
//	 * 		See {@link ChangeSystem}
//	 */
//	public void prepareListenerChange(ChangeSystem system, Changeable changeable);
//
//	/**
//	 * Called when {@link ChangeSystem#concludeListenerChange(Changeable)} has been
//	 * invoked, but before it concludes the change. All relevant locks are already held.
//	 * @param system
//	 * 		The {@link ChangeSystem} on which method was called
//	 * @param changeable
//	 * 		See {@link ChangeSystem}
//	 */
//	public void concludeListenerChange(ChangeSystem system, Changeable changeable);
//
//	/**
//	 * Called when {@link ChangeSystem#acquire()} has been
//	 * invoked, when lock is held, before the method returns.
//	 * @param system
//	 * 		The {@link ChangeSystem} on which method was called
//	 */
//	public void acquire(ChangeSystem system);
//	
//	/**
//	 * Called when {@link ChangeSystem#acquire()} has been
//	 * invoked, when lock is still held, before the method returns.
//	 * @param system
//	 * 		The {@link ChangeSystem} on which method was called
//	 */
//	public void release(ChangeSystem system);
	
	
}
