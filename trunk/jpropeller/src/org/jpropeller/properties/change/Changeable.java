package org.jpropeller.properties.change;

import org.jpropeller.properties.Prop;


/**
 * Interface for something that can change, causing a {@link Change}
 */
public interface Changeable {

	/**
	 * Standard annotation key.
	 * If this key is defined (with any value), data is considered
	 * to be transient, and should not be stored persistently. Often
	 * such data is a read-only {@link Prop}, but this is not universal - 
	 * in particular, non-prop data may have no editability data.
	 */
	public final static String TRANSIENT = "Transient";
		
	/**
	 * Standard annotation key.
	 * If this key is defined (with any value), changes to data should
	 * not be "undone" by an undo/redo system. Any changes should essentially
	 * be ignored by such a system. Examples are logs and accumulated data,
	 * where it is meaningless to "undo" the occurrence of an event that leads
	 * to a log entry. Equally, there is no need to undo such changes - for example
	 * where a {@link Changeable} represents collected data, there is no need to remove
	 * such data when undoing other changes - having the extra data available is
	 * always desirable
	 */
	public final static String DO_NOT_UNDO = "Do not undo";
	
	/**
	 * Get the features used to interact with a {@link Changeable}
	 * @return
	 * 		The {@link Changeable}'s features
	 */
	public ChangeableFeatures features();

}
