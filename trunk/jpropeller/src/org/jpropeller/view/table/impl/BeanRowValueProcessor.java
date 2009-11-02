package org.jpropeller.view.table.impl;

/**
 * Processor to allow translation and/or filtering of
 * values for a {@link BeanRowView}
 * @param <R>		The type of row
 * @param <T>		The type of value in the row
 */
public interface BeanRowValueProcessor<R, T> {
	/**
	 * Process a value that will be set into a row
	 * @param row		The row
	 * @param value		The value
	 * @return			The value to be set
	 * @throws			IllegalArgumentException if the value cannot be accepted
	 */
	public T process(R row, T value) throws IllegalArgumentException;
}
