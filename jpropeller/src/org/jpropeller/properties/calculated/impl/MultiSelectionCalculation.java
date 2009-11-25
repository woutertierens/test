package org.jpropeller.properties.calculated.impl;

import java.util.ArrayList;
import java.util.List;

import org.jpropeller.calculation.Calculation;
import org.jpropeller.collection.CList;
import org.jpropeller.properties.Prop;
import org.jpropeller.util.NoInstanceAvailableException;
import org.jpropeller.util.Source;

/**
 * Source for calculation giving a list of selections in another list,
 * chosen by an iterable of integers indices.
 */
public class MultiSelectionCalculation {

	private MultiSelectionCalculation(){};
	
	/**
	 * Create a new {@link Calculation} giving the
	 * selected items
	 * @param list			The list we are selecting from
	 * @param selection		The selected indices within the list
	 * @return				A {@link Calculation} giving a list of selected items
	 * 
	 * @param <T>			The type of item in the lists
	 */
	public static <T> Calculation<List<T>> create(final Prop<? extends CList<? extends T>> list, final Prop<? extends Iterable<Integer>> selection) {
		return BuildCalculation.<List<T>>on(list, selection).returning(new Source<List<T>>() {
			@Override
			public List<T> get() throws NoInstanceAvailableException {
				List<T> result = new ArrayList<T>();
				CList<? extends T> currentList = list.get();
				int size = currentList.size();
				for (int i : selection.get()) {
					if (i >= 0 && i < size) {
						result.add(currentList.get(i));
					}
				}
				return result;
			}
		});
	}
	
}
