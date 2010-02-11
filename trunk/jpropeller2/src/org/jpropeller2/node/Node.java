package org.jpropeller2.node;

import org.jpropeller2.box.Box;
import org.jpropeller2.ref.Ref;

/**
 * Groups named {@link Ref}s
 * Each {@link Ref} is accessible by name, 
 * and is within this {@link Box}.
 * Names of {@link Ref}s are {@link Iterable}.
 * The contained {@link Ref}s do NOT change after creation,
 * only their contents. 
 */
public interface Node extends Box, Iterable<String> {

	/**
	 * Get a named {@link Ref}
	 * @param name		The name of the {@link Ref}
	 * @return			The {@link Ref}
	 */
	public Ref<?> get(String name);
	
}
