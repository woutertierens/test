package org.jpropeller.system;

import org.jpropeller.system.impl.PropSystemDefault;

/**
 * Central source for System-wide aspects of JPropeller 
 */
public class Props {

	private static PropSystem propSystem = new PropSystemDefault();

	/**
	 * Get the system-wide {@link PropSystem}
	 * @return
	 * 		The {@link PropSystem}
	 */
	public static PropSystem getPropSystem() {
		return propSystem;
	}
	
}
