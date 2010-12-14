package org.jpropeller.view.info;

import org.joda.time.DateTime;
import org.jpropeller.properties.Prop;
import org.jpropeller.transformer.PathStep;

/**
 * Interface for objects with a significant
 * {@link DateTime} at which they were received
 */
public interface Received {

	/**
	 * The {@link DateTime} of (original) reception of the object.
	 * @return	Reception {@link DateTime}
	 */
	public abstract Prop<DateTime> receptionTime();

	/**
	 * {@link PathStep} to {@link #receptionTime()}
	 */
	public final static PathStep<Received, DateTime> toReceptionTime = new PathStep<Received, DateTime>() {
		public org.jpropeller.properties.Prop<DateTime> transform(Received s) { return s.receptionTime(); };
	};

}