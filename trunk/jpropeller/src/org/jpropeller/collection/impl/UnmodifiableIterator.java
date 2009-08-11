package org.jpropeller.collection.impl;

import java.util.Iterator;

/**
 * Wraps a delegate {@link Iterator} to stop it supporting
 * element removal
 * @param <T>		Type of iterated data
 */
public class UnmodifiableIterator<T> implements Iterator<T> {

	Iterator<T> delegate;
	
	/**
	 * Make an iterator
	 * @param delegate		The delegate {@link Iterator}
	 */
	public UnmodifiableIterator(Iterator<T> delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public boolean hasNext() {
		return delegate.hasNext();
	}

	@Override
	public T next() {
		return delegate.next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Cannot remove acquisitions");
	}
	
}