package org.jpropeller.view.info;

import org.joda.time.DateTime;
import org.jpropeller.properties.Prop;

/**
 * Interface for objects with a single 
 * {@link DateTime} timestamp
 */
public interface TimeStamped {

	/**
	 * The {@link DateTime} at which object was stamped.
	 * @return	{@link DateTime}
	 */
	public abstract Prop<DateTime> timeStamp();

}