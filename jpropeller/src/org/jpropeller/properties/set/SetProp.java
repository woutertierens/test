package org.jpropeller.properties.set;

import java.util.Set;

import org.jpropeller.bean.BeanFeatures;
import org.jpropeller.collection.ObservableSet;
import org.jpropeller.collection.impl.ObservableSetDefault;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.Prop;

/**
 * A {@link Prop} with zero or more values,
 * that can be iterated or viewed as an 
 * {@link ObservableSetDefault}
 * 
 * @param <T>
 * 		Type of values in the set, also the type of the {@link Prop}
 */
public interface SetProp<T> extends GeneralProp<T>, Iterable<T> {
	
	/**
	 * Get the number of elements in this property.
	 * {@link Set#size()}
	 * @return
	 * 		The number of elements.
	 */
	public int size();
	
	/**
	 * Check whether a value is contained
	 * {@link Set#contains(Object)}
	 * @param element
	 * 		The element to check
	 * @return
	 * 		True if the set contains the element, false otherwise
	 */
	public boolean contains(Object element);
	
	/**
	 * Get the whole {@link Set} of values of the property 
	 * as an {@link ObservableSet}
	 * @return
	 * 		values as a {@link ObservableSet}
	 */
	public ObservableSet<T> get();
	
	/**
	 * The name of the prop
	 * This is used in the {@link BeanFeatures} to look up 
	 * {@link Prop}s via {@link BeanFeatures#get(PropName)}
	 * @return
	 * 		Name of the prop
	 */
	public PropName<? extends SetProp<T>, T> getName();

}