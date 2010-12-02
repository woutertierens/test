package org.jpropeller.view.info;

import org.joda.time.DateTime;
import org.jpropeller.properties.Prop;
import org.jpropeller.transformer.PathStep;

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

	/**
	 * {@link PathStep} to {@link #creationTime()}
	 */
	public final static PathStep<Created, DateTime> toCreationTime = new PathStep<Created, DateTime>() {
		public org.jpropeller.properties.Prop<DateTime> transform(Created s) { return s.creationTime(); };
	};

}