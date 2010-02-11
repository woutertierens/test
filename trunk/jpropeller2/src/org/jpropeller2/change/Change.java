package org.jpropeller2.change;

import org.jpropeller2.box.Box;
import org.jpropeller2.view.View;

/**
 * Carries details of a change to data in a {@link Box}
 * Subinterfaces may give more information about changes
 * to specific {@link Box} types.
 */
public interface Change {
	
	/**
	 * This is true if any data object accessible immediately from
	 * the changed {@link Box} is no longer the same instance it was
	 * before the change occurred.
	 * So for example, we might have a "Person" {@link Box} contains an "Address"
	 * {@link Box}, and that Address {@link Box} contains a "street"
	 * string.
	 * Now imagine we have a person Bob, with address as a particular instance
	 * of Address "a". If the street string contained within "a" changes, then
	 * Bob will have a change, since his address has changed. However it is 
	 * still the same instance, "a", and so {@link #instancesChanged()} will
	 * be true.
	 * However if we set a completely new Address instance, "b", into Bob, then
	 * Bob will have a change where {@link #instancesChanged()} is true.
	 * This distinction is mainly useful for optimisations, where some {@link View}s
	 * may only need to perform a full refresh if instances change, and so can 
	 * be optimised when instances stay the same. 
	 * @return
	 */
	public boolean instancesChanged();
	
}
