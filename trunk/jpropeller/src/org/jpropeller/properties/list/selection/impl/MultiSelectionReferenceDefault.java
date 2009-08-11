package org.jpropeller.properties.list.selection.impl;

import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.collection.CCollection;
import org.jpropeller.collection.CList;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.list.selection.MultiSelectionReference;

/**
 * Default implementation of {@link MultiSelectionReference}
 *
 * @param <T>		The type of element in the list
 */
public class MultiSelectionReferenceDefault<T> extends BeanDefault implements MultiSelectionReference<T> {

	private final Prop<? extends CCollection<Integer>> selection;
	private final Prop<CList<T>> value;

	/**
	 * Create a {@link MultiSelectionReferenceDefault}
	 * @param value			The value itself
	 * @param selection		The selected indices within the value list
	 */
	public MultiSelectionReferenceDefault(
			Prop<CList<T>> value,
			Prop<? extends CCollection<Integer>> selection) {
		super();
		this.value = addProp(value);
		this.selection = addProp(selection);
	}

	@Override
	public Prop<? extends CCollection<Integer>> selection() { return selection; }

	@Override
	public Prop<CList<T>> value() {return value; }

}
