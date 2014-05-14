package org.jpropeller.util;

import javax.swing.Icon;

/**
 * A in interface used to classify a set of T into groups by assigning an int to every T.
 *
 * @param <T>
 */
public interface ListClassifier<T> {

	/**
	 * What is the classifier associated with t? Should be in [0,nClassifiers()[
	 */
	public int classifier(T t);
	/**
	 * What is the total number of classifiers?
	 */
	public int nClassifiers();
	/**
	 * A human-readable name for classifier
	 */
	public String classifierName(int classifier);
	/**
	 * An associated icon (may be null)
	 */
	public Icon classifierIcon(int classifier);

}
