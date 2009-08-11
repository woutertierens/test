package org.jpropeller.bean;

import java.util.List;

import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.ChangeableFeatures;

/**
 * Interface allowing access to the various features of a Bean
 */
public interface BeanFeatures extends ChangeableFeatures, Iterable<Prop<?>> {


	/**
	 * Get the {@link Prop} in the {@link Bean} with the specified 
	 * name. If there is no such {@link Prop}, null is returned
	 * This throws an {@link IllegalArgumentException} if the name specified
	 * has a generic type, since in this case we cannot guarantee the return
	 * type is correct.
	 * 
	 * @param <S>		The type of data in the {@link Prop} to be found
	 * @param name		The name of the {@link Prop}
	 * @return			The {@link Prop} with the name, or null if there is 
	 * 					no such {@link Prop} in the set
	 * @throws 			IllegalArgumentException if the name specified
	 * 					has a generic type, since in this case we cannot 
	 * 					guarantee the return type is correct.
	 */
	public <S> Prop<S> get(PropName<S> name) throws IllegalArgumentException;

	/**
	 * Get the {@link Prop} in the set with the specified name. If there is no
	 * such prop, null is returned
	 * Note that because a generic {@link PropName} will be accepted,
	 * the {@link Prop} returned cannot be guaranteed to have a type
	 * EXACTLY matching the type of any other {@link Prop} looked up
	 * with the same {@link PropName}. {@link #get(PropName)} DOES make
	 * this guarantee, by checking the {@link PropName} specified is not generic
	 * 
	 * @param <S>		The type of data in the {@link Prop} to be found
	 * @param name		The name of the {@link Prop}
	 * @return			The {@link Prop} with the name, or null if there is no such 
	 * 					{@link Prop} in the set
	 */
	public <S> Prop<S> getUnsafe(PropName<S> name);

	/**
	 * A list of {@link Prop}s in the {@link Bean}, in the order they were added to the {@link Bean}
	 * @return			Ordered list of {@link Prop}s in the {@link Bean}
	 */
	public List<Prop<?>> getList();
	
	/**
	 * The Bean to which this {@link BeanFeatures} belongs - it can only belong to one
	 * Bean
	 * @return			The Bean to which this {@link BeanFeatures} belongs
	 */
	public Bean getBean();
	
}
