package org.jpropeller.view.proxy.impl;

import org.jpropeller.bean.Bean;
import org.jpropeller.map.ExtendedPropMap;
import org.jpropeller.map.PropMap;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.path.impl.PathProp;
import org.jpropeller.properties.path.impl.PathPropBuilder;
import org.jpropeller.system.Props;
import org.jpropeller.view.proxy.ViewProxy;

/**
 * A {@link ViewProxy} where the {@link #model()} property is
 * a {@link PathProp}
 *
 * @param <M>
 * 		The type of data in the model
 */
public class ViewProxyPath<M> implements ViewProxy<M> {

	ExtendedPropMap propMap = Props.getPropSystem().createExtendedPropMap(this);
	PathProp<M> model;
	
	private ViewProxyPath(PathProp<M> model) {
		this.model = propMap.add(model);
	}

	@Override
	public PropMap props() {
		return propMap;
	}

	@Override
	public Prop<M> model() {
		return model;
	}
	
	/**
	 * This is the first stage in creating a {@link ViewProxyPath} - 
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
	 * 		A builder - call {@link ViewProxyUneditablePathBuilder#via(PropName)}
	 * and {@link ViewProxyUneditablePathBuilder#toProxy(PropName)} on this builder
	 * to build the entire path and then return the actual {@link ViewProxyPath}
	 * 
	 */
	public static <P extends Prop<T>, T> PathPropBuilder<P, T> from(Class<T> clazz, Bean pathRoot) {
		return new ViewProxyUneditablePathBuilder<P, T>(clazz, pathRoot);
	}
	
	//FIXME would be neater to make a different visible builder interface
	//where "to" method could return the proxy - this would still want to
	//use an UneditablePathPropBuilder, but not subclass it, so that the
	//"to" method name is still available 
	/**
	 * A class used to build a {@link ViewProxyPath}
	 *
	 * @param <P>
	 * 		The type of property at the end of the path for the proxy
	 * @param <T>
	 * 		The type of value in the property at the end of the path for the proxy
	 */
	public static class ViewProxyUneditablePathBuilder<P extends Prop<T>, T> extends PathPropBuilder<P, T> {
		protected ViewProxyUneditablePathBuilder(Class<T> clazz, Bean pathRoot) {
			super("model", clazz, pathRoot);
		}
		
		/**
		 * Complete the building process and produce a {@link ViewProxyPath}
		 * instance 
		 * @param lastName
		 * 		The name of the last step in the path
		 * @return
		 * 		The {@link ViewProxyPath}
		 */
		public ViewProxyPath<T> toProxy(PropName<? extends P, T> lastName) {
			PathProp<T> prop = super.to(lastName);
			return new ViewProxyPath<T>(prop);
		}
	}
}
