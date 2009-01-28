package org.jpropeller.properties.set;

import java.util.Set;

import org.jpropeller.bean.BeanFeatures;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;

/**
 * A {@link SetProp} that can be edited
 *
 * @param <T>
 * 		Type of values in the set, also the type of the {@link Prop}
 */
public interface EditableSetProp<T> extends SetProp<T> {
	
	/**
	 * Add an element
	 * {@link Set#add(Object)}
	 * @param e
	 * 		The element to add
	 * @return
	 * 		True if the element was added, false if the set
	 * is unaltered. 
	 */
	public boolean add(T e);

	/**
	 * Remove an element
	 * {@link Set#remove(Object)}
	 * @param e
	 * 		The element to remove
	 * @return
	 * 		True if the element was removed, false if the set
	 * is unaltered. 
	 */
	public boolean remove(Object e);

	/**
	 * The name of the prop
	 * This is used in the {@link BeanFeatures} to look up {@link Prop}s via {@link BeanFeatures#get(PropName)}
	 * @return
	 * 		Name of the prop
	 */
	public PropName<EditableSetProp<T>, T> getName();
	
	/**
	 * Clear all property values, then replace them with the new contents,
	 * in the order returned by the iterable. This is done as an "atomic"
	 * change, so only one large change will occur
	 * @param newContents
	 * 		The new contents
	 */
	public void replace(Iterable<T> newContents);

}