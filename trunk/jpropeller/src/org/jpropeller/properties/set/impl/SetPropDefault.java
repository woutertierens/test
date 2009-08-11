package org.jpropeller.properties.set.impl;

import java.util.Iterator;

import org.jpropeller.collection.CSet;
import org.jpropeller.collection.impl.CSetDefault;
import org.jpropeller.info.PropAccessType;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.changeable.impl.ChangeablePropDefault;
import org.jpropeller.properties.set.SetProp;
import org.jpropeller.properties.values.ValueProcessor;
import org.jpropeller.properties.values.impl.AcceptProcessor;
import org.jpropeller.properties.values.impl.ReadOnlyProcessor;

/**
 * An implementation of an {@link SetProp}
 *
 * @param <T>
 * 		The type of value in the prop
 */
public class SetPropDefault<T> extends ChangeablePropDefault<CSet<T>> implements SetProp<T> {

	/**
	 * Create an {@link SetPropDefault}
	 * @param name 
	 * 		Name for prop
	 * @param value
	 * 		The initial value of the {@link SetPropDefault}
	 * @param processor 
	 * 		To valid input {@link CSet} values
	 */
	public SetPropDefault(PropName<CSet<T>> name, CSet<T> value, ValueProcessor<CSet<T>> processor) {
		super(name, value, processor);
	}

	/**
	 * Create a new {@link SetPropDefault},
	 * accepting all new values
	 * @param name 
	 * 		The string value of the property name
	 * @param clazz
	 * 		The class of data in the set
	 * @param <S> 
	 * 		The type of data in the set
	 * @param value
	 * 		The initial value of the {@link Prop}
	 * @return
	 * 		The new {@link SetPropDefault}
	 */
	public static <S> SetPropDefault<S> editable(String name, Class<S> clazz, CSet<S> value) {
		return new SetPropDefault<S>(PropName.createSet(name, clazz), value, AcceptProcessor.<CSet<S>>get());
	}
	

	/**
	 * Create a new {@link SetPropDefault} with an
	 * initial value of an empty new {@link CSetDefault},
	 * accepting all new values.
	 * @param name 
	 * 		The string value of the property name
	 * @param clazz
	 * 		The class of data in the list/indexed property
	 * @param <S> 
	 * 		The type of data in the list/indexed property
	 * @return
	 * 		The new {@link SetPropDefault}
	 */
	public static <S> SetPropDefault<S> editable(String name, Class<S> clazz) {
		return editable(name, clazz, new CSetDefault<S>());
	}

	/**
	 * Create a new {@link SetPropDefault},
	 * with read-only behaviour
	 * @param name 
	 * 		The string value of the property name
	 * @param clazz
	 * 		The class of data in the set
	 * @param <S> 
	 * 		The type of data in the set
	 * @param value
	 * 		The initial value of the {@link Prop}
	 * @return
	 * 		The new {@link SetPropDefault}
	 */
	public static <S> SetPropDefault<S> create(String name, Class<S> clazz, CSet<S> value) {
		return new SetPropDefault<S>(PropName.createSet(name, clazz), value, ReadOnlyProcessor.<CSet<S>>get());
	}
	

	/**
	 * Create a new {@link SetPropDefault} with an
	 * initial value of an empty new {@link CSetDefault},
	 * with read-only behaviour
	 * @param name 
	 * 		The string value of the property name
	 * @param clazz
	 * 		The class of data in the list/indexed property
	 * @param <S> 
	 * 		The type of data in the list/indexed property
	 * @return
	 * 		The new {@link SetPropDefault}
	 */
	public static <S> SetPropDefault<S> create(String name, Class<S> clazz) {
		return create(name, clazz, new CSetDefault<S>());
	}
	
	@Override
	public PropAccessType getAccessType() {
		return PropAccessType.SET;
	}

	@Override
	public String toString() {
		return "Set Prop '" + getName().getString() + "' = '" + get() + "'";
	}
	
	//Set-style methods, delegated to set
	
	@Override
	public int size() {
		return this.value.size();
	}

	@Override
	public boolean add(T value) {
		return this.value.add(value);
	}

	@Override
	public void replace(Iterable<T> newContents) {
		this.value.replace(newContents);
	}

	@Override
	public Iterator<T> iterator() {
		return this.value.iterator();
	}

	@Override
	public boolean remove(Object e) {
		return this.value.remove(e);
	}

	@Override
	public boolean contains(Object element) {
		return this.value.contains(element);
	}

}
