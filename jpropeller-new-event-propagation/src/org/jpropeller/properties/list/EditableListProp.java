package org.jpropeller.properties.list;

import java.util.List;

import org.jpropeller.bean.BeanFeatures;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;

/**
 * A {@link ListProp} that can be edited
 * and is editable
 * @author bwebster
 *
 * @param <T>
 * 		The type of value contained in the prop
 */
public interface EditableListProp<T> extends ListProp<T> {
	
	/**
	 * Set a particular property
	 * {@link List#set(int, Object)}
	 * @param index
	 * 		The index of the property to set
	 * @param value
	 * 		The new value
	 * @return
	 * 		The old value
	 */
	public T set(int index, T value);
	
	/**
	 * Add a new property to the end of the list
	 * {@link List#add(Object)}
	 * @param value
	 * 		The value to add
	 */
	public void add(T value);
	
	/**
	 * Clear all property values, then replace them with the new contents,
	 * in the order returned by the iterable. This is done as an "atomic"
	 * change, so only one large change will occur
	 * @param newContents
	 * 		The new contents
	 */
	public void replace(Iterable<T> newContents);
	
	/**
	 * The name of the prop
	 * This is used in the {@link BeanFeatures} to look up {@link Prop}s via {@link BeanFeatures#get(PropName)}
	 * @return
	 * 		Name of the prop
	 */
	public PropName<EditableListProp<T>, T> getName();

	
}