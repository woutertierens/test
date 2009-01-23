package org.jpropeller.properties.list.impl;

import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.collection.ObservableList;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.GenericEditableProp;
import org.jpropeller.properties.changeable.impl.GenericEditableChangeablePropDefault;
import org.jpropeller.properties.list.ListAndSelectionReference;
import org.jpropeller.reference.Reference;

/**
 * Default implementation of {@link ListAndSelectionReference}
 * @param <T>
 * 		The type of element in the list 
 */
public class ListAndSelectionReferenceDefault<T> extends BeanDefault implements Reference<ObservableList<T>>, ListAndSelectionReference<T> {

	ListSelectionReferenceProp<T> selection;
	GenericEditableProp<ObservableList<T>> value;

	/**
	 * Make a new {@link ListAndSelectionReferenceDefault}
	 * @param list
	 * 		The initial list value in the {@link ListAndSelectionReferenceDefault}
	 * @param clazz
	 * 		The class of value in the list 
	 */
	public ListAndSelectionReferenceDefault(ObservableList<T> list, Class<T> clazz) {
		value = addProp(GenericEditableChangeablePropDefault.createObservableList("model", clazz, list));
		
		//Tracks selection within whichever list is in the "list" prop
		selection = addProp(new ListSelectionReferenceProp<T>(PropName.editable("selection", Integer.class), value));
	}
	
	/* (non-Javadoc)
	 * @see org.jpropeller.properties.list.impl.ListAndSelectionReference#selection()
	 */
	public ListSelectionReferenceProp<T> selection() {
		return selection;
	}

	@Override
	public String toString() {
		return "Selected index " + selection().get();
	}

	/* (non-Javadoc)
	 * @see org.jpropeller.properties.list.impl.ListAndSelectionReference#value()
	 */
	@Override
	public GenericEditableProp<ObservableList<T>> value() {
		return value;
	}
}
