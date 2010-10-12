package org.jpropeller.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jpropeller.collection.impl.IdentityReference;

/**
 * Maintains counts associated with instances,
 * only keeping weak links to those instances so that
 * they may still be garbage collected.
 * 
 * NOT thread safe.
 * 
 * @param <T>           The type of object counted
 */
public class WeakReferenceCounter<T> implements Iterable<T> {

	private final ReferenceQueue<T> queue = new ReferenceQueue<T>();

	/**
	 * Map from {@link IdentityReference}s to counts for the
	 * referenced instance. 
	 */
	private final Map<IWRef, Integer> counts;

	/**
	 * Create a new {@link WeakReferenceCounter}
	 */
	public WeakReferenceCounter() {
		this(16);
	}

	/**
	 * Create a new {@link WeakReferenceCounter}
	 * @param initialCapacity               The initial instance capacity
	 */
	public WeakReferenceCounter(int initialCapacity) {
		counts = new HashMap<IWRef, Integer>(initialCapacity);
	}

	private synchronized void clearQueue() {
		Object collected;
		while ((collected = queue.poll())!= null) {
			counts.remove(collected);
			collected = queue.poll();
		}
	}

	/**
	 * Add to the count for an instance
	 * @param instance      The instance
	 * @return                      The new count for the instance - 1 if
	 *                                      it has just been added for the first time.
	 */
	public int add(T instance) {

		clearQueue();

		IWRef ir = new IWRef(instance);

		//Get current count
		Integer count = counts.get(ir);

		//No mapping means 0 count
		if (count == null) {
			count = 0;
		}

		//We increment count
		count++;

		//Put count (back)
		counts.put(ir, count);

		return count;
	}

	/**
	 * Decrement the count for an instance
	 * @param instance      The instance
	 * @return                      The new count for the instance: 0 if it has
	 *                                      just been removed completely. -1 if it did not
	 *                                      have a count (that is, if it is being removed
	 *                                      but was not present)
	 */
	public int remove(T instance) {

		clearQueue();

		IWRef ir = new IWRef(instance);

		//Get current count
		Integer count = counts.get(ir);

		//No mapping, so just return -1
		if (count == null) {
			return -1;
		}

		//Decrement count
		count--;

		//Put count (back) if it is positive, otherwise clear
		if (count > 0) {
			counts.put(ir, count);
		} else {
			counts.remove(ir);
		}

		return count;
	}

	/**
	 * Clear all counts
	 */
	public void clear() {
		counts.clear();
		clearQueue();
	}

	/**
	 * Iterator over all instances with a count
	 * @return An iterator of counted instances
	 */
	@Override
	public Iterator<T> iterator() {
		clearQueue();
		//We just unpack the identity references
		final Iterator<IWRef> it = counts.keySet().iterator();
		return new UnpackingIterator(it);
	}

	/**
	 * Unpacks values from {@link WeakIdentityReference}s, and filters
	 * out unpacked null values (this occurs when the values are
	 * garbage collected).
	 * It also removes elements that refer to null, since they serve
	 * no purpose.
	 */
	private class UnpackingIterator implements Iterator<T> {

		private final Iterator<IWRef> it;
		boolean hasNext = false;
		T next = null;

		private UnpackingIterator(Iterator<IWRef> it) {
			this.it = it;
			seekNext();
		}

		@Override
		public boolean hasNext() {
			return hasNext;
		}

		@Override
		public T next() {
			T toReturn = next;
			seekNext();
			return toReturn;
		}

		private void seekNext() {
			boolean found = false;

			//Keep reading from underlying iterator until
			//we find the next non-null element, or run out 
			//of elements in underlying iterator
			while (!found && it.hasNext()) {
				next = it.next().get();
				if (next != null) {
					found = true;
					//If the reference is to null, has been garbage collected
					//so we must try next, and we should also clear out the
					//useless reference from the map
				} else {
					it.remove();
				}
			}

			//We have another element if we found one
			hasNext = found;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Cannot remove from WeakReferenceCounter's iterator");
		}

	}

	/** 
	 * A {@link WeakReference} that compares by identicality to other {@link IWRef}s
	 */
	class IWRef extends WeakReference<T> {
		int hashCode;

		IWRef(T obj) {
			super(obj, queue);
			hashCode = System.identityHashCode(obj);
		}

		public int hashCode() {
			return hashCode;
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			//We know that we only compare IWRefs to themselves, within Counter
			@SuppressWarnings("unchecked")
			IWRef ref = (IWRef)o;
			return (this.get() == ref.get());
		}
	}
}
