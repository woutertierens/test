package org.jpropeller.collection.impl;

import java.util.ListIterator;

import org.jpropeller.properties.change.Changeable;
import org.jpropeller.system.Props;

/**
 *	Wraps an iterator, making mutator methods unsupported and
 *  adding appropriate locking when state might be read.
 * @param <T>		The type of element 
 */
public class ImmutableCCollectionListIterator<T> implements ListIterator<T> {

	private final ListIterator<T> it;
	private final Changeable user;

	/**
	 * Make a wrapper 
	 * @param it 		The iterator to wrap
	 * @param user		The {@link Changeable} using this iterator
	 */
	public ImmutableCCollectionListIterator(ListIterator<T> it, Changeable user) {
		super();
		this.it = it;
		this.user = user;
	}
	
	
	//Methods must ensure listbean compliance

	public void add(final T e) {
		throw new UnsupportedOperationException("Cannot add to ImmutableCCollectionListIterator");
	}

	public void remove() {
		throw new UnsupportedOperationException("Cannot remove from ImmutableCCollectionListIterator");
	}

	public void set(final T e) {
		throw new UnsupportedOperationException("Cannot set element of ImmutableCCollectionListIterator");
	}
	
	//Methods delegated directly to the wrapped iterator
	public boolean hasNext() {
		Props.getPropSystem().getChangeSystem().prepareRead(user);
		try {
			return it.hasNext();
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(user);
		}
	}
	public boolean hasPrevious() {
		Props.getPropSystem().getChangeSystem().prepareRead(user);
		try {
			return it.hasPrevious();
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(user);
		}
	}
	public T next() {
		Props.getPropSystem().getChangeSystem().prepareRead(user);
		try {
			return it.next();
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(user);
		}
	}
	public int nextIndex() {
		Props.getPropSystem().getChangeSystem().prepareRead(user);
		try {
			return it.nextIndex();
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(user);
		}
	}
	public T previous() {
		Props.getPropSystem().getChangeSystem().prepareRead(user);
		try {
			return it.previous();
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(user);
		}
	}
	public int previousIndex() {
		Props.getPropSystem().getChangeSystem().prepareRead(user);
		try {
			return it.previousIndex();
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(user);
		}
	}
	
}
