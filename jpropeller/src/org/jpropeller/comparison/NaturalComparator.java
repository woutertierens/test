package org.jpropeller.comparison;

import java.util.Comparator;

/**
 * {@link NaturalComparator} just compares {@link Comparable} values using their
 * {@link Comparable#compareTo(Object)} method.
 *
 * @param <T>		The type of values compared
 */
public class NaturalComparator<T extends Comparable<? super T>> implements Comparator<T> {

	//This is the instance we give out. Due to type erasure, and lack of
	//state in the instances, all instances are identical anyway - 
	//we just preserve compile-time type safety by handing out the instance 
	//cast to the correct parametric type to ensure it only ever receives valid input types.
	@SuppressWarnings("unchecked")
	private final static NaturalComparator INSTANCE = new NaturalComparator();
	
	/**
	 * Creates a {@link NaturalComparator} for a given type
	 * @param <T>		The type to be compared
	 * @return			A new {@link NaturalComparator}
	 */
	//See explanation on SuppressWarnings above
	@SuppressWarnings("unchecked")
	public final static <T extends Comparable<? super T>> NaturalComparator<T> create(){
		return (NaturalComparator<T>)INSTANCE;
	}
	
	private NaturalComparator() {
	}
	
	@Override
	public int compare(T o1, T o2) {
		return o1.compareTo(o2);
	}

}
