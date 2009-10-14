package org.jpropeller.properties.path.impl;

import org.jpropeller.bean.Bean;
import org.jpropeller.collection.CList;
import org.jpropeller.collection.CMap;
import org.jpropeller.name.PropName;
import org.jpropeller.path.BeanPath;
import org.jpropeller.path.Paths;
import org.jpropeller.path.impl.BeanPathBuilder;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.values.ValueProcessor;
import org.jpropeller.properties.values.impl.ReadOnlyProcessor;
import org.jpropeller.reference.Reference;
import org.jpropeller.transformer.Transformer;

/**
 * This is a temporary builder object that is used to make a {@link PathProp}
 * 
 * For example, to make an {@link PathProp} starting from bean b and progressing
 * via properties x, y, z in order, use:
 * <pre>
 * PathProp &lt;String&gt; mirrorOfZ = from(nameOfMirrorOfZ, b).via(x.getName()).via(y.getName).to(z.getName);
 * </pre>
 * @param <R>
 * 		The type of the root bean for the {@link BeanPath} 
 * @param <D> 
 * 		The type of the current destination value we have built to
 * @param <T>
 * 		The type of data in the final prop reached by the path
 */
public class PathPropBuilder<R extends Bean, D extends Bean, T> {

	protected PropName<T> name;
	protected R pathRoot;
	protected BeanPathBuilder<R, D> builder;
	protected ValueProcessor<T> processor;
	
	protected PathPropBuilder(PropName<T> name, R pathRoot, BeanPathBuilder<R, D> builder, ValueProcessor<T> processor) {
		super();
		this.name = name;
		this.pathRoot = pathRoot;
		this.builder = builder;
		this.processor = processor;
	}

