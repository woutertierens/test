package org.jpropeller.info;

import org.jpropeller.properties.Prop;
import org.jpropeller.properties.list.ListProp;
import org.jpropeller.properties.map.MapProp;
import org.jpropeller.properties.set.SetProp;

/**
 * Describes how values are accessed within a {@link Prop} - 
 * is there a single value, a list, map or set?
 */
public enum PropAccessType {
	/**
	 * The {@link Prop} will implement {@link ListProp} 
	 */
	LIST,
	
	/**
	 * The {@link Prop} will implement {@link MapProp} 
	 */
	MAP,
	
	/**
	 * The {@link Prop} will implement {@link SetProp} 
	 */
	SET,
	
	/**
	 * The {@link Prop} has only a single value, and 
	 * this should be accessed via {@link Prop#get()}.
	 */
	SINGLE;
}
