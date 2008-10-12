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
 * This is a temporary builder object that is used to make an {@link EditablePathProp}
 * 
 * For example, to make an {@link EditablePathProp} starting from bean b and progressing
 * via properties x, y, z in order, use:
 * <pre>
 * EditablePathProp<String> mirrorOfZ = from(nameOfMirrorOfZ, b).via(x.getName()).via(y.getName).to(z.getName);
 * </pre>
 * 
 * @author bwebster
 *
 * @param <P> 
 * 		The type of final prop reached by the path (e.g. {@link Prop},
 * {@link EditableProp}, {@link ListProp} etc.)
 * @param <T>
 * 		The type of data in the final prop reached by the path
 */
public class EditablePathPropBuilder<P extends EditableProp<T>, T> {

	PropName<EditableProp<T>, T> name;
	Bean pathRoot;
	PathNameList list;

	
	protected EditablePathPropBuilder(PropName<EditableProp<T>, T> name, Bean pathRoot) {
		super();
		this.name = name;
		this.pathRoot = pathRoot;
		list = new PathNameListDefault();
	}

	protected EditablePathPropBuilder(String name, Class<T> clazz, Bean pathRoot) {
		this(PropName.editable(name, clazz), pathRoot);
	}

	/**
	 * Start a {@link EditablePathPropBuilder} that can be used to build an {@link EditablePathProp}
	 * by use of {@link #via(Transformer)} and {@link #to(Transformer)} methods.
	 * For example, to make an {@link EditablePathProp} starting from bean b and progressing
	 * via properties x, y, z in order, use:
	 * <pre>
	 * EditablePathProp<String> mirrorOfZ = from(nameOfMirrorOfZ, b).via(x.getName()).via(y.getName).to(z.getName);
	 * </pre>
	 * @param <P> 
	 * 		The type of final prop reached by the path (e.g. {@link Prop},
	 * {@link EditableProp}, {@link ListProp} etc.)
	 * @param <T>
	 * 		The type of data in the final prop reached by the path
	 * @param name
	 * 		The name of the {@link EditablePathProp} to build
	 * @param pathRoot
	 * 		The root of the path
	 * @return
	 * 		A builder to be used to make an {@link EditablePathProp}
	 */
	public static <P extends EditableProp<T>, T> EditablePathPropBuilder<P, T> from(PropName<EditableProp<T>, T> name, Bean pathRoot) {
		return new EditablePathPropBuilder<P, T>(name, pathRoot);
	}
	
	/**
	 * Start a {@link EditablePathPropBuilder} that can be used to build an {@link EditablePathProp}
	 * by use of {@link #via(Transformer)} and {@link #to(Transformer)} methods.
	 * For example, to make an {@link EditablePathProp} starting from bean b and progressing
	 * via properties x, y, z in order, use:
	 * <pre>
	 * EditablePathProp<String> mirrorOfZ = from(nameOfMirrorOfZ, b).via(x.getName()).via(y.getName).to(z.getName);
	 * </pre>
	 * @param <P> 
	 * 		The type of final prop reached by the path (e.g. {@link Prop},
	 * {@link EditableProp}, {@link ListProp} etc.)
	 * @param <T>
	 * @param name
	 * 		The string name of the {@link EditablePathProp} to build
	 * @param clazz
	 * 		The type of data in the final prop reached by the path
	 * @param pathRoot
	 * 		The root of the path
	 * @return
	 * 		A builder to be used to make an {@link EditablePathProp}
	 */
	public static <P extends EditableProp<T>, T> EditablePathPropBuilder<P, T> from(String name, Class<T> clazz, Bean pathRoot) {
		return new EditablePathPropBuilder<P, T>(name, clazz, pathRoot);
	}
	
	/**
	 * Produce a {@link BeanPathDefault} using the {@link PropName}s used to create this
	 * object, then the last name specified
	 * @param lastTransform
	 * 		The last {@link Transformer} in the path
	 * @return
	 * 		The path itself
	 */
	public EditablePathProp<T> to(Transformer<? super Bean, ? extends P> lastTransform) {
		BeanPathDefault<P, T> path = new BeanPathDefault<P, T>(list, lastTransform);
		return new EditablePathProp<T>(name, pathRoot, path);
	}
	
	/**
	 * Add another name to this path, and return this
	 * instance (to allow chaining of calls to {@link #via(Transformer)})
	 * @param nextTransformer
	 * 		The next{@link Transformer} in the path
	 * @return
	 * 		The path itself
	 */
	public EditablePathPropBuilder<P, T> via(Transformer<? super Bean, Prop<? extends Bean>> nextTransformer) {
		list.add(nextTransformer);
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
	public EditablePathProp<T> to(PropName<P, T> lastName) {
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
	public EditablePathPropBuilder<P, T> via(PropName<? extends Prop<? extends Bean>, ? extends Bean> nextName) {
		return via(new BeanToBeanPropTransformer(nextName));
	}
}
