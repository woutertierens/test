package org.jpropeller.properties.list.impl;

import java.util.Iterator;
import org.jpropeller.collection.CList;
import org.jpropeller.collection.impl.CListDefault;
import org.jpropeller.info.PropAccessType;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.changeable.impl.ChangeablePropDefault;
import org.jpropeller.properties.list.ListProp;
import org.jpropeller.properties.values.ValueProcessor;
import org.jpropeller.properties.values.impl.AcceptProcessor;
import org.jpropeller.properties.values.impl.ReadOnlyProcessor;

/**
 * An implementation of an {@link ListProp} that uses an {@link CList}
 * to back the data.
 *
 * @param <L>
 * 		The type of data in the list in the prop 
 */
public class ListPropDefault<L> extends ChangeablePropDefault<CList<L>> implements ListProp<L> {

	/**
	 * Create a prop, using a specified {@link CList} directly
	 * in the prop.
	 * @param data
	 * 		The wrapped list
	 */
	ListPropDefault(PropName<CList<L>> name, CList<L> data, ValueProcessor<CList<L>> processor) {
		super(name, data, processor);
	}

	/**
	 * Create a new read-only {@link ListPropDefault}, using
	 * an empty new {@link CListDefault}
	 * @param contentsClass
	 * 		The class of data in the list/indexed property
	 * @param name 
	 * 		The string value of the property name
	 * @param <S> 
	 * 		The type of data in the list
	 * @return
	 * 		The new {@link ListPropDefault}
	 */
	public static <S> ListPropDefault<S> create(Class<S> contentsClass, String name) {
		return create(contentsClass, name, new CListDefault<S>());
	}

	/**
	 * Create a new read-only {@link ListPropDefault}
	 * @param contentsClass
	 * 		The class of data in the list/indexed property
	 * @param name 
	 * 		The string value of the property name
	 * @param data
	 * 		The data to contain
	 * @param <S>
	 * 		The type of data in the list/indexed property
	 * @return
	 * 		The new {@link ListPropDefault}
	 */
	public static <S> ListPropDefault<S> create(Class<S> contentsClass, String name, CList<S> data) {
		PropName<CList<S>> propName = PropName.createList(contentsClass, name);
		return new ListPropDefault<S>(propName, data, ReadOnlyProcessor.<CList<S>>get());
	}
	
	/**
	 * Create a new read-only {@link ListPropDefault}, using
	 * an empty new {@link CListDefault}
	 * @param contentsClass
	 * 		The class of data in the list/indexed property
	 * @param name 
	 * 		The string value of the property name
	 * @param <S> 
	 * 		The type of data in the list
	 * @return
	 * 		The new {@link ListPropDefault}
	 */
	public static <S> ListPropDefault<S> editable(Class<S> contentsClass, String name) {
		return editable(contentsClass, name, new CListDefault<S>());
	}

	/**
	 * Create a new read-only {@link ListPropDefault}
	 * @param contentsClass
	 * 		The class of data in the list/indexed property
	 * @param name 
	 * 		The string value of the property name
	 * @param data
	 * 		The data to contain
	 * @param <S>
	 * 		The type of data in the list/indexed property
	 * @return
	 * 		The new {@link ListPropDefault}
	 */
	public static <S> ListPropDefault<S> editable(Class<S> contentsClass, String name, CList<S> data) {
		PropName<CList<S>> propName = PropName.createList(contentsClass, name);
		return new ListPropDefault<S>(propName, data, AcceptProcessor.<CList<S>>get());
	}
	
	
	@Override
	public PropAccessType getAccessType() {
		return PropAccessType.LIST;
	}

	@Override
	public String toString() {
		return "List Prop '" + getName().getString() + "' = '" + get() + "'";
	}
	
	//List-style methods, delegated to list
	
	@Override
	public L get(int index) {
		return this.value.get(index);
	}	

	@Override
	public int size() {
		return this.value.size();
	}

	@Override
	public Iterator<L> iterator() {
		return this.value.iterator();
	}

	@Override
	public void add(L value) {
		this.value.add(value);
	}

	@Override
	public void replace(Iterable<L> newContents) {
		this.value.replace(newContents);
	}

	@Override
	public L set(int index, L value) {
		return this.value.set(index, value);
	}
}
