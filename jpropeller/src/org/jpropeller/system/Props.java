package org.jpropeller.system;

import org.jpropeller.properties.change.ChangeSystem;
import org.jpropeller.system.impl.PropSystemDefault;

/**
 * Central source for System-wide aspects of JPropeller 
 */
public class Props {

	//Static class
	private Props(){}
	
	private static PropSystem propSystem = new PropSystemDefault();
	
	/**
	 * Get the system-wide {@link PropSystem}, this must not
	 * change after it is first called.
	 * @return
	 * 		The {@link PropSystem}
	 */
	public static PropSystem getPropSystem() {
		return propSystem;
	}
	
	/**
	 * Convenience method for {@link ChangeSystem#acquire()} on the
	 * {@link ChangeSystem}
	 */
	public static void acquire() {
		getPropSystem().getChangeSystem().acquire();
	}

	/**
	 * Convenience method for {@link ChangeSystem#release()} on the
	 * {@link ChangeSystem}
	 */
	public static void release() {
		getPropSystem().getChangeSystem().release();
	}

}
