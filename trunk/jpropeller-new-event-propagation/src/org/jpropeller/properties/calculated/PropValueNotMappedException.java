package org.jpropeller.properties.calculated;

import org.jpropeller.properties.GeneralProp;

/**
 * Exception thrown when there is no value for a prop
 * @author shingoki
 */
public class PropValueNotMappedException extends RuntimeException {
	private static final long serialVersionUID = -7459041852177953835L;
	
	GeneralProp<?> prop;
	
	/**
	 * Create an exception
	 * @param prop
	 * 		The prop without a mapped value
	 */
	public PropValueNotMappedException(GeneralProp<?> prop) {
		super("No value in PropValueMap for " + prop);
		this.prop = prop;
	}
	
	
}
