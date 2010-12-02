package org.jpropeller.view.info;

import org.joda.time.DateTime;
import org.jpropeller.properties.Prop;

/**
 * Interface for objects with a significant creation
 * {@link DateTime}
 */
public interface Created {

	/**
	 * The {@link DateTime} of (original) creation of the object.
	 * @return	Creation {@link DateTime}
	 */
	public abstract Prop<DateTime> creationTime();

}