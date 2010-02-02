package org.jpropeller.view.info;

import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.transformer.PathStep;
import org.jpropeller.view.View;

/**
 * {@link Lockable} instances have a {@link Boolean} {@link Prop} 
 * indicating whether they are locked.
 * {@link Lockable} should be respected where possible by {@link View}s,
 * which should not allow editing of {@link Lockable} instances while
 * they are locked.
 */
public interface Lockable {

	/**
	 * Locked property, true when the {@link Lockable} is locked,
	 * false when it is unlocked.
	 * @return Locked property
	 */
	public Prop<Boolean> locked();
	
	/**
	 * The {@link PropName} for {@link #locked()} property
	 */
	public final static PropName<Boolean> LOCKED = PropName.create(Boolean.class, "locked"); 

	/**
	 * Path to {@link #locked()}
	 */
	public final static PathStep<Lockable, Boolean> toLocked = new PathStep<Lockable, Boolean>() {
		@Override
		public Prop<Boolean> transform(Lockable s) {
			return s.locked();
		}
	};

}
