package org.jpropeller.reference.impl;

import org.jpropeller.bean.Bean;
import org.jpropeller.collection.CList;
import org.jpropeller.name.PropName;
import org.jpropeller.path.BeanPath;
import org.jpropeller.path.Paths;
import org.jpropeller.path.impl.BeanPathBuilder;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.list.ListProp;
import org.jpropeller.properties.path.impl.PathPropBuilder;
import org.jpropeller.properties.values.impl.ReadOnlyProcessor;
import org.jpropeller.reference.Reference;
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
 * @param <T>
 * 		The type of data in the final prop reached by the path
 */
public class PathReferenceBuilder<R extends Bean, D extends Bean, T> {

	R pathRoot;
	PathPropBuilder<R, D, T> builder;
	Class<T> clazz;
	
	protected PathReferenceBuilder(R pathRoot, PathPropBuilder<R, D, T> builder, Class<T> clazz) {
		super();
		this.pathRoot = pathRoot;
		this.builder = builder;
		this.clazz = clazz;
	}

	/**
	 * Start a {@link PathReferenceBuilder} that can be used to build a {@link PathReference}
	 * by use of {@link #via(Transformer)} and {@link #to(Transformer)} methods.
	 * The {@link PathReference} will have a read-only {@link Reference#value()}.
	 * @param <R>
	 * 		The type of the root bean for the {@link BeanPath} 
	 * @param <T>
	 * 		The type of data in the final prop reached by the path
	 * @param pathRoot
	 * 		The root of the path
	 * @param clazz
	 * 		The class at the end of the path
	 * @return
	 * 		A builder to be used to make an {@link PathReference}
	 */
	public static <R extends Bean, T> PathReferenceBuilder<R, R, T> from(R pathRoot, Class<T> clazz) {
		return new PathReferenceBuilder<R, R, T>(
				pathRoot, 
				PathPropBuilder.<R, T>from("value", clazz, pathRoot, ReadOnlyProcessor.<T>get()),
				clazz
				);
	}
	
	/**
	 * Start a {@link PathReferenceBuilder} that can be used to build a {@link PathReference}
	 * by use of {@link #via(Transformer)} and {@link #to(Transformer)} methods.
	 * The final prop in the path must be to a {@link CList}
	 * @param <R>
	 * 		The type of the root bean for the {@link BeanPath} 
	 * @param <T>
	 * 		The type of data in the final prop reached by the path
	 * @param pathRoot
	 * 		The root of the path
	 * @param clazz
	 * 		The class at the end of the path
	 * @return
	 * 		A builder to be used to make an {@link PathReference}
	 */
	public static <R extends Bean, T> PathReferenceBuilder<R, R, CList<T>> listFrom(R pathRoot, Class<T> clazz) {
		return from(PropName.createList("value", clazz), pathRoot);
	}
	
	/**
	 * Start a {@link PathReferenceBuilder} that can be used to build a {@link PathReference}
	 * by use of {@link #via(Transformer)} and {@link #to(Transformer)} methods.
	 * The final prop in the path must be to a {@link CList}
	 * The path will start from the specified {@link Reference}, and initially go to
	 * the value of that {@link Reference}. The path can then be extended on from
	 * the value.
	 * @param <M>
	 * 		The type of value in the reference at the root of the path
	 * @param <R>
	 * 		The type of the root bean for the {@link BeanPath} 
	 * @param <T>
	 * 		The type of data in the final prop reached by the path
	 * @param reference
	 * 		The reference at the root of the path
	 * @param clazz
	 * 		The class at the end of the path
	 * @return
	 * 		A builder to be used to make an {@link PathReference}
	 */
	public static <M extends Bean, R extends Reference<M>, T> PathReferenceBuilder<R, M, CList<T>> listFromRef(R reference, Class<T> clazz) {
		return from(PropName.createList("value", clazz), reference).via(Paths.modelToValue(reference));
	}
	
	/**
	 * Start a {@link PathReferenceBuilder} that can be used to build a {@link PathReference}
	 * by use of {@link #via(Transformer)} and {@link #to(Transformer)} methods.
	 * The path will start from the specified {@link Reference}, and initially go to
	 * the value of that {@link Reference}. The path can then be extended on from
	 * the value.
	 * @param <M>
	 * 		The type of value in the reference at the root of the path
	 * @param <R>
	 * 		The type of the root bean for the {@link BeanPath} 
	 * @param <T>
	 * 		The type of data in the final prop reached by the path
	 * @param reference
	 * 		The reference at the root of the path
	 * @param clazz
	 * 		The class at the end of the path
	 * @return
	 * 		A builder to be used to make an {@link PathReference}
	 */
	public static <M extends Bean, R extends Reference<M>, T> PathReferenceBuilder<R, M, T> fromRef(R reference, Class<T> clazz) {
		return from(reference, clazz).via(Paths.modelToValue(reference));
	}
	
	/**
	 * Start a {@link PathReferenceBuilder} that can be used to build a {@link PathReference}
	 * by use of {@link #via(Transformer)} and {@link #to(Transformer)} methods.
	 * @param <R>
	 * 		The type of the root bean for the {@link BeanPath} 
	 * @param <P> 
	 * 		The type of final prop reached by the path (e.g. {@link Prop},
	 * {@link Prop}, {@link ListProp} etc.)
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
	public static <R extends Bean, P extends Prop<T>, T> PathReferenceBuilder<R, R, T> from(PropName<T> name, R pathRoot) {
		if (!"value".equals(name.getString())) {
			throw new IllegalArgumentException("Value property of PathReference must be 'value'");
		}
		return new PathReferenceBuilder<R, R, T>(
				pathRoot, 
				PathPropBuilder.<R, T>from(name, pathRoot, ReadOnlyProcessor.<T>get()),
				name.getPropClass());
	}
	
	/**
	 * Produce a {@link PathReference} using the steps used to create this
	 * builder, then the last {@link Transformer} specified
	 * @param lastTransform
	 * 		The last {@link Transformer} in the path
	 * @return
	 * 		The path itself
	 */
	public PathReference<T> to(Transformer<? super D, ? extends Prop<T>> lastTransform) {
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
	public PathReference<T> to(PropName<T> lastName) {
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
	public <E extends Bean> PathReferenceBuilder<R, E, T> via(Transformer<? super D, ? extends Prop<E>> nextTransform) {
		return new PathReferenceBuilder<R, E, T>(pathRoot, builder.via(nextTransform), clazz);
	}
	/**
	 * Add another name to this path, and return this
	 * instance (to allow chaining of calls to {@link #via(Transformer)})
	 * @param <E> 
	 * 		The type of value in the prop the name leads to
	 * @param nextName
	 * 		The next {@link PropName} in the path
	 * @return
	 * 		The path itself
	 */
	public <E extends Bean> PathReferenceBuilder<R, E, T> via(PropName<E> nextName) {
		return new PathReferenceBuilder<R, E, T>(pathRoot, builder.via(nextName), clazz);
	}
	
}
