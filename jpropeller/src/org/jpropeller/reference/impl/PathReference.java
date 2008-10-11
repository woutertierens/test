package org.jpropeller.reference.impl;

import org.jpropeller.bean.Bean;
import org.jpropeller.map.ExtendedPropMap;
import org.jpropeller.map.PropMap;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.path.impl.PathProp;
import org.jpropeller.properties.path.impl.PathPropBuilder;
import org.jpropeller.reference.Reference;
import org.jpropeller.system.Props;

/**
 * A {@link Reference} where the {@link #value()} property is
 * a {@link PathProp}
 *
 * @param <M>
 * 		The type of value
 */
public class PathReference<M> implements Reference<M> {

	ExtendedPropMap propMap = Props.getPropSystem().createExtendedPropMap(this);
	PathProp<M> model;
	
	private PathReference(PathProp<M> model) {
		this.model = propMap.add(model);
	}

	@Override
	public PropMap props() {
		return propMap;
	}

	@Override
	public Prop<M> value() {
		return model;
	}
	
	/**
	 * This is the first stage in creating a {@link PathReference} - 
	 * call this method specifying the class of the end of the path,
	 * and the {@link Bean} from which the path starts
	 * @param <P> 
	 * 		The type of prop at the end of the path
	 * @param <T> 
	 * 		The type of value in the prop at the end of the path
	 * @param clazz
	 * 		The class of the property at the end of the
	 * path (the one that is mirrored by the path property)
	 * @param pathRoot
	 * 		The start of the path 
	 * @return 
	 * 		A builder - call {@link PathReferenceBuilder#via(PropName)}
	 * and {@link PathReferenceBuilder#toReference(PropName)} on this builder
	 * to build the entire path and then return the actual {@link PathReference}
	 * 
	 */
	public static <P extends Prop<T>, T> PathPropBuilder<P, T> from(Class<T> clazz, Bean pathRoot) {
		return new PathReferenceBuilder<P, T>(clazz, pathRoot);
	}
	
	//FIXME would be neater to make a different visible builder interface
	//where "to" method could return the reference - this would still want to
	//use a PathPropBuilder, but not subclass it, so that the
	//"to" method name is still available 
	/**
	 * A class used to build a {@link PathReference}
	 *
	 * @param <P>
	 * 		The type of property at the end of the path for the reference
	 * @param <T>
	 * 		The type of value in the property at the end of the path for the reference
	 */
	public static class PathReferenceBuilder<P extends Prop<T>, T> extends PathPropBuilder<P, T> {
		protected PathReferenceBuilder(Class<T> clazz, Bean pathRoot) {
			super("model", clazz, pathRoot);
		}
		
		/**
		 * Complete the building process and produce a {@link PathReference}
		 * instance 
		 * @param lastName
		 * 		The name of the last step in the path
		 * @return
		 * 		The {@link PathReference}
		 */
		public PathReference<T> toReference(PropName<? extends P, T> lastName) {
			PathProp<T> prop = super.to(lastName);
			return new PathReference<T>(prop);
		}
	}
}
