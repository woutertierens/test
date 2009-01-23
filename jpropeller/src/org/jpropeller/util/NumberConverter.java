package org.jpropeller.util;

/**
 * Converts between {@link Number}s and instances of a more
 * specific subclass T of {@link Number} (e.g. Double, Integer etc.)
 * @author bwebster
 *
 * @param <T>
 * 		The specific type of {@link Number} to be converted
 */
public interface NumberConverter<T extends Number> {

	/**
	 * Convert a T to a {@link Number}
	 * @param t
	 * 		The value to convert
	 * @return
	 * 		t as a {@link Number}
	 */
	public Number toNumber(T t);

	/**
	 * Convert a {@link Number} to a T
	 * @param n
	 * 		The {@link Number} to convert
	 * @return
	 * 		n as a T
	 */
	public T toT(Number n);
	
}
