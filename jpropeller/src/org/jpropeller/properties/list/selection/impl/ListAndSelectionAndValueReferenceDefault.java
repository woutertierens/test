package org.jpropeller.properties.list.selection.impl;

import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.collection.CList;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.list.impl.ListPropDefault;
import org.jpropeller.properties.list.selection.ListAndSelectionAndValueReference;
import org.jpropeller.transformer.PathStep;

/**
 * Default implementation of {@link ListAndSelectionAndValueReference}
 * @param <T>
 * 		The type of element in the list 
 */
public class ListAndSelectionAndValueReferenceDefault<T> extends BeanDefault implements ListAndSelectionAndValueReference<T> {

	ListSelectionReferenceProp<T> selection;
	Prop<CList<T>> value;
	Prop<T> selectedValue;

	/**
	 * Make a Transformer from a {@link ListAndSelectionAndValueReference} to the selected value
	 * of that reference
	 * @param <S>
	 * 		The type of data in the selected value (and the list) 
	 * @return
	 * 		Transformer
	 */
	public final static <S> PathStep<ListAndSelectionAndValueReference<S>, S> transformerToSelectedValue() {
		return new PathStep<ListAndSelectionAndValueReference<S>, S>() {
			@Override
			public Prop<S> transform(ListAndSelectionAndValueReference<S> ref) {
				return ref.selectedValue();
			}
		};
	}
	
	
	/**
	 * Make a new {@link ListAndSelectionAndValueReferenceDefault},
	 * using a specific prop for the value, rather than just an initial
	 * list.
	 * @param clazz
	 * 		The class of value in the list 
	 * @param valueProp
	 * 		The prop to be used as the value of the reference. 
	 * 		(returned by {@link #value()}).
	 * 		Normally expected to be named "value", but this is not
	 * required.
	 */
	public ListAndSelectionAndValueReferenceDefault(Class<T> clazz, Prop<CList<T>> valueProp) {
		value = addProp(valueProp);
		
		//Tracks selection within whichever list is in the "list" prop
		selection = addProp(new ListSelectionReferenceProp<T>(PropName.create(Integer.class, "selection"), value));
		
		//Tracks the actual selected value by looking up the selection index in the list
		selectedValue = addProp(new ListIndexProp<T>(PropName.create(clazz, "selectedValue"), value, selection));
	}
	
	/**
	 * Make a new {@link ListAndSelectionAndValueReferenceDefault}
	 * @param clazz
	 * 		The class of value in the list 
	 * @param list
	 * 		The initial list value in the {@link ListAndSelectionAndValueReferenceDefault}
	 */
	public ListAndSelectionAndValueReferenceDefault(Class<T> clazz, CList<T> list) {
		this(clazz, ListPropDefault.editable(clazz, "model", list));
	}
	
	/**
	 * The selection index in the list - can be set and got,
	 * and will move itself to track the same element in the list
	 * when possible.
	 * @return
	 * 		The selection index
	 */
	public ListSelectionReferenceProp<T> selection() {
		return selection;
	}

	/**
	 * The selected element of the list - that is, the element at
	 * the index from {@link #selection()}, in the list from
	 * {@link #value()}
	 * @return
	 * 		The selected elementListSelectionValueReference
	 */
	public Prop<T> selectedValue() {
		return selectedValue;
	}

	@Override
	public Prop<CList<T>> value() {
		return value;
	}
	
	@Override
	public String toString() {
		return "Selected index " + selection().get();
	}

	/**
	 * Get a {@link PathStep} from a {@link ListAndSelectionAndValueReference} to
	 * the {@link ListAndSelectionAndValueReference#selectedValue()} of that reference.
	 * @param <T>	The type of value
	 * @return		The {@link PathStep}
	 */
	public static <T> PathStep<ListAndSelectionAndValueReference<T>, T> toSelection() {
		return new PathStep<ListAndSelectionAndValueReference<T>, T>() {
			@Override
			public Prop<T> transform(ListAndSelectionAndValueReference<T> s) {
				return s.selectedValue();
			}
		};
	}
}
