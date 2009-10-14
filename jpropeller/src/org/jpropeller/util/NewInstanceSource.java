package org.jpropeller.util;


/**
 * A {@link Source} that provides instances of a {@link Class}
 * via calling its {@link Class#newInstance()} method
 *
 * @param <T>		The type of object provided
 */
public class NewInstanceSource<T> implements Source<T> {
	
	private final Class<T> clazz;
	
	/**
	 * Create a new {@link NewInstanceSource}
	 * @param <T>		The type of instance
	 * @param clazz		The class of instance
	 * 					Must be for a {@link Class} with a default constructor
	 * @return			A new {@link NewInstanceSource}
	 */
	public final static <T> NewInstanceSource<T> create(Class<T> clazz) {
		return new NewInstanceSource<T>(clazz);
	}
	
	/**
	 * Create a {@link NewInstanceSource}
	 * @param clazz		The {@link Class} of which to make new instances
	 * 					Must be for a {@link Class} with a default constructor
	 */
	private NewInstanceSource(Class<T> clazz) {
		super();
		this.clazz = clazz;
	}

	@Override
	public T get() throws NoInstanceAvailableException {
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			throw new NoInstanceAvailableException("Cannot create a new " + clazz.getName());
		} catch (IllegalAccessException e) {
			throw new NoInstanceAvailableException("Cannot create a new " + clazz.getName());
		}
	}
}
