package org.jpropeller.bean;

import java.util.List;

import org.jpropeller.collection.CList;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.change.Changeable;
import org.jpropeller.system.Props;
import org.jpropeller.util.Source;

/**
 * Allows building of a calculated {@link Prop} containing a {@link CList}, 
 * and adding of this {@link Prop} to a containing bean, just
 * call {@link #returning(Source)}
 *
 * @param <T>	The type of value in calculated list
 */
public interface BuildAndAddCalculatedListProp<T> {

	/**
	 * Call this method to produce a {@link Prop} based on the
	 * inputs provided to {@link Props#calculatedListOn(Class, String, Changeable...)},
	 * returning the values produced by the {@link Source}. Note that the {@link Source}
	 * must only use the values of the specified inputs.
	 * @param source	{@link Source} of results of calculation
	 * @return	The new {@link Prop}
	 */
	public Prop<CList<T>> returning(Source<List<T>> source);
}