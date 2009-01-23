package org.jpropeller.properties.list.impl;

import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.collection.ObservableList;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.GenericEditableProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.changeable.impl.GenericEditableChangeablePropDefault;
import org.jpropeller.properties.list.ListSelectionValueReference;
import org.jpropeller.reference.Reference;

/**
 * A {@link Reference} to an {@link ObservableList}, which also has an additional
 * selection property. This property will track selection in the list when possible,
 * resetting to -1 when not possible (e.g. a new list is set)
 * @param <T>
 * 		The type of element in the list 
 */
public class ListSelectionValueReferenceDefault<T> extends BeanDefault implements ListSelectionValueReference<T> {

	ListSelectionReferenceProp<T> selection;
	GenericEditableProp<ObservableList<T>> value;
	Prop<T> selectedValue;

	/**
	 * Make a new {@link ListSelectionValueReferenceDefault}
	 * @param list
	 * 		The initial list value in the {@link ListSelectionValueReferenceDefault}
	 * @param clazz
	 * 		The class of value in the list 
	 */
	public ListSelectionValueReferenceDefault(ObservableList<T> list, Class<T> clazz) {
		value = addProp(GenericEditableChangeablePropDefault.createObservableList("model", clazz, list));
		
		//Tracks selection within whichever list is in the "list" prop
		selection = addProp(new ListSelectionReferenceProp<T>(PropName.editable("selection", Integer.class), value));
		
		//Tracks the actual selected value by looking up the selection index in the list
		selectedValue = addProp(new ListIndexProp<T>(PropName.create("selectedValue", clazz), value, selection));
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
	public String toString() {
		return "Selected index " + selection().get();
	}

	@Override
	public GenericEditableProp<ObservableList<T>> value() {
		return value;
	}
}
