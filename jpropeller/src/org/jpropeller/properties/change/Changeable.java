package org.jpropeller.properties.change;


/**
 * Interface for something that can change, causing a {@link Change}
 */
public interface Changeable {

	/**
	 * Get the features used to interact with a {@link Changeable}
	 * @return
	 * 		The {@link Changeable}'s features
	 */
	public ChangeableFeatures features();

}