	protected PathPropBuilder(String name, Class<T> clazz, R pathRoot, BeanPathBuilder<R, D> builder, ValueProcessor<T> processor) {
		this(PropName.create(clazz, name), pathRoot, builder, processor);
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
	 * @param <T>
	 * 		The type of data in the final prop reached by the path
	 * @param name
	 * 		The name of the {@link PathProp} to build
	 * @param pathRoot
	 * 		The root of the path
	 * @param processor
	 * 		The {@link ValueProcessor} for the resulting {@link PathProp}
	 * @return
	 * 		A builder to be used to make an {@link PathProp}
	 */
	public static <R extends Bean, T> PathPropBuilder<R, R, T> from(PropName<T> name, R pathRoot, ValueProcessor<T> processor) {
		return new PathPropBuilder<R, R, T>(name, pathRoot, BeanPathBuilder.<R>create(), processor);
	}
	
	/**
	 * Start a {@link PathPropBuilder} that can be used to build an {@link PathProp}
	 * by use of {@link #via(Transformer)} and {@link #to(Transformer)} methods.
	 * For example, to make an {@link PathProp} starting from bean b and progressing
	 * via properties x, y, z in order, use:
	 * <pre>
	 * PathProp<String> mirrorOfZ = from(nameOfMirrorOfZ, b).via(x.getName()).via(y.getName).to(z.getName);
	 * </pre>
	 * The path must end at a {@link Prop} containing a {@link CList} of type T
	 * @param <R>
	 * 		The type of the root bean for the {@link BeanPath} 
	 * @param <T>
	 * 		The type of data in the {@link CList} in the final {@link Prop} reached by the path
	 * @param name
	 * 		The name of the {@link PathProp} to build
	 * @param pathRoot
	 * 		The root of the path
	 * @param processor
	 * 		The {@link ValueProcessor} for the resulting {@link PathProp}
	 * @return
	 * 		A builder to be used to make an {@link PathProp}
	 */
	public static <R extends Bean, T> PathPropBuilder<R, R, CList<T>> listFrom(PropName<CList<T>> name, R pathRoot, ValueProcessor<CList<T>> processor) {
		return new PathPropBuilder<R, R, CList<T>>(name, pathRoot, BeanPathBuilder.<R>create(), processor);
	}
	
	/**
	 * Start a {@link PathPropBuilder} that can be used to build a {@link PathProp}
	 * by use of {@link #via(Transformer)} and {@link #to(Transformer)} methods.
	 * For example, to make a {@link PathProp} starting from bean b and progressing
	 * via properties x, y, z in order, use:
	 * <pre>
	 * PathProp<String> mirrorOfZ = from(nameOfMirrorOfZ, b).via(x.getName()).via(y.getName).to(z.getName);
	 * </pre>
	 * The path must end at a {@link Prop} containing a {@link CList} of type T
	 * @param clazz
	 * 		The type of data in the final prop reached by the path
	 * @param name
	 * 		The string name of the {@link PathProp} to build
	 * @param pathRoot
	 * 		The root of the path
	 * @param processor
	 * 		The {@link ValueProcessor} for the resulting {@link PathProp}
	 * @param <R>
	 * 		The type of the root bean for the {@link BeanPath} 
	 * @param <T>
	 * 		The type of data in the {@link CList} in the final {@link Prop} reached by the path
	 * @return
	 * 		A builder to be used to make a {@link PathProp}
	 */
	public static <R extends Bean, T> PathPropBuilder<R, R, CList<T>> listFrom(Class<T> clazz, String name, R pathRoot, ValueProcessor<CList<T>> processor) {
		return new PathPropBuilder<R, R, CList<T>>(PropName.createList(clazz, name), pathRoot, BeanPathBuilder.<R>create(), processor);
	}
	
	/**
	 * Start a {@link PathPropBuilder} that can be used to build a {@link PathProp}
	 * by use of {@link #via(Transformer)} and {@link #to(Transformer)} methods.
	 * For example, to make a {@link PathProp} starting from bean b and progressing
	 * via properties x, y, z in order, use:
	 * <pre>
	 * PathProp<String> mirrorOfZ = from(nameOfMirrorOfZ, b).via(x.getName()).via(y.getName).to(z.getName);
	 * </pre>
	 * The path must end at a {@link Prop} containing a {@link CList} of type T
	 * The name of the {@link Prop} will be "pathProp", and it will be read only
	 * @param <R>
	 * 		The type of the root bean for the {@link BeanPath} 
	 * @param <T>
	 * 		The type of data in the {@link CList} in the final {@link Prop} reached by the path
	 * @param clazz
	 * 		The type of data in the final prop reached by the path
	 * @param pathRoot
	 * 		The root of the path
	 * @return
	 * 		A builder to be used to make a {@link PathProp}
	 */
	public static <R extends Bean, T> PathPropBuilder<R, R, CList<T>> listFrom(Class<T> clazz, R pathRoot) {
		return new PathPropBuilder<R, R, CList<T>>(PropName.createList(clazz, "pathProp"), pathRoot, BeanPathBuilder.<R>create(), ReadOnlyProcessor.<CList<T>>get());
	}
	
	/**
	 * Start a {@link PathPropBuilder} that can be used to build a {@link PathProp}
	 * by use of {@link #via(Transformer)} and {@link #to(Transformer)} methods.
	 * For example, to make a {@link PathProp} starting from bean b and progressing
	 * via properties x, y, z in order, use:
	 * <pre>
	 * PathProp<String> mirrorOfZ = from(nameOfMirrorOfZ, b).via(x.getName()).via(y.getName).to(z.getName);
	 * </pre>
	 * The path must end at a {@link Prop} containing a {@link CList} of type T
	 * Path must start from a {@link Reference}, and will then go to the value of that
	 * reference.
	 * @param clazz
	 * 		The type of data in the final prop reached by the path
	 * @param name
	 * 		The string name of the {@link PathProp} to build
	 * @param reference
	 * 		The root of the path
	 * @param processor
	 * 		The {@link ValueProcessor} for the resulting {@link PathProp}
	 * @param <M>
	 * 		The type of value in the reference
	 * @param <R>
	 * 		The type of the root bean for the {@link BeanPath} 
	 * @param <T>
	 * 		The type of data in the {@link CList} in the final {@link Prop} reached by the path
	 * @return
	 * 		A builder to be used to make a {@link PathProp}
	 */
	public static <M extends Bean, R extends Reference<M>, T> PathPropBuilder<R, M, CList<T>> listFromRef(Class<T> clazz, String name, R reference, ValueProcessor<CList<T>> processor) {
		return new PathPropBuilder<R, R, CList<T>>(PropName.createList(clazz, name), reference, BeanPathBuilder.<R>create(), processor).via(Paths.modelToValue(reference));
	}
	
	/**
	 * Start a {@link PathPropBuilder} that can be used to build a {@link PathProp}
	 * by use of {@link #via(Transformer)} and {@link #to(Transformer)} methods.
	 * For example, to make a {@link PathProp} starting from bean b and progressing
	 * via properties x, y, z in order, use:
	 * <pre>
	 * PathProp<String> mirrorOfZ = from(nameOfMirrorOfZ, b).via(x.getName()).via(y.getName).to(z.getName);
	 * </pre>
	 * The path must end at a {@link Prop} containing a {@link CList} of type T
	 * Path must start from a {@link Reference}, and will then go to the value of that
	 * reference.
	 * The name of the {@link Prop} will be "pathProp", and it will be read only
	 * @param <M>
	 * 		The type of value in the reference
	 * @param <R>
	 * 		The type of the root bean for the {@link BeanPath} 
	 * @param <T>
	 * 		The type of data in the {@link CList} in the final {@link Prop} reached by the path
	 * @param clazz
	 * 		The type of data in the final prop reached by the path
	 * @param reference
	 * 		The root of the path
	 * @return
	 * 		A builder to be used to make a {@link PathProp}
	 */
	public static <M extends Bean, R extends Reference<M>, T> PathPropBuilder<R, M, CList<T>> listFromRef(Class<T> clazz, R reference) {
		return new PathPropBuilder<R, R, CList<T>>(PropName.createList(clazz, "pathProp"), reference, BeanPathBuilder.<R>create(), ReadOnlyProcessor.<CList<T>>get()).via(Paths.modelToValue(reference));
	}
	
	/**
	 * Start a {@link PathPropBuilder} that can be used to build a {@link PathProp}
	 * by use of {@link #via(Transformer)} and {@link #to(Transformer)} methods.
	 * For example, to make a {@link PathProp} starting from bean b and progressing
	 * via properties x, y, z in order, use:
	 * <pre>
	 * PathProp<String> mirrorOfZ = from(nameOfMirrorOfZ, b).via(x.getName()).via(y.getName).to(z.getName);
	 * </pre>
	 * The path must end at a {@link Prop} containing a {@link CList} of type T
	 * Path must start from a {@link Reference}, and will then go to the value of that
	 * reference.
	 * @param keyClass
	 * 		The type of key in the final map prop reached by the path
	 * @param valueClass
	 * 		The type of value in the final map prop reached by the path
	 * @param name
	 * 		The string name of the {@link PathProp} to build
	 * @param reference
	 * 		The root of the path
	 * @param processor
	 * 		The {@link ValueProcessor} for the resulting {@link PathProp}
	 * @param <M>
	 * 		The type of value in the reference
	 * @param <R>
	 * 		The type of the root bean for the {@link BeanPath} 
	 * @param <K>
	 * 		The type of key in the {@link CMap} in the final {@link Prop} reached by the path
	 * @param <V>
	 * 		The type of value in the {@link CMap} in the final {@link Prop} reached by the path
	 * @return
	 * 		A builder to be used to make a {@link PathProp}
	 */
	public static <M extends Bean, R extends Reference<M>, K, V> PathPropBuilder<R, M, CMap<K, V>> mapFromRef(Class<K> keyClass, Class<V> valueClass, String name, R reference, ValueProcessor<CMap<K, V>> processor) {
		return new PathPropBuilder<R, R, CMap<K, V>>(PropName.createMap(keyClass, valueClass, name), reference, BeanPathBuilder.<R>create(), processor).via(Paths.modelToValue(reference));
	}
	
	/**
	 * Start a {@link PathPropBuilder} that can be used to build a {@link PathProp}
	 * by use of {@link #via(Transformer)} and {@link #to(Transformer)} methods.
	 * For example, to make a {@link PathProp} starting from bean b and progressing
	 * via properties x, y, z in order, use:
	 * <pre>
	 * PathProp<String> mirrorOfZ = from(nameOfMirrorOfZ, b).via(x.getName()).via(y.getName).to(z.getName);
	 * </pre>
	 * The path must end at a {@link Prop} containing a {@link CList} of type T
	 * Path must start from a {@link Reference}, and will then go to the value of that
	 * reference.
	 * The name of the {@link Prop} will be "pathProp", and it will be read only
	 * @param <M>
	 * 		The type of value in the reference
	 * @param <R>
	 * 		The type of the root bean for the {@link BeanPath} 
	 * @param <K>
	 * 		The type of key in the {@link CMap} in the final {@link Prop} reached by the path
	 * @param <V>
	 * 		The type of value in the {@link CMap} in the final {@link Prop} reached by the path
	 * @param keyClass
	 * 		The type of key in the final map prop reached by the path
	 * @param valueClass
	 * 		The type of value in the final map prop reached by the path
	 * @param reference
	 * 		The root of the path
	 * @return
	 * 		A builder to be used to make a {@link PathProp}
	 */
	public static <M extends Bean, R extends Reference<M>, K, V> PathPropBuilder<R, M, CMap<K, V>> mapFromRef(Class<K> keyClass, Class<V> valueClass, R reference) {
		return new PathPropBuilder<R, R, CMap<K, V>>(PropName.createMap(keyClass, valueClass, "pathProp"), reference, BeanPathBuilder.<R>create(), ReadOnlyProcessor.<CMap<K,V>>get()).via(Paths.modelToValue(reference));
	}
	
	/**
	 * Start a {@link PathPropBuilder} that can be used to build a {@link PathProp}
	 * by use of {@link #via(Transformer)} and {@link #to(Transformer)} methods.
	 * For example, to make a {@link PathProp} starting from bean b and progressing
	 * via properties x, y, z in order, use:
	 * <pre>
	 * PathProp<String> mirrorOfZ = from(nameOfMirrorOfZ, b).via(x.getName()).via(y.getName).to(z.getName);
	 * </pre>
	 * The path must end at a {@link Prop} containing a {@link CList} of type T
	 * @param keyClass
	 * 		The type of key in the final map prop reached by the path
	 * @param valueClass
	 * 		The type of value in the final map prop reached by the path
	 * @param name
	 * 		The string name of the {@link PathProp} to build
	 * @param pathRoot
	 * 		The root of the path
	 * @param processor
	 * 		The {@link ValueProcessor} for the resulting {@link PathProp}
	 * @param <R>
	 * 		The type of the root bean for the {@link BeanPath} 
	 * @param <K>
	 * 		The type of key in the {@link CMap} in the final {@link Prop} reached by the path
	 * @param <V>
	 * 		The type of value in the {@link CMap} in the final {@link Prop} reached by the path
	 * @return
	 * 		A builder to be used to make a {@link PathProp}
	 */
	public static <R extends Bean, K, V> PathPropBuilder<R, R, CMap<K, V>> mapFrom(Class<K> keyClass, Class<V> valueClass, String name, R pathRoot, ValueProcessor<CMap<K, V>> processor) {
		return new PathPropBuilder<R, R, CMap<K, V>>(PropName.createMap(keyClass, valueClass, name), pathRoot, BeanPathBuilder.<R>create(), processor);
	}
	
	/**
	 * Start a {@link PathPropBuilder} that can be used to build a {@link PathProp}
	 * by use of {@link #via(Transformer)} and {@link #to(Transformer)} methods.
	 * For example, to make a {@link PathProp} starting from bean b and progressing
	 * via properties x, y, z in order, use:
	 * <pre>
	 * PathProp<String> mirrorOfZ = from(nameOfMirrorOfZ, b).via(x.getName()).via(y.getName).to(z.getName);
	 * </pre>
	 * The path must end at a {@link Prop} containing a {@link CList} of type T
	 * The name of the {@link Prop} will be "pathProp", and it will be read only
	 * @param <R>
	 * 		The type of the root bean for the {@link BeanPath} 
	 * @param <K>
	 * 		The type of key in the {@link CMap} in the final {@link Prop} reached by the path
	 * @param <V>
	 * 		The type of value in the {@link CMap} in the final {@link Prop} reached by the path
	 * @param keyClass
	 * 		The type of key in the final map prop reached by the path
	 * @param valueClass
	 * 		The type of value in the final map prop reached by the path
	 * @param pathRoot
	 * 		The root of the path
	 * @return
	 * 		A builder to be used to make a {@link PathProp}
	 */
	public static <R extends Bean, K, V> PathPropBuilder<R, R, CMap<K, V>> mapFrom(Class<K> keyClass, Class<V> valueClass, R pathRoot) {
		return new PathPropBuilder<R, R, CMap<K, V>>(PropName.createMap(keyClass, valueClass, "pathProp"), pathRoot, BeanPathBuilder.<R>create(), ReadOnlyProcessor.<CMap<K,V>>get());
	}
	
	/**
	 * Start a {@link PathPropBuilder} that can be used to build a {@link PathProp}
	 * by use of {@link #via(Transformer)} and {@link #to(Transformer)} methods.
	 * For example, to make a {@link PathProp} starting from bean b and progressing
	 * via properties x, y, z in order, use:
	 * <pre>
	 * PathProp<String> mirrorOfZ = from(nameOfMirrorOfZ, b).via(x.getName()).via(y.getName).to(z.getName);
	 * </pre>
	 * Path must start from a {@link Reference}, and will then go to the value of that
	 * reference.
	 * @param clazz
	 * 		The type of data in the final prop reached by the path
	 * @param name
	 * 		The string name of the {@link PathProp} to build
	 * @param reference
	 * 		The root of the path
	 * @param processor
	 * 		The {@link ValueProcessor} for the resulting {@link PathProp}
	 * @param <M>
	 * 		The type of value in the reference
	 * @param <R>
	 * 		The type of the root bean for the {@link BeanPath} 
	 * @param <T>
	 * 		The type of value in the {@link Prop} at the end of the path
	 * @return
	 * 		A builder to be used to make a {@link PathProp}
	 */
	public static <M extends Bean, R extends Reference<M>, T> PathPropBuilder<R, M, T> fromRef(Class<T> clazz, String name, R reference, ValueProcessor<T> processor) {
		return new PathPropBuilder<R, R, T>(name, clazz, reference, BeanPathBuilder.<R>create(), processor).via(Paths.modelToValue(reference));
	}

	/**
	 * Start a {@link PathPropBuilder} that can be used to build a {@link PathProp}
	 * by use of {@link #via(Transformer)} and {@link #to(Transformer)} methods.
	 * For example, to make a {@link PathProp} starting from bean b and progressing
	 * via properties x, y, z in order, use:
	 * <pre>
	 * PathProp<String> mirrorOfZ = from(nameOfMirrorOfZ, b).via(x.getName()).via(y.getName).to(z.getName);
	 * </pre>
	 * Path must start from a {@link Reference}, and will then go to the value of that
	 * reference.
	 * The name of the {@link Prop} will be "pathProp", and it will be read only
	 * @param <M>
	 * 		The type of value in the reference
	 * @param <R>
	 * 		The type of the root bean for the {@link BeanPath} 
	 * @param <T>
	 * 		The type of value in the {@link Prop} at the end of the path
	 * @param clazz
	 * 		The type of data in the final prop reached by the path
	 * @param reference
	 * 		The root of the path
	 * @return
	 * 		A builder to be used to make a {@link PathProp}
	 */
	public static <M extends Bean, R extends Reference<M>, T> PathPropBuilder<R, M, T> fromRef(Class<T> clazz, R reference) {
		return new PathPropBuilder<R, R, T>("pathProp", clazz, reference, BeanPathBuilder.<R>create(), ReadOnlyProcessor.<T>get()).via(Paths.modelToValue(reference));
	}

	/**
	 * Start a {@link PathPropBuilder} that can be used to build an {@link PathProp}
	 * by use of {@link #via(Transformer)} and {@link #to(Transformer)} methods.
	 * For example, to make an {@link PathProp} starting from bean b and progressing
	 * via properties x, y, z in order, use:
	 * <pre>
	 * PathProp<String> mirrorOfZ = from(nameOfMirrorOfZ, b).via(x.getName()).via(y.getName).to(z.getName);
	 * </pre>
	 * Path must start from a {@link Reference}, and will then go to the value of that
	 * reference.
	 * @param <M>
	 * 		The type of value in the reference
	 * @param <R>
	 * 		The type of the root reference for the {@link BeanPath} 
	 * @param <T>
	 * 		The type of data in the final prop reached by the path
	 * @param name
	 * 		The name of the {@link PathProp} to build
	 * @param reference
	 * 		The root of the path
	 * @param processor
	 * 		The {@link ValueProcessor} for the resulting {@link PathProp}
	 * @return
	 * 		A builder to be used to make an {@link PathProp}
	 */
	public static <M extends Bean, R extends Reference<M>, T> PathPropBuilder<R, M, T> fromRef(PropName<T> name, R reference, ValueProcessor<T> processor) {
		return new PathPropBuilder<R, R, T>(name, reference, BeanPathBuilder.<R>create(), processor).via(Paths.modelToValue(reference));
	}
	
	/**
	 * Start a {@link PathPropBuilder} that can be used to build a {@link PathProp}
	 * by use of {@link #via(Transformer)} and {@link #to(Transformer)} methods.
	 * For example, to make a {@link PathProp} starting from bean b and progressing
	 * via properties x, y, z in order, use:
	 * <pre>
	 * PathProp<String> mirrorOfZ = from(nameOfMirrorOfZ, b).via(x.getName()).via(y.getName).to(z.getName);
	 * </pre>
	 * @param clazz
	 * 		The type of data in the final prop reached by the path
	 * @param name
	 * 		The string name of the {@link PathProp} to build
	 * @param pathRoot
	 * 		The root of the path
	 * @param processor
	 * 		The {@link ValueProcessor} for the resulting {@link PathProp}
	 * @param <R>
	 * 		The type of the root bean for the {@link BeanPath} 
	 * @param <T>
	 * 		The type of value in the {@link Prop} at the end of the path
	 * @return
	 * 		A builder to be used to make a {@link PathProp}
	 */
	public static <R extends Bean, T> PathPropBuilder<R, R, T> from(Class<T> clazz, String name, R pathRoot, ValueProcessor<T> processor) {
		return new PathPropBuilder<R, R, T>(name, clazz, pathRoot, BeanPathBuilder.<R>create(), processor);
	}
	
	/**
	 * Start a {@link PathPropBuilder} that can be used to build a {@link PathProp}
	 * by use of {@link #via(Transformer)} and {@link #to(Transformer)} methods.
	 * For example, to make a {@link PathProp} starting from bean b and progressing
	 * via properties x, y, z in order, use:
	 * <pre>
	 * PathProp<String> mirrorOfZ = from(nameOfMirrorOfZ, b).via(x.getName()).via(y.getName).to(z.getName);
	 * </pre>
	 * The name of the {@link Prop} will be "pathProp", and it will be read only
	 * @param <R>
	 * 		The type of the root bean for the {@link BeanPath} 
	 * @param <T>
	 * 		The type of value in the {@link Prop} at the end of the path
	 * @param clazz
	 * 		The type of data in the final prop reached by the path
	 * @param pathRoot
	 * 		The root of the path
	 * @return
	 * 		A builder to be used to make a {@link PathProp}
	 */
	public static <R extends Bean, T> PathPropBuilder<R, R, T> from(Class<T> clazz, R pathRoot) {
		return new PathPropBuilder<R, R, T>("pathProp", clazz, pathRoot, BeanPathBuilder.<R>create(), ReadOnlyProcessor.<T>get());
	}
	
	/**
	 * Produce a {@link PathProp} using the steps used to create this
	 * builder, then the last {@link Transformer} specified
	 * @param lastTransform
	 * 		The last {@link Transformer} in the path
	 * @return
	 * 		The path itself
	 */
	public PathProp<R, T> to(Transformer<? super D, ? extends Prop<T>> lastTransform) {
		return new PathProp<R, T>(name, pathRoot, builder.to(lastTransform), processor);
	}

	/**
	 * Produce a {@link PathProp} using the steps used to create this
	 * builder, then the last name specified
	 * @param lastName
	 * 		The last {@link PropName} in the path
	 * @return
	 * 		The path itself
	 */
	public PathProp<R, T> to(PropName<T> lastName) {
		return new PathProp<R, T>(name, pathRoot, builder.to(lastName), processor);
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
	public <E extends Bean> PathPropBuilder<R, E, T> via(Transformer<? super D, ? extends Prop<E>> nextTransform) {
		return new PathPropBuilder<R, E, T>(name, pathRoot, builder.via(nextTransform), processor);
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
	public <E extends Bean> PathPropBuilder<R, E, T> via(PropName<E> nextName) {
		return new PathPropBuilder<R, E, T>(name, pathRoot, builder.via(nextName), processor);
	}
	
}
