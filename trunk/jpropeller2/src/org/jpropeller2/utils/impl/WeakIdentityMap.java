package org.jpropeller2.utils.impl;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Implements some enough of {@link Map} to allow
 * for maintaining values associated with keys without
 * retaining the keys.
 * Keys cannot be null.
 * {@link #entrySet()}, {@link #keySet()}, {@link #values()}
 * and {@link #putAll(Map)} are not supported.
 *
 * @param <K>		The type of key
 * @param <V>		The type of value
 * 
 * NOT thread safe.
 * 
 * @param <T>		The type of object counted
 */
public class WeakIdentityMap<V> implements Map<Object, V>{

	private final ReferenceQueue<Object> queue = new ReferenceQueue<Object>();
	
	/**
	 * Map from {@link WeakIdentityReference}s to counts for the
	 * referenced instance. 
	 */
	private final Map<IWRef, V> core;
	
	/**
	 * Create a new {@link WeakIdentityMap}
	 */
	public WeakIdentityMap() {
		this(16);
	}
	
	/**
	 * Create a new {@link WeakIdentityMap}
	 * @param initialCapacity		The initial instance capacity
	 */
	public WeakIdentityMap(int initialCapacity) {
		core = new HashMap<IWRef, V>(initialCapacity);
	}
	
	private synchronized void clearQueue() {
        Object collected;
        while ((collected = queue.poll())!= null) {
            core.remove((IWRef)collected);
            collected = queue.poll();
        }
    }

    @Override
    public boolean equals(Object o) {
    	if (o instanceof WeakIdentityMap<?>) {
    		return core.equals(((WeakIdentityMap<?>)o).core);
    	} else {
    		return false;
    	}
    }
    
    @Override
    public int hashCode() {
    	clearQueue();
    	return core.hashCode();
    }
    
    @Override
    public V get(Object key) {
        clearQueue();
        return core.get(new IWRef(key));
    }
    
    @Override
    public V put(Object key, V value) {
        clearQueue();
        return core.put(new IWRef(key), value);
    }

    @Override
    public boolean isEmpty() {
        clearQueue();
        return core.isEmpty();
    }
    
    @Override
    public V remove(Object key) {
        clearQueue();
        return core.remove(new IWRef(key));
    }
    
    @Override
    public int size() {
        clearQueue();
        return core.size();
    }
	
    @Override
    public void clear() {
        core.clear();
        clearQueue();
    }

    @Override
    public boolean containsKey(Object key) {
        clearQueue();
        return core.containsKey(new IWRef(key));
    }

    @Override
    public boolean containsValue(Object value)  {
        clearQueue();
        return core.containsValue(value);
    }
    
    @Override
    public Set<Map.Entry<Object, V>> entrySet() { throw new UnsupportedOperationException(); }
    @Override
    public Set<Object> keySet() { throw new UnsupportedOperationException(); }
	@Override
	public void putAll(Map<? extends Object, ? extends V> m) { throw new UnsupportedOperationException(); }
	@Override
	public Collection<V> values() { throw new UnsupportedOperationException(); }

    class IWRef extends WeakReference<Object> {
        int hashCode;
        
        IWRef(Object obj) {
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
            IWRef ref = (IWRef)o;
            return (this.get() == ref.get());
        }
    }
}
