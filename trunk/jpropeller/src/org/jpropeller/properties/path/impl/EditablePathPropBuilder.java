package org.jpropeller.properties.path.impl;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.path.PathNameList;
import org.jpropeller.path.impl.BeanPathDefault;
import org.jpropeller.path.impl.PathNameListDefault;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.list.ListProp;

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
	 * by use of {@link #via(PropName)} and {@link #to(PropName)} methods.
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
	 * by use of {@link #via(PropName)} and {@link #to(PropName)} methods.
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
	 * @param lastName
	 * 		The last {@link PropName} in the path
	 * @return
	 * 		The path itself
	 */
	public EditablePathProp<T> to(PropName<? extends P, T> lastName) {
		BeanPathDefault<P, T> path = new BeanPathDefault<P, T>(list, lastName);
		return new EditablePathProp<T>(name, pathRoot, path);
	}
	
	/**
	 * Add another name to this path, and return this
	 * instance (to allow chaining of calls to {@link #via(PropName)})
	 * @param nextName
	 * 		The next{@link PropName} in the path
	 * @return
	 * 		The path itself
	 */
	public EditablePathPropBuilder<P, T> via(PropName<? extends Prop<? extends Bean>, ? extends Bean> nextName) {
		list.add(nextName);
		return this;
	}
}
