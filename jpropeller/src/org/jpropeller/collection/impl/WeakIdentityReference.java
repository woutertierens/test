package org.jpropeller.collection.impl;

import java.lang.ref.WeakReference;

import org.jpropeller.properties.change.Immutable;

/**
 * Stores a {@link WeakReference} to a value.
 * Value cannot be null, since this is incompatible with {@link WeakReference} (we
 * cannot tell the difference between the value having been garbage collected,
 * and a null value).
 * Additionally, is equal only to other {@link WeakIdentityReference}s 
 * with identical values (identical implies a==b, not a.equals(b)) 
 *
 * @param <T>		The type of value
 */
public class WeakIdentityReference<T> implements Immutable {

	private final WeakReference<T> value;
	private final int hashCode;
	
	/**
	 * Make an {@link WeakIdentityReference}
	 * @param value		Our contents, CANNOT BE NULL.
	 * @throws IllegalArgumentException		If the value is null
	 */
	public WeakIdentityReference(T value) {
		if (value == null) throw new IllegalArgumentException("WeakIdentityReference cannot have a null value");
		this.value = new WeakReference<T>(value);
		hashCode = System.identityHashCode(value);
	}
	
	//We are equal only to IdentityReferences with
	//identical (==) values.
	@Override
	public boolean equals(Object obj) {
		//Same reference is always to same object
		if (obj == this) {
			return true;
			
		//If other reference is null, we can't be equal
		} else if (obj == null) {
			return false;
			
		} else {
			if (obj instanceof WeakIdentityReference<?>) {
				WeakIdentityReference<?> other = (WeakIdentityReference<?>) obj;
				Object otherObject = other.get();
				Object thisObject = get();
				//We allow null==null
				return thisObject == otherObject;
			} else {
				return false;
			}
		}
	}
	
	@Override
	public int hashCode() {
		//We always have the system identity hash of the value,
		//even after the value has been garbage collected
		return hashCode;
	}
	
	/**
	 * Get the value, the object we contain contents
	 * @return	value
	 */
	public T get() {
		return value.get();
	}
	
}
