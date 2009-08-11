package org.jpropeller.view.combo.impl;

import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.collection.CList;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.properties.changeable.impl.ChangeablePropDefault;
import org.jpropeller.properties.constrained.impl.SelectionFromCollectionProp;
import org.jpropeller.view.combo.ListComboBoxReference;

/**
 * Default, mutable implementation of {@link ListComboBoxReference}
 *
 * @param <T>	The type of element in the list and selection
 */
public class ListComboBoxReferenceDefault<T> extends BeanDefault implements ListComboBoxReference<T> {

	private final Prop<T> selection;
	private final Prop<CList<T>> value;

	/**
	 * Create a {@link ListComboBoxReference}
	 * @param value			The list
	 * @param selection		The selection in the list
	 */
	private ListComboBoxReferenceDefault(Prop<CList<T>> value, Prop<T> selection) {
		super();
		this.value = addProp(value);
		this.selection = addProp(selection);
	}

	/**
	 * Create a {@link ListComboBoxReference}
	 * @param value			The list
	 * @param valueClass	The class of the list contents
	 * @param initialSelection	The initial selection
	 * @param <S>			The type of list contents 
	 * @return 				A new {@link ListComboBoxReferenceDefault}
	 */
	public static <S extends Changeable> ListComboBoxReferenceDefault<S> create(Prop<CList<S>> value, Class<S> valueClass, S initialSelection) {
		return new ListComboBoxReferenceDefault<S>(value, ChangeablePropDefault.editable("selection", valueClass, initialSelection));
	}
	
	/**
	 * Create a {@link ListComboBoxReference} where
	 * the value is constrained to be in the list, using a
	 * {@link SelectionFromCollectionProp} 
	 * @param value			The list
	 * @param valueClass	The class of the list contents
	 * @param selectFirst	If true, then the selection will be set to the first element
	 * 						of the list when it must be constrained.
	 * 						If false, selection is set to null when constrained.
	 * 						{@link SelectionFromCollectionProp}
	 * 
	 * @param <S>			The type of list contents 
	 * @return 				A new {@link ListComboBoxReferenceDefault}
	 */
	public static <S extends Changeable> ListComboBoxReferenceDefault<S> createConstrained(Prop<CList<S>> value, Class<S> valueClass, boolean selectFirst) {
		Prop<S> selection = new SelectionFromCollectionProp<S>(PropName.create("selection", valueClass), value, selectFirst);
		return new ListComboBoxReferenceDefault<S>(value, selection);
	}
	
	/**
	 * Create a {@link ListComboBoxReference} where
	 * the value may be constrained to be in the list, using a
	 * {@link SelectionFromCollectionProp} 
	 * @param value			The list
	 * @param valueClass	The class of the list contents
	 * @param selectionMustBeInList		If true, selection is constrained to be in list.
	 * @param selectFirst	If true, then the selection will be set to the first element
	 * 						of the list when it must be constrained.
	 * 						If false, selection is set to null when constrained.
	 * 						{@link SelectionFromCollectionProp}
	 * 
	 * @param <S>			The type of list contents 
	 * @return 				A new {@link ListComboBoxReferenceDefault}
	 */
	public static <S extends Changeable> ListComboBoxReference<S> create(Prop<CList<S>> value, Class<S> valueClass, boolean selectionMustBeInList, boolean selectFirst) {
		ListComboBoxReference<S> comboRef;
		if (selectionMustBeInList) {
			comboRef = ListComboBoxReferenceDefault.createConstrained(value, valueClass, selectFirst);
		} else {
			comboRef = ListComboBoxReferenceDefault.create(value, valueClass);
		}
		return comboRef;
	}
	
	/**
	 * Create a {@link ListComboBoxReference} with a null initial selection
	 * @param value			The list
	 * @param valueClass	The class of the list contents
	 * @param <S>			The type of list contents 
	 * @return 				A new {@link ListComboBoxReferenceDefault}
	 */
	public static <S extends Changeable> ListComboBoxReferenceDefault<S> create(Prop<CList<S>> value, Class<S> valueClass) {
		return create(value, valueClass, null);
	}
	
	@Override
	public Prop<T> selection() { return selection; }

	@Override
	public Prop<CList<T>> value() {return value; }

}
