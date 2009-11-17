package org.jpropeller.util;

/**
 * A pair of objects
 *
 * @param <A>	Type of first element
 * @param <B>	Type of second element
 */
public class Pair<A, B> {
	private final A a;
	private final B b;
	
	/**
	 * Create a pair
	 * @param a		The first element
	 * @param b		The second element
	 */
	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}
	
	/**
	 * First element
	 * @return a
	 */
	public A a() {
		return a;
	}
	
	/**
	 * Second element
	 * @return b
	 */
	public B b() {
		return b;
	}
	
}
