package org.jpropeller.properties.list.selection.impl;

import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.collection.CList;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.list.impl.ListPropDefault;
import org.jpropeller.properties.list.selection.ListAndSelectionAndValueReference;
import org.jpropeller.transformer.BeanPathTo;

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
	public final static <S> BeanPathTo<ListAndSelectionAndValueReference<S>, S> transformerToSelectedValue() {
		return new BeanPathTo<ListAndSelectionAndValueReference<S>, S>() {
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
	 * @param valueProp
	 * 		The prop to be used as the value of the reference. 
	 * 		(returned by {@link #value()}).
	 * 		Normally expected to be named "value", but this is not
	 * required.
	 * @param clazz
	 * 		The class of value in the list 
	 */
	public ListAndSelectionAndValueReferenceDefault(Prop<CList<T>> valueProp, Class<T> clazz) {
		value = addProp(valueProp);
		
		//Tracks selection within whichever list is in the "list" prop
		selection = addProp(new ListSelectionReferenceProp<T>(PropName.create("selection", Integer.class), value));
		
		//Tracks the actual selected value by looking up the selection index in the list
		selectedValue = addProp(new ListIndexProp<T>(PropName.create("selectedValue", clazz), value, selection));
	}
	
	/**
	 * Make a new {@link ListAndSelectionAndValueReferenceDefault}
	 * @param list
	 * 		The initial list value in the {@link ListAndSelectionAndValueReferenceDefault}
	 * @param clazz
	 * 		The class of value in the list 
	 */
	public ListAndSelectionAndValueReferenceDefault(CList<T> list, Class<T> clazz) {
		this(ListPropDefault.editable("model", clazz, list), clazz);
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

}
