package org.jpropeller.util;

import java.util.LinkedList;
import java.util.List;

/**
 * Converts a delegate {@link Source} of T into a {@link Source} of
 * lists of T, where each list will just contain one element,
 * which is got from the delegate.
 *
 * @param <T>	The type provided by the delegate source
 */
public class SingleInstanceListSource<T> implements Source<List<T>>{

	private final Source<T> delegate;
	
	/**
	 * Create a {@link SingleInstanceListSource}
	 * @param delegate	The delegate - provides instances that will be wrapped
	 * in lists 
	 */
	public SingleInstanceListSource(Source<T> delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public List<T> get() throws NoInstanceAvailableException {
		LinkedList<T> l = new LinkedList<T>();
		l.add(delegate.get());
		return l;
	}

}
