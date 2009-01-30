package org.jpropeller.properties.list.impl;

import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.collection.ObservableList;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.GenericEditableProp;
import org.jpropeller.properties.changeable.impl.GenericEditableChangeablePropDefault;
import org.jpropeller.properties.list.ListSelectionEditableValueReference;
import org.jpropeller.transformer.BeanPathToEditable;

/**
 * Default implementation of {@link ListSelectionEditableValueReference}
 * @param <T>
 * 		The type of element in the list 
 */
public class ListSelectionEditableValueReferenceDefault<T> extends BeanDefault implements ListSelectionEditableValueReference<T> {

	ListSelectionReferenceProp<T> selection;
	GenericEditableProp<ObservableList<T>> value;
	EditableProp<T> selectedValue;

	/**
	 * Make a Transformer from a {@link ListSelectionEditableValueReference} to the selected value
	 * of that reference
	 * @param <S>
	 * 		The type of data in the selected value (and the list) 
	 * @return
	 * 		Transformer
	 */
	public final static <S> BeanPathToEditable<ListSelectionEditableValueReference<S>, S> transformerToSelectedValue() {
		return new BeanPathToEditable<ListSelectionEditableValueReference<S>, S>() {
			@Override
			public EditableProp<S> transform(ListSelectionEditableValueReference<S> ref) {
				return ref.selectedValue();
			}
		};
	}
	
	
	/**
	 * Make a new {@link ListSelectionEditableValueReferenceDefault},
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
	public ListSelectionEditableValueReferenceDefault(GenericEditableProp<ObservableList<T>> valueProp, Class<T> clazz) {
		value = addProp(valueProp);
		
		//Tracks selection within whichever list is in the "list" prop
		selection = addProp(new ListSelectionReferenceProp<T>(PropName.editable("selection", Integer.class), value));
		
		//Tracks the actual selected value by looking up the selection index in the list
		selectedValue = addProp(new EditableListIndexProp<T>(PropName.editable("selectedValue", clazz), value, selection));
	}
	
	/**
	 * Make a new {@link ListSelectionEditableValueReferenceDefault}
	 * @param list
	 * 		The initial list value in the {@link ListSelectionEditableValueReferenceDefault}
	 * @param clazz
	 * 		The class of value in the list 
	 */
	public ListSelectionEditableValueReferenceDefault(ObservableList<T> list, Class<T> clazz) {
		value = addProp(GenericEditableChangeablePropDefault.createObservableList("model", clazz, list));
		
		//Tracks selection within whichever list is in the "list" prop
		selection = addProp(new ListSelectionReferenceProp<T>(PropName.editable("selection", Integer.class), value));
		
		//Tracks the actual selected value by looking up the selection index in the list
		selectedValue = addProp(new EditableListIndexProp<T>(PropName.editable("selectedValue", clazz), value, selection));
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
	public EditableProp<T> selectedValue() {
		return selectedValue;
	}
	
	@Override
	public String toString() {
		return "Selected index " + selection().get();
	}

	@Override
	public GenericEditableProp<ObservableList<T>> value() {
		return value;
	}
}
