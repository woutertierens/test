package org.jpropeller.properties.list.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jpropeller.calculation.Calculation;
import org.jpropeller.collection.CCollection;
import org.jpropeller.collection.CList;
import org.jpropeller.collection.impl.IdentityHashSet;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Changeable;

/**
 * A {@link Calculation} giving a {@link List}, where the ith element
 * in the calculated list is taken from a list prop, at the integer index
 * given at the ith position in a selectionProp (as iterated).
 *
 * @param <T>		The type of element in the lists
 */
public class ListSelectionCalculation<T> implements Calculation<List<T>> {

	private final Set<Prop<?>> sourceProps;
	private final Prop<? extends CCollection<Integer>> selectionProp;
	private final Prop<? extends CList<T>> listProp;
	
	/**
	 * Make a {@link ListSelectionCalculation}
	 * @param selectionProp		The prop containing selected indices. The indexed elements from
	 * 							listProp will be in the calculated list, in the order their 
	 * 							indices occur in this selection. Any selected indices outside
	 * 							the list will be ignored.
	 * 							
	 * @param listProp			The prop giving the list from which we are selecting elements,
	 * 							according to the selection indices in selectionProp
	 */
	public ListSelectionCalculation(Prop<? extends CCollection<Integer>> selectionProp, Prop<? extends CList<T>> listProp) {
		this.selectionProp = selectionProp;
		this.listProp = listProp;
		IdentityHashSet<Prop<?>> props = new IdentityHashSet<Prop<?>>();
		props.add(selectionProp);
		props.add(listProp);
		sourceProps = Collections.unmodifiableSet(props);
	}
	
	@Override
	public List<T> calculate() {
		List<T> result = new ArrayList<T>(selectionProp.get().size());
		CList<T> list = listProp.get();
		for (Integer i : selectionProp.get()) {
			if (i >= 0 && i < list.size()) {
				result.add(list.get(i));
			}
		}
		return result;
	}

	@Override
	public Set<? extends Changeable> getSources() {
		return sourceProps;
	}

}
