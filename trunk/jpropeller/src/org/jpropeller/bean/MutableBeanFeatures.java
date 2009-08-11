package org.jpropeller.bean;

import org.jpropeller.properties.Prop;

/**
 * A {@link BeanFeatures} providing methods that should only
 * be visible to the {@link Bean} itself that is using the features
 * 
 * {@link BeanFeatures} should NOT be exposed as this interface 
 * externally to the {@link Bean}
 */
public interface MutableBeanFeatures extends BeanFeatures {

	/**
	 * Add a prop to the {@link Bean}
	 * @param <P> 
	 * 		The type of prop
	 * @param <S>
	 * 		Type of data in the prop
	 * @param prop
	 * 		The prop
	 * @return
	 * 		The same prop (for chaining)
	 */
	public <P extends Prop<S>, S> P add(P prop);
	
}
