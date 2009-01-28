package org.jpropeller.info;

import org.jpropeller.properties.Prop;
import org.jpropeller.properties.list.ListProp;
import org.jpropeller.properties.map.MapProp;
import org.jpropeller.properties.set.SetProp;

/**
 * Describes how values are accessed within a {@link Prop} - 
 * is there a single value, a list or a map?
 */
public enum PropAccessType {
	/**
	 * The {@link Prop} will implement {@link ListProp}, and 
	 * should be accessed via {@link ListProp#get(int)}. 
	 */
	LIST,
	
	/**
	 * The {@link Prop} will implement {@link MapProp}, and 
	 * should be accessed via {@link MapProp#get(Object)}. 
	 */
	MAP,
	
	/**
	 * The {@link Prop} will implement {@link SetProp}, and 
	 * should be accessed via {@link SetProp} iterator. 
	 */
	SET,
	
	/**
	 * The {@link Prop} has only a single value, and 
	 * this should be accessed via {@link Prop#get()}.
	 */
	SINGLE;
}
