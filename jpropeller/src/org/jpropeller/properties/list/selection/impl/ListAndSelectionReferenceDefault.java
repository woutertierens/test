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

	Prop<Integer> selection;
	Prop<CList<T>> value;

	/**
	 * Make a new {@link ListAndSelectionReferenceDefault}
	 * @param list			The {@link Prop} of {@link CList} in the reference
	 * @param selection		The {@link Prop} of integer selection index in the reference
	 * @return 				A new {@link ListAndSelectionReferenceDefault}
	 * @param <T>			The type of element in the list 
	 */
	public static <T> ListAndSelectionReferenceDefault<T> create(Prop<CList<T>> list, Prop<Integer> selection) {
		return new ListAndSelectionReferenceDefault<T>(list, selection);
	}
	
	/**
	 * Make a new {@link ListAndSelectionReferenceDefault}
	 * @param list			The {@link Prop} of {@link CList} in the reference
	 * @param selection		The {@link Prop} of integer selection index in the reference
	 */
	public ListAndSelectionReferenceDefault(Prop<CList<T>> list, Prop<Integer> selection) {
		this.value = addProp(list);
		this.selection = addProp(selection);
	}
	
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
	public Prop<Integer> selection() {
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
