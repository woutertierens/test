package org.jpropeller.path;

import java.util.Iterator;

import org.jpropeller.properties.Prop;

/**
 * Interface for extended {@link Iterator} that provides a sequence of
 * {@link Prop}s via the normal {@link Iterator} interface, then
 * provides one final {@link Prop} containing a value of type D
 * @param <D>		The type of value in the final {@link Prop}
 */
public interface BeanPathIterator<D> extends Iterator<Prop<?>>{

	/**
	 * Get the final prop in the iteration - this is only available AFTER
	 * the last element has been returned by the {@link Iterator} interface.
	 * In other words - you should iterate over all the {@link Prop}s
	 * returned by {@link Iterator#next()}, then use this prop as the
	 * final element.
	 * 
	 * @return			The final prop
	 */
	public Prop<D> finalProp();
	
}
