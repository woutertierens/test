package org.jpropeller.view.info;

import org.joda.time.DateTime;
import org.jpropeller.properties.Prop;
import org.jpropeller.transformer.PathStep;

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

	/**
	 * {@link PathStep} to {@link #startTime()}
	 */
	public final static PathStep<Started, DateTime> toStartTime = new PathStep<Started, DateTime>() {
		public org.jpropeller.properties.Prop<DateTime> transform(Started s) { return s.startTime(); };
	};
	
}