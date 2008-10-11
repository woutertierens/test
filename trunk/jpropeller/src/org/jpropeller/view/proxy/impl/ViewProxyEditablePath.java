package org.jpropeller.view.proxy.impl;

import org.jpropeller.bean.Bean;
import org.jpropeller.map.ExtendedPropMap;
import org.jpropeller.map.PropMap;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.path.impl.EditablePathProp;
import org.jpropeller.properties.path.impl.EditablePathPropBuilder;
import org.jpropeller.system.Props;
import org.jpropeller.view.proxy.ViewProxy;

/**
 * A {@link ViewProxy} where the {@link #model()} property is
 * an {@link EditablePathProp}
 *
 * @param <M>
 * 		The type of data in the model
 */
public class ViewProxyEditablePath<M> implements ViewProxy<M> {

	ExtendedPropMap propMap = Props.getPropSystem().createExtendedPropMap(this);
	EditablePathProp<M> model;
	
	private ViewProxyEditablePath(EditablePathProp<M> model) {
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
	 * This is the first stage in creating a {@link ViewProxyEditablePath} - 
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
	 * 		A builder - call {@link ViewProxyEditablePathBuilder#via(PropName)}
	 * and {@link ViewProxyEditablePathBuilder#toProxy(PropName)} on this builder
	 * to build the entire path and then return the actual {@link ViewProxyEditablePath}
	 * 
	 */
	public static <P extends EditableProp<T>, T> EditablePathPropBuilder<P, T> from(Class<T> clazz, Bean pathRoot) {
		return new ViewProxyEditablePathBuilder<P, T>(clazz, pathRoot);
	}
	
	//FIXME would be neater to make a different visible builder interface
	//where "to" method could return the proxy - this would still want to
	//use an EditablePathPropBuilder, but not subclass it, so that the
	//"to" method name is still available 
	/**
	 * A class used to build a {@link ViewProxyEditablePath}
	 *
	 * @param <P>
	 * 		The type of property at the end of the path for the proxy
	 * @param <T>
	 * 		The type of value in the property at the end of the path for the proxy
	 */
	public static class ViewProxyEditablePathBuilder<P extends EditableProp<T>, T> extends EditablePathPropBuilder<P, T> {
		protected ViewProxyEditablePathBuilder(Class<T> clazz, Bean pathRoot) {
			super("model", clazz, pathRoot);
		}
		
		/**
		 * Complete the building process and produce a {@link ViewProxyEditablePath}
		 * instance 
		 * @param lastName
		 * 		The name of the last step in the path
		 * @return
		 * 		The {@link ViewProxyEditablePath}
		 */
		public ViewProxyEditablePath<T> toProxy(PropName<? extends P, T> lastName) {
			EditablePathProp<T> prop = super.to(lastName);
			return new ViewProxyEditablePath<T>(prop);
		}
	}
}
