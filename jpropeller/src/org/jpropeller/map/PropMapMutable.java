package org.jpropeller.map;

import org.jpropeller.bean.Bean;
import org.jpropeller.properties.GeneralProp;

/**
 * A {@link PropMap} providing methods that should only
 * be visible to the {@link Bean} using the map to store
 * its properties.
 * 
 * {@link PropMap}s should NOT be exposed as this interface 
 * externally to the {@link Bean}
 */
public interface PropMapMutable extends PropMap {

	/**
	 * Add a prop to the {@link PropMap}
	 * @param <P> 
	 * 		The type of prop
	 * @param <S>
	 * 		Type of data in the prop
	 * @param prop
	 * 		The prop
	 * @return
	 * 		The same prop (for chaining)
	 */
	public <P extends GeneralProp<S>, S> P add(P prop);
	
}
