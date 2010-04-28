package org.jpropeller.util;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Very simple {@link Target} implementation - synchronises access
 * to a single storage slot, in which the first non-null value put to the
 * {@link Target} is stored.
 * @param <T> The type of value accepted and stored.
 */
public class TargetSlot<T> implements Target<T> {

	AtomicReference<T> ref = new AtomicReference<T>();
	
	@Override
	public void put(T instance) {
		//Only accept first value
		ref.compareAndSet(null, instance);
	}
	
	/**
	 * Get the first non-null value put into this {@link Target}, or null
	 * if no non-null value has yet been put.
	 * @return	First non-null value put, or null.
	 */
	public T get() {
		return ref.get();
	}

}
