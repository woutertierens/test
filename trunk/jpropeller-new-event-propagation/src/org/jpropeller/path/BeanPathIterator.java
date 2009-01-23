package org.jpropeller.path;

import java.util.Iterator;

import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.GenericProp;
import org.jpropeller.properties.Prop;

/**
 * Interface for extended {@link Iterator} that provides a sequence of
 * {@link GeneralProp}s via the normal {@link Iterator} interface, then
 * provides one final {@link Prop} of a known type P
 * @param <P>
 * 		The type of the final {@link Prop}
 * @param <D>
 * 		The type of value in the final {@link Prop}
 */
public interface BeanPathIterator<P extends GenericProp<D>, D> extends Iterator<GeneralProp<?>>{

	/**
	 * Get the final prop in the iteration - this is only available AFTER
	 * the last element has been returned by the {@link Iterator} interface.
	 * In other words - you should iterate over all the {@link GeneralProp}s
	 * returned by {@link Iterator#next()}, then use this prop as the
	 * final element.
	 * @return
	 * 		The final prop
	 */
	public P finalProp();
	
}
