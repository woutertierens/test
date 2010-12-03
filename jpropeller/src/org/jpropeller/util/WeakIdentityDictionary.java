package org.jpropeller.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements a similar interface to {@link Map}, but much simpler.
 * 
 * Stores mappings from keys to values, where keys are compared by identity (==)
 * rather than normal equality ({@link Object#equals(Object)})
 * 
 * Keys are held with {@link WeakReference}s, and so may be GCed.
 * 
 * Values are held strongly, and are expected only to be present in this map, so
 * that they may also be GCed.
 * 
 * Thread safe
 * 
 * @param <K>
 *            The type of key
 * @param <V>
 *            The type of value
 */
public class WeakIdentityDictionary<K, V> {

	private final Map<WeakReference<K>, V> map = new HashMap<WeakReference<K>, V>();
	private final ReferenceQueue<K> refQueue = new ReferenceQueue<K>();

	/**
	 * Get the value associated with the key, or null if no value is associated.
	 * 
	 * @param key
	 *            The key, must not be null
	 * @return The associated value, or null
	 */
	public synchronized V get(K key) {
		clearQueue();
		if (key == null) {
			throw new IllegalArgumentException("Null key");
		}
		WeakReference<K> keyref = makeReference(key);
		return map.get(keyref);
	}

	/**
	 * Set the value associated with the key
	 * 
	 * @param key
	 *            The key, must not be null
	 * @param value
	 *            The value to be associated with the key, replacing any
	 *            existing value
	 * @return The old value (if there was one), or null otherwise
	 */
	public synchronized V put(K key, V value) {
		clearQueue();
		if (key == null) {
			throw new IllegalArgumentException("Null key");
		}
		//Note we use the reference queue only here, since we only
		//care about these references being GCed. Others are just
		//used temporarily for lookup purposes.
		WeakReference<K> keyref = makeReference(key, refQueue);
		return map.put(keyref, value);
	}

	/**
	 * Remove the value associated with the key
	 * 
	 * @param key
	 *            The key, must not be null
	 * @return The old value (if there was one), or null otherwise
	 */
	public synchronized V remove(K key) {
		clearQueue();
		if (key == null) {
			throw new IllegalArgumentException("Null key");
		}
		WeakReference<K> keyref = makeReference(key);
		return map.remove(keyref);
	}

	//Remove the mappings for all collected references
	private void clearQueue() {
		Reference<? extends K> ref;
		while ((ref = refQueue.poll()) != null) {
			map.remove(ref);
		}
	}

	private WeakReference<K> makeReference(K referent) {
		return new IdentityWeakReference<K>(referent);
	}

	private WeakReference<K> makeReference(K referent, ReferenceQueue<K> q) {
		return new IdentityWeakReference<K>(referent, q);
	}

	private static class IdentityWeakReference<T> extends WeakReference<T> {

		private final int hashCode;
		
		IdentityWeakReference(T o) {
			this(o, null);
		}

		IdentityWeakReference(T o, ReferenceQueue<T> q) {
			super(o, q);
			if (o == null) {
				throw new IllegalArgumentException("Null value");
			}
			this.hashCode = System.identityHashCode(o);
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			//We know we are only used within this class,
			//so will only be compared to other IdentityWeakReferences
			IdentityWeakReference<?> wr = (IdentityWeakReference<?>) o;
			return (get() == wr.get());
		}

		public int hashCode() {
			return hashCode;
		}
	}
}
