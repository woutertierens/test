package org.jpropeller.collection;

import java.util.Map;

/**
 * Stores detail of a single contiguous (or complete) 
 * change to the mappings in a {@link Map}
 */
public interface MapDelta {
	
	/**
	 * @return The size of the map before the change
	 * For a COMPLETE change this may be -1 indicating no knowledge of
	 * old size
	 */
	public int getOldSize();
	
	/**
	 * @return The size of the map after the change
	 */
	public int getNewSize();
	
	/**
	 * @return The type of change
	 */
	public CollectionChangeType getType();

	/**
	 * If the change is an {@link CollectionChangeType#ALTERATION},
	 * {@link CollectionChangeType#INSERTION} or {@link CollectionChangeType#DELETION} it
	 * may apply only to the mapping from a single key - in this case, this method
	 * will return true, and the value of {@link MapDelta#getKey()} may be used. If
	 * this method returns false, the value of that key should never be used.
	 * Note that while a change applying to a single key MAY be marked with that
	 * key, it may not - that is, a {@link MapDelta} may OVERspecify the change,
	 * appearing to indicate that more has changed than actually has. (Of course it
	 * should never UNDERspecify)
	 * @return
	 * 		True if key may be used to find a single changed mapping
	 */
	public boolean isKeyValid();
	
	/**
	 * If the change is an {@link CollectionChangeType#ALTERATION},
	 * {@link CollectionChangeType#INSERTION} or {@link CollectionChangeType#DELETION} it
	 * may apply only to the mapping from a single key - in this case, this method
	 * will return the key in that mapping, and {@link MapDelta#isKeyValid()} will
	 * return true.
	 * If this method returns false, the value of that key should never be used.
	 * Note that while a change applying to a single key MAY be marked with that
	 * key, it may not - that is, a {@link MapDelta} may OVERspecify the change,
	 * appearing to indicate that more has changed than actually has. (Of course it
	 * should never UNDERspecify)
	 * @return
	 * 		The key used to make the change, OR null if multiple
	 * mappings are affected, or if the single key for the change is not known
	 */
	public Object getKey();
}
