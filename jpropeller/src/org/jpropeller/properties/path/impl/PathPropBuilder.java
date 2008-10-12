package org.jpropeller.properties.path.impl;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.path.PathNameList;
import org.jpropeller.path.impl.BeanPathDefault;
import org.jpropeller.path.impl.PathNameListDefault;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.list.ListProp;
import org.jpropeller.transformer.Transformer;
import org.jpropeller.transformer.impl.BeanToBeanPropTransformer;
import org.jpropeller.transformer.impl.BeanToPropTransformer;

/**
 * This is a temporary builder object that is used to make a {@link PathProp}
 * 
 * For example, to make a {@link PathProp} starting from bean b and progressing
 * via properties x, y, z in order, use:
 * <pre>
 * PathProp<String> mirrorOfZ = from(nameOfMirrorOfZ, b).via(x.getName()).via(y.getName).to(z.getName);
 * </pre>
 * 
 * @param <P> 
 * 		The type of final prop reached by the path (e.g. {@link Prop},
 * {@link EditableProp}, {@link ListProp} etc.)
 * @param <T>
 * 		The type of data in the final prop reached by the path
 */
public class PathPropBuilder<P extends Prop<T>, T> {

	PropName<Prop<T>, T> name;
	Bean pathRoot;
	PathNameList list;
	
	protected PathPropBuilder(PropName<Prop<T>, T> name, Bean pathRoot) {
		super();
		this.name = name;
		this.pathRoot = pathRoot;
		list = new PathNameListDefault();
	}

	protected PathPropBuilder(String name, Class<T> clazz, Bean pathRoot) {
		this(PropName.create(name, clazz), pathRoot);
	}

	/**
	 * Start a {@link PathPropBuilder} that can be used to build an {@link PathProp}
	 * by use of {@link #via(Transformer)} and {@link #to(Transformer)} methods.
	 * For example, to make an {@link PathProp} starting from bean b and progressing
	 * via properties x, y, z in order, use:
	 * <pre>
	 * PathProp<String> mirrorOfZ = from(nameOfMirrorOfZ, b).via(x.getName()).via(y.getName).to(z.getName);
	 * </pre>
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
	public static <P extends Prop<T>, T> PathPropBuilder<P, T> from(PropName<Prop<T>, T> name, Bean pathRoot) {
		return new PathPropBuilder<P, T>(name, pathRoot);
	}
	
	/**
	 * Start a {@link PathPropBuilder} that can be used to build a {@link PathProp}
	 * by use of {@link #via(Transformer)} and {@link #to(Transformer)} methods.
	 * For example, to make a {@link PathProp} starting from bean b and progressing
	 * via properties x, y, z in order, use:
	 * <pre>
	 * PathProp<String> mirrorOfZ = from(nameOfMirrorOfZ, b).via(x.getName()).via(y.getName).to(z.getName);
	 * </pre>
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
	public static <P extends Prop<T>, T> PathPropBuilder<P, T> from(String name, Class<T> clazz, Bean pathRoot) {
		return new PathPropBuilder<P, T>(name, clazz, pathRoot);
	}
	
	/**
	 * Produce a {@link BeanPathDefault} using the {@link PropName}s used to create this
	 * object, then the last name specified
	 * @param lastTransform
	 * 		The last {@link Transformer} in the path
	 * @return
	 * 		The path itself
	 */
	public PathProp<T> to(Transformer<? super Bean, ? extends P> lastTransform) {
		BeanPathDefault<P, T> path = new BeanPathDefault<P, T>(list, lastTransform);
		return new PathProp<T>(name, pathRoot, path);
	}
	
	/**
	 * Add another transform to this path, and return this
	 * instance (to allow chaining of calls to {@link #via(Transformer)})
	 * @param nextTransform
	 * 		The next {@link Transformer} in the path
	 * @return
	 * 		The path itself
	 */
	public PathPropBuilder<P, T> via(Transformer<? super Bean, Prop<? extends Bean>> nextTransform) {
		list.add(nextTransform);
		return this;
	}
	
	/**
	 * Produce a {@link BeanPathDefault} using the {@link PropName}s used to create this
	 * object, then the last name specified
	 * @param lastName
	 * 		The last {@link PropName} in the path
	 * @return
	 * 		The path itself
	 */
	public PathProp<T> to(PropName<P, T> lastName) {
		return to(new BeanToPropTransformer<P, T>(lastName));
	}
	
	/**
	 * Add another name to this path, and return this
	 * instance (to allow chaining of calls to {@link #via(Transformer)})
	 * @param nextName
	 * 		The next {@link PropName} in the path
	 * @return
	 * 		The path itself
	 */
	public PathPropBuilder<P, T> via(PropName<? extends Prop<? extends Bean>, ? extends Bean> nextName) {
		return via(new BeanToBeanPropTransformer(nextName));
	}
}
