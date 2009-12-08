package org.jpropeller.collection.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.jpropeller.properties.change.Changeable;
import org.jpropeller.system.Props;

/**
 * Wraps a list to make it unmodifiable (so tracking is preserved),
 * and also performs appropriate locking on reading.
 * Passes on required properties to sublists and iterators.
 * @param <E>		The type of element 
 */
public class ImmutableCCollectionSublist<E> implements List<E> {
	private final Changeable user;
	private final List<E> coreList;
	
	/**
	 * Create a new {@link ImmutableCCollectionSublist}
	 * @param lockedList	The list we will wrap
	 * @param user			The user of this sublist - the source of the data
	 */
	public ImmutableCCollectionSublist(List<E> lockedList, Changeable user) {
		this.coreList = Collections.unmodifiableList(lockedList);
		this.user = user;
	}
	
	//These methods are complex - we need to make the returned iterators/sublists
	//also perform locking
	
	//Iterators use the same shell as the parent list
	public Iterator<E> iterator() {
		Props.getPropSystem().getChangeSystem().prepareRead(user);
		try {
			return new ImmutableCCollectionIterator<E>(coreList.iterator(), user);
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(user);
		}
	}
	public ListIterator<E> listIterator() {
		Props.getPropSystem().getChangeSystem().prepareRead(user);
		try {
			return new ImmutableCCollectionListIterator<E>(coreList.listIterator(), user);
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(user);
		}
	}
	public ListIterator<E> listIterator(int index) {
		Props.getPropSystem().getChangeSystem().prepareRead(user);
		try {
			return new ImmutableCCollectionListIterator<E>(coreList.listIterator(index), user);
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(user);
		}
	}
	
	//Make another locking list around sublist, so it gives the same behaviour.
	//Note that the wrapping isn't recursive - there should just be one LockedList
	//wrapper around each list, even when getting subList().subList().subList() etc.
	public List<E> subList(int fromIndex, int toIndex) {
		Props.getPropSystem().getChangeSystem().prepareRead(user);
		try {
			return new ImmutableCCollectionSublist<E>(coreList.subList(fromIndex, toIndex), user);
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(user);
		}
	}
	
	
	//These methods are all simple - just lock around them
	public boolean contains(Object o) {
		Props.getPropSystem().getChangeSystem().prepareRead(user);
		try {
			return coreList.contains(o);
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(user);
		}
	}
	public boolean containsAll(Collection<?> c) {
		Props.getPropSystem().getChangeSystem().prepareRead(user);
		try {
			return coreList.containsAll(c);
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(user);
		}
	}
	public boolean equals(Object o) {
		Props.getPropSystem().getChangeSystem().prepareRead(user);
		try {
			return coreList.equals(o);
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(user);
		}
	}
	public E get(int index) {
		Props.getPropSystem().getChangeSystem().prepareRead(user);
		try {
			return coreList.get(index);
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(user);
		}
	}
	public int hashCode() {
		Props.getPropSystem().getChangeSystem().prepareRead(user);
		try {
			return coreList.hashCode();
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(user);
		}
	}
	public int indexOf(Object o) {
		Props.getPropSystem().getChangeSystem().prepareRead(user);
		try {
			return coreList.indexOf(o);
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(user);
		}
	}
	public boolean isEmpty() {
		Props.getPropSystem().getChangeSystem().prepareRead(user);
		try {
			return coreList.isEmpty();
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(user);
		}
	}
	public int lastIndexOf(Object o) {
		Props.getPropSystem().getChangeSystem().prepareRead(user);
		try {
			return coreList.lastIndexOf(o);
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(user);
		}
	}
	public int size() {
		Props.getPropSystem().getChangeSystem().prepareRead(user);
		try {
			return coreList.size();
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(user);
		}
	}
	public Object[] toArray() {
		Props.getPropSystem().getChangeSystem().prepareRead(user);
		try {
			return coreList.toArray();
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(user);
		}
	}
	public <T> T[] toArray(T[] a) {
		Props.getPropSystem().getChangeSystem().prepareRead(user);
		try {
			return coreList.toArray(a);
		} finally {
			Props.getPropSystem().getChangeSystem().concludeRead(user);
		}
	}
	
	//Methods that modify the list will all fail due to unmodifiable view
	public boolean add(E e) {
		return coreList.add(e);
	}
	public void add(int index, E element) {
		coreList.add(index, element);
	}
	public boolean addAll(Collection<? extends E> c) {
		return coreList.addAll(c);
	}
	public boolean addAll(int index, Collection<? extends E> c) {
		return coreList.addAll(index, c);
	}
	public void clear() {
		coreList.clear();
	}
	public E remove(int index) {
		return coreList.remove(index);
	}
	public boolean remove(Object o) {
		return coreList.remove(o);
	}
	public boolean removeAll(Collection<?> c) {
		return coreList.removeAll(c);
	}
	public boolean retainAll(Collection<?> c) {
		return coreList.retainAll(c);
	}
	public E set(int index, E element) {
		return coreList.set(index, element);
	}
	
}
