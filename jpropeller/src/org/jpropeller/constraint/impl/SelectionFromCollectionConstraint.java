package org.jpropeller.constraint.impl;

import java.util.Collections;
import java.util.Set;

import org.jpropeller.collection.CCollection;
import org.jpropeller.collection.impl.IdentityHashSet;
import org.jpropeller.constraint.Constraint;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Changeable;

/**
 * This {@link Constraint} will manage a selection from the
 * {@link CCollection} value of a {@link Prop}, ensuring that
 * the value is always in the {@link CCollection}.
 * 
 * If selectFirst is true, then when the value
 * needs to be constrained, it will be changed to the first item in
 * the collection, OR null if the collection is empty. Otherwise, the value will
 * always be reset to null.
 *
 * @param <T>	The type of value, and of element in the collection
 */
public class SelectionFromCollectionConstraint<T> implements Constraint<T> {

	private final boolean selectFirst;
	private final Prop<? extends CCollection<? extends T>> collectionProp;
	private final Set<Changeable> sources;
	
	/**
	 * Make a {@link SelectionFromCollectionConstraint}
	 *
	 * This will ensure that the value is always an 
	 * element of the collection  (or at least that collection.contains(value) is always true).
	 * Hence changes to values that are not contained in collection are rejected 
	 * (value is reset) and if the collection changes so that it no longer 
	 * contains the value, the value will also be reset. 
	 * 
	 * @param collectionProp	{@link Prop} giving the collection value is constrained to.
	 * 
	 * @param selectFirst		Determines the behavious when value is reset as
	 * 							described above. 
	 * 							If true, the value will be set to the 
	 * 							first item in the collection, or null if the collection is empty.
	 * 							If false, the value will be nulled on selection reset.
	 */
	public SelectionFromCollectionConstraint(Prop<? extends CCollection<? extends T>> collectionProp, boolean selectFirst) {
		this.collectionProp = collectionProp;
		this.selectFirst = selectFirst;
		
		IdentityHashSet<Changeable> sourcesM = new IdentityHashSet<Changeable>();
		sourcesM.add(collectionProp);
		sources = Collections.unmodifiableSet(sourcesM);
	}

	
	@Override
	public T constrain(T setValue) {
		CCollection<? extends T> collection = collectionProp.get();
		
		//If we have no collection, just null value
		if (collection == null) {
			return null;
		}
		
		//No problem if value is in collection
		if (collection.contains(setValue)) {
			return setValue;
		} else {
			if (selectFirst) {
				return collection.isEmpty() ? null : collection.iterator().next();
			} else {
				return null;
			}
		}
	}

	@Override
	public Set<? extends Changeable> getSources() {
		return sources;
	}

}
