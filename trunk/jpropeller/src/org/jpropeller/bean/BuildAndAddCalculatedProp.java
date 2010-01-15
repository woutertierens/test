package org.jpropeller.bean;

import org.jpropeller.properties.Prop;
import org.jpropeller.properties.calculated.impl.CalculatedProp;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.system.Props;
import org.jpropeller.util.Source;

/**
 * Allows building of a {@link CalculatedProp} and adding
 * of this {@link CalculatedProp} to a containing bean, just
 * call {@link #returning(Source)}
 *
 * @param <T>	The type of calculated value
 */
public interface BuildAndAddCalculatedProp<T> {

	/**
	 * Call this method to produce a {@link CalculatedProp} based on the
	 * inputs provided to {@link Props#calculated(Class, String, Changeable...)},
	 * returning the values produced by the {@link Source}. Note that the {@link Source}
	 * must only use the values of the specified inputs.
	 * @param source	{@link Source} of results of calculation
	 * @return	The new {@link CalculatedProp}
	 */
	public Prop<T> returning(Source<T> source);
}