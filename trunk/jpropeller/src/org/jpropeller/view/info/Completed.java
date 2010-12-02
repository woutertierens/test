package org.jpropeller.view.info;

import org.joda.time.DateTime;
import org.jpropeller.properties.Prop;

/**
 * Interface for objects with a significant completion
 * {@link DateTime}
 */
public interface Completed {

	/**
	 * The {@link DateTime} of (original) completion of the object.
	 * May be null if object is not yet completed.
	 * @return	Completion {@link DateTime}
	 */
	public abstract Prop<DateTime> completionTime();

}