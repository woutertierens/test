package org.jpropeller.collection.impl;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jpropeller.collection.CList;
import org.jpropeller.properties.calculated.impl.CalculatedProp;
import org.jpropeller.properties.change.Changeable;

/**
 * A {@link Set} based on comparison of contents by identicality. As
 * for a normal {@link Set}, this {@link Set} cannot contain "duplicate" contents.
 * However in a normal set two objects are considered to be duplicates if
 * they are equal according to {@link Object#equals(Object)}, using hash codes
 * from {@link Object#hashCode()}. In this {@link Set}, two objects are considered
 * duplicates if and only if they are identical, that is if (a==b).
 * 
 * This is based on, and very similar to, {@link IdentityHashMap} - see the
 * docs for that class for a further discussion of equality versus identicality.
 * The restrictions and advice for use of this class are the same as for {@link IdentityHashMap} - 
 * in particular, be aware that this is an unusual set, and is not for general purpose
 * use. It is particularly useful within JPropeller since it is often required to
 * know which set of instances is relevant to an operation - it matters whether the
 * actual {@link Changeable} "a" has had a change, not whether a {@link Changeable} that
 * happens to be equal to "a" has had a change. Other examples are for {@link CalculatedProp}s
 * and their calculations, where it is important to know the {@link Set} of {@link Changeable}s
 * that the calculation depends on - again it is important to have the actual exact
 * {@link Changeable}, not just an equal one. A particular concern is around {@link CList} - empty
 * {@link CList} instances (like {@link List}s) are all equal, but this doesn't mean that
 * it is equivalent to depend on any empty {@link CList}!
 * 
 * @param <E>	The type of contents of the {@link Set}
 */
public class IdentityHashSet<E> extends AbstractCollection<E> implements Set<E> {

    private transient IdentityHashMap<E,Object> map;

    // Dummy value to associate with an Object in the backing Map
    // Note that all IdentityHashSets use the same Object, which means
    // that we can compare their maps using direct equality comparison
    // of IdentityHashMap
    private static final Object PRESENT = new Object();

    /**
     * Constructs a new, empty set; the backing {@link IdentityHashMap} instance has
     * default initial capacity (21)
     */
    public IdentityHashSet() {
    	map = new IdentityHashMap<E,Object>();
    }

    /**
     * Constructs a new set containing the elements in the specified
     * collection.  The {@link IdentityHashMap} is created with expected
     * maximum size of 1.5 times the collection size.
     *
     * @param c the collection whose elements are to be placed into this set
     * @throws NullPointerException if the specified collection is null
     */
    public IdentityHashSet(Collection<? extends E> c) {
    	map = new IdentityHashMap<E,Object>((int)(c.size() * 1.5));
    	addAll(c);
    }

    /**
     * Constructs a new, empty set; the backing {@link IdentityHashMap} instance has
     * the specified expected maximum size.
     *
     * @param      expectedMaxSize   the expected maximum size of the hash map
     */
    public IdentityHashSet(int expectedMaxSize) {
    	map = new IdentityHashMap<E,Object>(expectedMaxSize);
    }

    @Override
    public Iterator<E> iterator() {
    	return map.keySet().iterator();
    }

    @Override
    public int size() {
    	return map.size();
    }

    @Override
    public boolean isEmpty() {
    	return map.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
    	return map.containsKey(o);
    }

    @Override
    public boolean add(E e) {
    	return map.put(e, PRESENT)==null;
    }

    @Override
    public boolean remove(Object o) {
    	return map.remove(o)==PRESENT;
    }

    @Override
    public void clear() {
    	map.clear();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
    	throw new CloneNotSupportedException();
    }

    /**
     * Compares the specified object with this set for equality.  Returns
     * <tt>true</tt> if the specified object is also a set, the two sets
     * have the same size, and every member of the specified set is
     * contained in this set (or equivalently, every member of this set is
     * contained in the specified set).  This definition ensures that the
     * equals method works properly across different implementations of the
     * set interface.
     * 
     * <p><b>Owing to the reference-equality-based semantics of this set it is
     * possible that the symmetry and transitivity requirements of the
     * <tt>Object.equals</tt> contract may be violated if this set is compared
     * to a normal set.  However, the {@link Object#equals(Object)} contract is
     * guaranteed to hold among {@link IdentityHashSet} instances.</b>
     *
     * @param o object to be compared for equality with this set
     * @return <tt>true</tt> if the specified object is equal to this set
     */
	@SuppressWarnings("rawtypes")		//We can use raw types since we use only equals method
	@Override
	public boolean equals(Object o) {
		
		//Quick check, equal to ourselves
        if (o == this) {
            return true;
            
        //If we have another IdentityHashSet we can compare
        //based on the backing map
        } else if (o instanceof IdentityHashSet) {
        	IdentityHashSet iset = (IdentityHashSet) o;
        	
        	//Quickest to compare on size
            if (iset.size() != size())
                return false;

            //We can now compare based on backing map,
            //we know this compares based on equality of the
            //keys AND values - the values are always equal
            //since each IdentityHashSet uses the same single
            //static value: PRESENT
            IdentityHashMap otherMap = iset.map;
            
            return map.equals(otherMap);
            
        //If we have another type of Set, we will try to
        //compare using its equality method, which may or
        //may not work, as noted in javadoc
        } else if (o instanceof Set) {
            Set s = (Set)o;
            return s.equals(this);
            
        //Not equal to something that is not a Set
        } else {
            return false;
        }
	}

