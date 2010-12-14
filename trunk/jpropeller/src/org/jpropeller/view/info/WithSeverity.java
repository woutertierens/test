package org.jpropeller.view.info;

import org.jpropeller.properties.Prop;

/**
 * An object that may have a {@link Severity} associated
 * with it.
 */
public interface WithSeverity {

	/**
	 * The {@link Severity} associated with this object,
	 * or null if there is no associated {@link Severity}.
	 * @return	A {@link Severity} or null.
	 */
	public Prop<Severity> severity();
	
}