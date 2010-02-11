package org.jpropeller2.ref;

import java.awt.Color;

/**
 * A reference to a single value
 * @param <T>	The type of valie
 */
public interface Ref<T> {
	
	/**
	 * Get the value
	 * @return	The value
	 */
	public T get();
	
	/**
	 * Set the value
	 * @param t		The value to set
	 */
	public void set(T t);
	
	/**
	 * The {@link Class} of values in the
	 * {@link Ref}, where this {@link Class}
	 * exists, or null.
	 * 
	 * Returning null is valid for any {@link Ref},
	 * and indicates that it is not expected to be
	 * useful to introspect on the value {@link Class}.
	 * 
	 * In one case, it is impossible to return anything
	 * other than null - when T is itself a generic
	 * type, in which case there is nothing accepted
	 * as a Class<T> except null - thanks to Sun's
	 * questionable decision on type erasure.
	 * 
	 * It generally IS useful to return a {@link Class}
	 * for any {@link Ref} containing a simple 
	 * type like a primitive wrapper ({@link Integer},
	 * {@link Double}, etc.) or similar simple
	 * type like {@link String}, {@link Color}. This
	 * is because automatic UI generation can easily
	 * work with these types to give a useful display.
	 * 
	 * It is expected that default implementations of
	 * {@link Ref} will provide convenience methods for creating
	 * {@link Ref}s with these simple types without having
	 * to pass the class, by overloading a method with
	 * the different content types.
	 * 
	 * It may be useful to supply a {@link Class} for more
	 * complex but non-generic types, but actually it's
	 * generally not THAT useful, since for these classes
	 * it's probably more common for an automatic UI to
	 * just inspect the actual value of the {@link Ref}
	 * to establish type.
	 * 
	 * @return	{@link Class} of values
	 */
	public Class<T> valueClass(); 
}
