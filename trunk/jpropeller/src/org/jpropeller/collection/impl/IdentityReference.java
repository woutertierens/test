package org.jpropeller.collection.impl;

import org.jpropeller.properties.change.Immutable;

/**
 * Stores a value.
 * Additionally, is equal only to other IdentityReferences 
 * with identical values (identical implies a==b, not a.equals(b)) 
 *
 * @param <T>		The type of value
 */
public class IdentityReference<T> implements Immutable {

	private final T value;
	
	/**
	 * Make an {@link IdentityReference}
	 * @param value		Our contents
	 */
	public IdentityReference(T value) {
		this.value = value;
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
			if (obj instanceof IdentityReference<?>) {
				IdentityReference<?> other = (IdentityReference<?>) obj;
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
		Object value = get();
		
		//Null is assigned code 0
		if (value == null) {
			return 0;
		}
		
		//We default to system identity hash code
		int code = System.identityHashCode(value);
		
		//Only null will be given code 0 - override to
		//1 if this is not the case. Seems likely that
		//identity hash code will not be 0 for non-null,
		//but we want to be sure
		if (value != null && code == 0) {
			code = 1;
		}
		
		return code;
	}
	
	/**
	 * Get the value, the object we contain contents
	 * @return	value
	 */
	public T get() {
		return value;
	}
	
}
