package org.jpropeller.properties.change;

import org.jpropeller.properties.list.ListProp;
import org.jpropeller.properties.map.MapProp;

/**
 * The types of {@link Change} 
 */
public enum ChangeType {
	/**
	 * Change is the base type, implementing only {@link Change}
	 */
	BASE,
	
	/**
	 * Change applies to a {@link ListProp}
	 */
	LIST,
	
	/**
	 * Change applies to a {@link MapProp}
	 */
	MAP
}
