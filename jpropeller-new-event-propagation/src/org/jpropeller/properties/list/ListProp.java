package org.jpropeller.properties.list;

import java.util.List;

import org.jpropeller.bean.BeanFeatures;
import org.jpropeller.collection.ObservableList;
import org.jpropeller.collection.impl.ObservableListDefault;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.Prop;

/**
 * A {@link Prop} with multiple values indexed by an integer, 
 * which can be viewed as an {@link ObservableListDefault}
 * 
 * The entries in the list may be changed (in an {@link EditableListProp},
 * but the list itself will not change - that is, the {@link ObservableList}
 * returned by get() is always the same instance.
 * 
 * @author bwebster
 *
 * @param <T>
 * 		The type of values in the property
 */
public interface ListProp<T> extends GeneralProp<T>, Iterable<T> {
	
	/**
	 * Get the property value at an index
	 * {@link List#get(int)}
	 * @param index
	 * 		property index
	 * @return
	 * 		the value
	 */
	public T get(int index);

	/**
	 * Get the number of elements indexed by this property.
	 * {@link List#size()}
	 * @return
	 * 		The number of elements.
	 */
	public int size();
	
	/**
	 * A view of the values of the property as an {@link ObservableListDefault}
	 * @return
	 * 		values as a list
	 */
	public ObservableList<T> get();
	
	/**
	 * The name of the prop
	 * This is used in the {@link BeanFeatures} to look up {@link Prop}s via {@link BeanFeatures#get(PropName)}
	 * @return
	 * 		Name of the prop
	 */
	public PropName<? extends ListProp<T>, T> getName();

}