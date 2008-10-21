package org.jpropeller.properties.path.impl;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.path.BeanPath;
import org.jpropeller.path.impl.BeanPathBuilder;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.GenericProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.list.ListProp;
import org.jpropeller.transformer.Transformer;

/**
 * This is a temporary builder object that is used to make a {@link PathProp}
 * 
 * For example, to make a {@link PathProp} starting from bean b and progressing
 * via properties x, y, z in order, use:
 * <pre>
 * PathProp<String> mirrorOfZ = from(nameOfMirrorOfZ, b).via(x.getName()).via(y.getName).to(z.getName);
 * </pre>
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
public class PathPropBuilder<R extends Bean, D extends Bean, P extends GenericProp<T>, T> {

	PropName<Prop<T>, T> name;
	R pathRoot;
	BeanPathBuilder<R, D> builder;
	
	protected PathPropBuilder(PropName<Prop<T>, T> name, R pathRoot, BeanPathBuilder<R, D> builder) {
		super();
		this.name = name;
		this.pathRoot = pathRoot;
		this.builder = builder;
	}

	protected PathPropBuilder(String name, Class<T> clazz, R pathRoot, BeanPathBuilder<R, D> builder) {
		this(PropName.create(name, clazz), pathRoot, builder);
	}

	/**
	 * Start a {@link PathPropBuilder} that can be used to build an {@link PathProp}
	 * by use of {@link #via(Transformer)} and {@link #to(Transformer)} methods.
	 * For example, to make an {@link PathProp} starting from bean b and progressing
	 * via properties x, y, z in order, use:
	 * <pre>
	 * PathProp<String> mirrorOfZ = from(nameOfMirrorOfZ, b).via(x.getName()).via(y.getName).to(z.getName);
	 * </pre>
	 * @param <R>
	 * 		The type of the root bean for the {@link BeanPath} 
	 * @param <P> 
	 * 		The type of final prop reached by the path (e.g. {@link Prop},
	 * {@link EditableProp}, {@link ListProp} etc.)
	 * @param <T>
	 * 		The type of data in the final prop reached by the path
	 * @param name
	 * 		The name of the {@link PathProp} to build
	 * @param pathRoot
	 * 		The root of the path
	 * @return
	 * 		A builder to be used to make an {@link PathProp}
	 */
	public static <R extends Bean, P extends Prop<T>, T> PathPropBuilder<R, R, P, T> from(PropName<Prop<T>, T> name, R pathRoot) {
		return new PathPropBuilder<R, R, P, T>(name, pathRoot, BeanPathBuilder.<R>create());
	}
	
	/**
	 * Start a {@link PathPropBuilder} that can be used to build a {@link PathProp}
	 * by use of {@link #via(Transformer)} and {@link #to(Transformer)} methods.
	 * For example, to make a {@link PathProp} starting from bean b and progressing
	 * via properties x, y, z in order, use:
	 * <pre>
	 * PathProp<String> mirrorOfZ = from(nameOfMirrorOfZ, b).via(x.getName()).via(y.getName).to(z.getName);
	 * </pre>
	 * @param <R>
	 * 		The type of the root bean for the {@link BeanPath} 
	 * @param <P> 
	 * 		The type of final prop reached by the path (e.g. {@link Prop},
	 * {@link EditableProp}, {@link ListProp} etc.)
	 * @param <T>
	 * @param name
	 * 		The string name of the {@link PathProp} to build
	 * @param clazz
	 * 		The type of data in the final prop reached by the path
	 * @param pathRoot
	 * 		The root of the path
	 * @return
	 * 		A builder to be used to make a {@link PathProp}
	 */
	public static <R extends Bean, P extends Prop<T>, T> PathPropBuilder<R, R, P, T> from(String name, Class<T> clazz, R pathRoot) {
		return new PathPropBuilder<R, R, P, T>(name, clazz, pathRoot, BeanPathBuilder.<R>create());
	}
	
	/**
	 * Produce a {@link PathProp} using the steps used to create this
	 * builder, then the last {@link Transformer} specified
	 * @param lastTransform
	 * 		The last {@link Transformer} in the path
	 * @return
	 * 		The path itself
	 */
	public PathProp<R, T> to(Transformer<? super D, ? extends P> lastTransform) {
		return new PathProp<R, T>(name, pathRoot, builder.to(lastTransform));
	}

	/**
	 * Produce a {@link PathProp} using the steps used to create this
	 * builder, then the last name specified
	 * @param lastName
	 * 		The last {@link PropName} in the path
	 * @return
	 * 		The path itself
	 */
	public PathProp<R, T> to(PropName<P, T> lastName) {
		return new PathProp<R, T>(name, pathRoot, builder.to(lastName));
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
	public <E extends Bean> PathPropBuilder<R, E, P, T> via(Transformer<? super D, ? extends GenericProp<E>> nextTransform) {
		return new PathPropBuilder<R, E, P, T>(name, pathRoot, builder.via(nextTransform));
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
	public <E extends Bean, G extends GenericProp<E>> PathPropBuilder<R, E, P, T> via(PropName<G, E> nextName) {
		return new PathPropBuilder<R, E, P, T>(name, pathRoot, builder.via(nextName));
	}
	
	
}
