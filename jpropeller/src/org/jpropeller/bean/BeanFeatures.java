package org.jpropeller.bean;

import java.util.List;

import org.jpropeller.name.GenericPropName;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.change.ChangeableFeatures;

/**
 * Interface allowing access to the various features of a Bean
 */
public interface BeanFeatures extends ChangeableFeatures, Iterable<GeneralProp<?>> {


	/**
	 * Get the {@link GeneralProp} in the {@link Bean} with the specified 
	 * name. If there is no such {@link GeneralProp}, null is returned
	 * @param <P> 
	 * 		The type of {@link GeneralProp} to be found, e.g. {@link EditableProp}
	 * @param <S>
	 * 		The type of data in the {@link GeneralProp} to be found
	 * @param name
	 * 		The name of the {@link GeneralProp}
	 * @return
	 * 		The {@link GeneralProp} with the name, or null if there is 
	 * no such {@link GeneralProp} in the set
	 */
	public <P extends GeneralProp<S>, S> P get(PropName<P, S> name);

	/**
	 * Get the {@link GeneralProp} in the set with the specified name. If there is no
	 * such prop, null is returned
	 * Note that because a {@link GenericPropName} is used rather than a {@link PropName},
	 * the {@link GeneralProp} returned cannot be guaranteed to have a type
	 * EXACTLY matching the type of any other {@link GeneralProp} looked up
	 * with the same {@link GenericPropName}. {@link #get(PropName)} DOES make
	 * this guarantee, by using a {@link PropName} rather than a {@link GenericPropName}
	 * @param <P> 
	 * 		The type of {@link GeneralProp} to be found, e.g. {@link EditableProp}
	 * @param <S>
	 * 		The type of data in the {@link GeneralProp} to be found
	 * @param name
	 * 		The name of the {@link GeneralProp}
	 * @return
	 * 		The {@link GeneralProp} with the name, or null if there is no such 
	 * {@link GeneralProp} in the set
	 */
	public <P extends GeneralProp<S>, S> P getUnsafe(GenericPropName<P, S> name);

	/**
	 * A list of {@link GeneralProp}s in the {@link Bean}, in the order they were added to the {@link Bean}
	 * @return
	 * 		Ordered list of {@link GeneralProp}s in the {@link Bean}
	 */
	public List<GeneralProp<?>> getList();
	
	/**
	 * The Bean to which this {@link BeanFeatures} belongs - it can only belong to one
	 * Bean
	 * @return
	 * 		The Bean to which this {@link BeanFeatures} belongs
	 */
	public Bean getBean();
	
}
