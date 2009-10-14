package org.jpropeller.properties.list.selection.impl;

import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.collection.CList;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.list.impl.ListPropDefault;
import org.jpropeller.properties.list.selection.ListAndSelectionReference;
import org.jpropeller.reference.Reference;

/**
 * Default implementation of {@link ListAndSelectionReference}
 * @param <T>
 * 		The type of element in the list 
 */
public class ListAndSelectionReferenceDefault<T> extends BeanDefault implements Reference<CList<T>>, ListAndSelectionReference<T> {

	ListSelectionReferenceProp<T> selection;
	Prop<CList<T>> value;

	/**
	 * Make a new {@link ListAndSelectionReferenceDefault}
	 * @param clazz
	 * 		The class of value in the list 
	 * @param list
	 * 		The initial list value in the {@link ListAndSelectionReferenceDefault}
	 */
	public ListAndSelectionReferenceDefault(Class<T> clazz, CList<T> list) {
		Prop<CList<T>> valueProp = ListPropDefault.editable(clazz, "model", list);

		value = addProp(valueProp);
		
		//Tracks selection within whichever list is in the "list" prop
		selection = addProp(new ListSelectionReferenceProp<T>(PropName.create(Integer.class, "selection"), value));
	}

	@Override
	public ListSelectionReferenceProp<T> selection() {
		return selection;
	}

	@Override
	public String toString() {
		return "Selected index " + selection().get();
	}

	@Override
	public Prop<CList<T>> value() {
		return value;
	}
}