    /**
     * Returns the hash code value for this set. The hash code of a set is
     * defined to be the sum of the hash codes of the elements in the set,
     * where the hash code of a <tt>null</tt> element is defined to be zero.
     * This ensures that <tt>s1.equals(s2)</tt> implies that
     * <tt>s1.hashCode()==s2.hashCode()</tt> for any two sets <tt>s1</tt>
     * and <tt>s2</tt>, as required by the general contract of
     * {@link Object#hashCode}.
     *
     * <p><b>Owing to the reference-equality-based semantics of the
     * instances in this set, it is possible that the contractual
     * requirement of <tt>Object.hashCode</tt> mentioned in the previous
     * paragraph will be violated if one of the two objects being compared is
     * an {@link IdentityHashSet} instance and the other is a normal {@link Set}.
     * This is because two {@link IdentityHashSet}s must only be equal if they contain
     * the "same" elements, and elements are only considered the same in an
     * {@link IdentityHashSet} if they are equal references (==). Hence we
     * must provide system identity hash codes. These may NOT be comparable
     * to the object hash chodes used in a normal {@link Set}, if the
     * objects have non-default hash codes / equality methods.
     * </b>
     *
     * @return the hash code value for this set
     * @see Object#equals(Object)
     * @see #equals(Object)
     */
	public int hashCode() {
        int result = 0;
        for (E e : this) {
        	result += System.identityHashCode(e);
        }
        return result;
	}

	//This method is equivalent to that
	//from AbstractSet, but commented and checked for correct
	//function with identicality
	@Override
	public boolean removeAll(Collection<?> other) {
		boolean removedAny = false;

		//If we are the larger collection
		if (size() > other.size()) {
			
			//Remove each element of the other from ourself
			for (Iterator<?> i = other.iterator(); i.hasNext(); )
				removedAny |= remove(i.next());
			
		//If the other collection is larger
		} else {
			
			//Check each of our elements - remove them if they
			//are in the other set
			for (Iterator<?> i = iterator(); i.hasNext(); ) {
				if (other.contains(i.next())) {
					i.remove();
					removedAny = true;
				}
			}
		}
		return removedAny;
	}

	//This method is equivalent to that
	//from AbstractCollection, but commented and checked for correct
	//function with identicality
	@Override
	public boolean addAll(Collection<? extends E> other) {
		boolean modified = false;
		
		//Add each element of other collection
		Iterator<? extends E> i = other.iterator();
		while (i.hasNext()) {
			if (add(i.next())) {
				//If we actually added anything new, we are modified
				modified = true;
			}
		}
		
		return modified;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		//Need to actually check whether each element in c
		//is also in this set. You might think it is useful to
		//compare sizes, but of course c might contain duplicates,
		//whereas this set can't
		Iterator<?> e = c.iterator();
		while (e.hasNext())
		    if (!contains(e.next()))
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <b>
	 * Note that the behaviour of this method needs careful consideration - 
	 * it removes from this set any of this set's contents that are
	 * contained in the {@link Collection} c. Since the {@link Collection} c
	 * might be based on object equality ( {@link Object#equals(Object)} ) 
	 * rather than reference equality/identicality (==), some elements may be removed
	 * from this set that are just equal to those in c, rather than identical.
	 * </b>
	 * 
	 * However if c is an {@link IdentityHashSet}, only identical objects will
	 * be removed 
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		boolean modified = false;
		Iterator<E> e = iterator();
		while (e.hasNext()) {
		    if (!c.contains(e.next())) {
		    	e.remove();
		    	modified = true;
		    }
		}
		return modified;
	}

	@Override
	public Object[] toArray() {
		//Super implementation doesn't consider equality, just uses iterator, so safe
		return super.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		//Super implementation doesn't consider equality, just uses iterator, so safe
		return super.toArray(a);
	}
	
}
