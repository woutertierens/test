package org.jpropeller.reference.impl;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.path.BeanPath;
import org.jpropeller.path.impl.BeanPathBuilder;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.GenericProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.list.ListProp;
import org.jpropeller.properties.path.impl.PathPropBuilder;
import org.jpropeller.transformer.Transformer;

/**
 * This is a temporary builder object that is used to make a {@link PathReference}
 *
 * Usage is the same as {@link BeanPathBuilder}
 * 
 * @param <R>
 * 		The type of the root bean for the {@link BeanPath} 
 * @param <D> 
 * 		The type of the current destination value we have built to
 * @param <P> 
 * 		The type of final prop reached by the path (e.g. {@link Prop},
 * {@link EditableProp}, {@link ListProp} etc.)
 * @param <T>
 * 		The type of data in the final prop reached by the path
 */
public class PathReferenceBuilder<R extends Bean, D extends Bean, P extends GenericProp<T>, T> {

	R pathRoot;
	PathPropBuilder<R, D, P, T> builder;
	
	protected PathReferenceBuilder(R pathRoot, PathPropBuilder<R, D, P, T> builder) {
		super();
		this.pathRoot = pathRoot;
		this.builder = builder;
	}

	/**
	 * Start a {@link PathReferenceBuilder} that can be used to build a {@link PathReference}
	 * by use of {@link #via(Transformer)} and {@link #to(Transformer)} methods.
	 * @param <R>
	 * 		The type of the root bean for the {@link BeanPath} 
	 * @param <P> 
	 * 		The type of final prop reached by the path (e.g. {@link Prop},
	 * {@link EditableProp}, {@link ListProp} etc.)
	 * @param <T>
	 * 		The type of data in the final prop reached by the path
	 * @param pathRoot
	 * 		The root of the path
	 * @param clazz
	 * 		The class at the end of the path
	 * @return
	 * 		A builder to be used to make an {@link PathReference}
	 */
	public static <R extends Bean, P extends GenericProp<T>, T> PathReferenceBuilder<R, R, P, T> from(R pathRoot, Class<T> clazz) {
		return new PathReferenceBuilder<R, R, P, T>(pathRoot, PathPropBuilder.<R, P, T>from("value", clazz, pathRoot));
	}
	
	/**
	 * Start a {@link PathReferenceBuilder} that can be used to build a {@link PathReference}
	 * by use of {@link #via(Transformer)} and {@link #to(Transformer)} methods.
	 * @param <R>
	 * 		The type of the root bean for the {@link BeanPath} 
	 * @param <P> 
	 * 		The type of final prop reached by the path (e.g. {@link Prop},
	 * {@link EditableProp}, {@link ListProp} etc.)
	 * @param <T>
	 * 		The type of data in the final prop reached by the path
	 * @param name
	 * 		The name of the model prop - must be appropriate type
	 * of name, with string value "value" 
	 * @param pathRoot
	 * 		The root of the path
	 * @return
	 * 		A builder to be used to make an {@link PathReference}
	 */
	public static <R extends Bean, P extends GenericProp<T>, T> PathReferenceBuilder<R, R, P, T> from(PropName<Prop<T>, T> name, R pathRoot) {
		if (!"value".equals(name.getString())) {
			throw new IllegalArgumentException("Value property of PathReference must be 'value'");
		}
		return new PathReferenceBuilder<R, R, P, T>(pathRoot, PathPropBuilder.<R, P, T>from(name, pathRoot));
	}
	
	/**
	 * Produce a {@link PathReference} using the steps used to create this
	 * builder, then the last {@link Transformer} specified
	 * @param lastTransform
	 * 		The last {@link Transformer} in the path
	 * @return
	 * 		The path itself
	 */
	public PathReference<T> to(Transformer<? super D, ? extends P> lastTransform) {
		return new PathReference<T>(builder.to(lastTransform));
	}

	/**
	 * Produce a {@link PathReference} using the steps used to create this
	 * builder, then the last name specified
	 * @param lastName
	 * 		The last {@link PropName} in the path
	 * @return
	 * 		The path itself
	 */
	public PathReference<T> to(PropName<P, T> lastName) {
		return new PathReference<T>(builder.to(lastName));
	}

	/**
	 * Add another transform to this path, and return this
	 * instance (to allow chaining of calls to {@link #via(Transformer)})
	 * @param <E>
	 * 		The type of value in the prop the transform leads to 
	 * @param nextTransform
	 * 		The next {@link Transformer} in the path
	 * @return
	 * 		The path itself
	 */
	public <E extends Bean> PathReferenceBuilder<R, E, P, T> via(Transformer<? super D, ? extends GenericProp<E>> nextTransform) {
		return new PathReferenceBuilder<R, E, P, T>(pathRoot, builder.via(nextTransform));
	}
	/**
	 * Add another name to this path, and return this
	 * instance (to allow chaining of calls to {@link #via(Transformer)})
	 * @param <E> 
	 * 		The type of value in the prop the name leads to
	 * @param <G> 
	 * 		The type of prop the name leads to
	 * @param nextName
	 * 		The next {@link PropName} in the path
	 * @return
	 * 		The path itself
	 */
	public <E extends Bean, G extends GenericProp<E>> PathReferenceBuilder<R, E, P, T> via(PropName<G, E> nextName) {
		return new PathReferenceBuilder<R, E, P, T>(pathRoot, builder.via(nextName));
	}
	
}
