/*
 *  $Id: MutablePropBean.java,v 1.1 2008/03/24 11:19:49 shingoki Exp $
 *
 *  Copyright (c) 2008 shingoki
 *
 *  This file is part of jpropeller, see http://jpropeller.sourceforge.net
 *
 *    jpropeller is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    jpropeller is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with jpropeller; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.jpropeller.collection.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.jpropeller.bean.BeanFeatures;
import org.jpropeller.bean.MutableBeanFeatures;
import org.jpropeller.collection.ObservableList;
import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.calculated.PropCalculation;
import org.jpropeller.properties.calculated.impl.CalculatedProp;
import org.jpropeller.properties.change.Change;
import org.jpropeller.properties.change.ChangeSystem;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.change.impl.ChangeDefault;
import org.jpropeller.properties.change.impl.InternalChangeImplementation;
import org.jpropeller.system.PropSystem;
import org.jpropeller.system.Props;

/**
 * An {@link ObservableListCalculated} uses a {@link PropCalculation} to
 * produce a {@link List} from source properties, and then makes that
 * {@link List} available for read-only access.
 * 
 * The {@link ObservableListCalculated} changes only when a new calculation
 * is required, and when elements of a previously calculated list change, 
 * and that calculation is performed lazily, and cached - this
 * behaviour is similar to that for {@link CalculatedProp}.
 * 
 * @param <E>
 * 		The type of element in the list
 */
public class ObservableListCalculated<E> implements ObservableList<E> {
	
	//Standard code block for a bean
	private MutableBeanFeatures features;

	private PropCalculation<List<E>> calculation;

	//The current calculation list result we delegate 
	//to for actual storage, etc., or null if no value
	private List<E> core;	
	private boolean cacheValid = false;

	@Override
	public BeanFeatures features() {
		return features;
	}

	
	/**
	 * Create a new {@link ObservableListCalculated}
	 * @param calculation
	 * 		The calculation yielding list contents
	 */
	public ObservableListCalculated(PropCalculation<List<E>> calculation) {
		super();
		
		this.calculation = calculation;
		
		features = Props.getPropSystem().createBeanFeatures(this, new InternalChangeImplementation() {
			@Override
			public Change internalChange(Changeable changed, Change change,
					List<Changeable> initial, Map<Changeable, Change> changes) {
				
				//Mark the cache as invalid - it will need to be recalculated
				cacheValid = false;

				System.out.println(">>>>Cache invalidated");
				
				//Since we only listen to the actual props we are calculated
				//from, we don't need to filter here, we always have a
				//change
				return ChangeDefault.instance(
						false,	//Change is consequence of source proper change 
						false	//Instance may well change - we don't know until we recalculate, 
								//but it is very likely (e.g. a new primitive wrapper value)
						);

			}
		});
		
		//Listen to each source prop
		for (GeneralProp<?> prop : calculation.getSourceProps()) {
			prop.features().addChangeableListener(this);
		}
	}

	/**
	 * Start a read operation - prepare for a read, then attempt to
	 * calculate a new cached value if necessary.
	 * Note that when this returns or throws an exception, the 
	 * {@link PropSystem} read lock WILL be held 
	 * {@link ChangeSystem#prepareRead(Changeable)} is called 
	 * as first operation). Hence it is necessary to use this inside
	 * a try...finally block, with {@link #end()} in the finally block:
	 * <pre>
	 * 	try {
	 * 		start();
	 * 		//Read state from the cachedValue
	 * </pre>
	 */
	private void start() {
		System.out.println("start()");

		Props.getPropSystem().getChangeSystem().prepareRead(this);
		System.out.println("prepared Read");
		if (!cacheValid) {
			System.out.println("recalculating");

			core = calculation.calculate();
			System.out.println("recalculated");
			
			//Null values are not acceptable - throw exception sooner rather than later
			if (core == null) {
				throw new NullPointerException("Null list returned by calculation");
			}
			
			//We now have a usable value
			cacheValid = true;
		} else {
			System.out.println("No recalculation needed");
		}
	}
	
	private void end() {
		Props.getPropSystem().getChangeSystem().concludeRead(this);
	}

	//The means of reading state - need to check cache before each, and
	//also use read lock
	
	@Override
	public boolean contains(Object o) {
		try {
			start();
			return core.contains(o);
		} finally {
			end();
		}
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		try {
			start();
			return core.containsAll(c);
		} finally {
			end();
		}
	}

	@Override
	public E get(int index) {
		try {
			start();
			return core.get(index);
		} finally {
			end();
		}
	}

	@Override
	public int indexOf(Object o) {
		try {
			start();
			return core.indexOf(o);
		} finally {
			end();
		}
	}

	@Override
	public boolean isEmpty() {
		try {
			start();
			return core.isEmpty();
		} finally {
			end();
		}
	}

	@Override
	public Iterator<E> iterator() {
		try {
			start();
			return core.iterator();
		} finally {
			end();
		}
	}

	@Override
	public int lastIndexOf(Object o) {
		try {
			start();
			return core.lastIndexOf(o);
		} finally {
			end();
		}
	}

	@Override
	public ListIterator<E> listIterator() {
		try {
			start();
			return core.listIterator();
		} finally {
			end();
		}
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		try {
			start();
			return core.listIterator(index);
		} finally {
			end();
		}
	}

	@Override
	public int size() {
		System.out.println("Checking size");
		try {
			start();
			return core.size();
		} finally {
			end();
		}
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		try {
			start();
			return core.subList(fromIndex, toIndex);
		} finally {
			end();
		}
	}

	@Override
	public Object[] toArray() {
		try {
			start();
			return core.toArray();
		} finally {
			end();
		}
	}

	@Override
	public <T> T[] toArray(T[] a) {
		try {
			start();
			return core.toArray(a);
		} finally {
			end();
		}
	}
	
	//Unsupported operations
	private void throwUnsupported(String operation) {
		throw new UnsupportedOperationException("Can't " + operation + " " + ObservableListCalculated.class.getName());		
	}
	private void throwUnsupportedAdd() {
		throwUnsupported("add to");		
	}
	@Override
	public void replace(Iterable<E> newContents) { throwUnsupported("replace");}
	@Override
	public boolean add(E e) {throwUnsupportedAdd(); return false;}
	@Override
	public void add(int index, E element) {throwUnsupportedAdd();}
	@Override
	public boolean addAll(Collection<? extends E> c) {throwUnsupportedAdd(); return false;}
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {throwUnsupportedAdd(); return false;}
	@Override
	public void clear() {throwUnsupported("clear");}
	@Override
	public boolean remove(Object o) {throwUnsupported("remove from");return false;}
	@Override
	public E remove(int index) {throwUnsupported("remove from");return null;}
	@Override
	public boolean removeAll(Collection<?> c) {throwUnsupported("remove from");return false;}
	@Override
	public boolean retainAll(Collection<?> c) {throwUnsupported("alter (by retainAll)");return false;}
	@Override
	public E set(int index, E element) {throwUnsupported("set elements of");return null;}
	
}
