package org.jpropeller.collection.impl;

import java.util.Iterator;

import org.jpropeller.properties.change.Changeable;
import org.jpropeller.system.Props;

/**
 *	Wraps an iterator, and makes main class fire property change (complete list
 *	change) when remove is used, and adds appropriate locking.
 *	@param <T> The type of element 
 */
public class ImmutableCCollectionIterator<T> implements Iterator<T>{
	
	//The wrapped iterator
	final Iterator<T> it;
	final Changeable user;
	
	/**
	 * Make a wrapper 
	 * @param it 		The iterator to wrap
	 * @param user 		The user of this IteratorShell
	 */
	public ImmutableCCollectionIterator(Iterator<T> it, Changeable user) {
		this.it = it;
		this.user = user;
	}

	//Method must ensure listbean compliance
	public void remove() {
		throw new UnsupportedOperationException("Cannot remove from IteratorShell");
	}
	
	//Methods delegated directly to the wrapped iterator
	public boolean equals(Object obj) {
		Props.getPropSystem().getChangeSystem().prepareRead(user);
		try {
			return it.equals(obj);
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(user);
		}
	}
	public int hashCode() {
		Props.getPropSystem().getChangeSystem().prepareRead(user);
		try {
			return it.hashCode();
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(user);
		}
	}
	public boolean hasNext() {
		Props.getPropSystem().getChangeSystem().prepareRead(user);
		try {
			return it.hasNext();
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
	public String toString() {
		Props.getPropSystem().getChangeSystem().prepareRead(user);
		try {
			return it.toString();
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(user);
		}
	}
}