package org.jpropeller.view.info;

import org.joda.time.DateTime;
import org.jpropeller.properties.Prop;

/**
 * Interface for objects with a significant start
 * {@link DateTime}
 */
public interface Started {

	/**
	 * The {@link DateTime} when object was "started", may be
	 * null if object has not yet started.
	 * @return	Start {@link DateTime}
	 */
	public abstract Prop<DateTime> startTime();

}