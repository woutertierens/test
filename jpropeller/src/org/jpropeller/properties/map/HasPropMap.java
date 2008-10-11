package org.jpropeller.properties.map;

import org.jpropeller.map.PropMap;
import org.jpropeller.properties.GeneralProp;
import org.jpropeller.properties.Prop;

import org.jpropeller.bean.Bean;

/**
 * An interface implemented by {@link Bean} and {@link GeneralProp},
 * since they both have a {@link PropMap}.
 * <br>
 * In the case of a {@link Prop} this is the containing {@link PropMap}
 * <br>
 * In the case of a {@link Bean} this is the {@link PropMap} holding the 
 * properties of the {@link Bean}
 * 
 * @author bwebster
 *
 */
public interface HasPropMap {
	
	/**
	 * Get the set of Props associated with the instance
	 * @return
	 * 		The {@link PropMap}
	 */
	public PropMap props();

}
