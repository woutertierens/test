package org.jpropeller.view.info;

import org.joda.time.DateTime;
import org.jpropeller.properties.Prop;
import org.jpropeller.transformer.PathStep;

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
	
	/**
	 * {@link PathStep} to {@link #timeStamp()}
	 */
	public final static PathStep<TimeStamped, DateTime> toTimeStamp = new PathStep<TimeStamped, DateTime>() {
		public org.jpropeller.properties.Prop<DateTime> transform(TimeStamped s) { return s.timeStamp(); };
	};


}