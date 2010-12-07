package org.jpropeller.view.info;

import org.jpropeller.properties.Prop;

/**
 * An object that may have a {@link String} note about it.
 */
public interface Notable {

	/**
	 * A (potentially long, multiline) note about the
	 * object. Intended to be user edited, used for additional
	 * free-forminformation about the object.
	 * A whitespace-only note should be considered by convention
	 * to be the same as "not having a note", to make user editing
	 * easier. Should NOT be null to indicate no note, "" is the canonical
	 * way to indicate this, and as descrived above any whitespace-only string
	 * should be considered as blank.
	 * @return	Note {@link String}, not null.
	 */
	public Prop<String> note();
	
}