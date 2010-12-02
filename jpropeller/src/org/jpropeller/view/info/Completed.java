package org.jpropeller.view.info;

import org.joda.time.DateTime;
import org.jpropeller.properties.Prop;
import org.jpropeller.transformer.PathStep;

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

	/**
	 * {@link PathStep} to {@link #completionTime()}
	 */
	public final static PathStep<Completed, DateTime> toCompletionTime = new PathStep<Completed, DateTime>() {
		public org.jpropeller.properties.Prop<DateTime> transform(Completed s) { return s.completionTime(); };
	};

}