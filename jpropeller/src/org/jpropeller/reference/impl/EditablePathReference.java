package org.jpropeller.reference.impl;

import org.jpropeller.bean.Bean;
import org.jpropeller.map.ExtendedPropMap;
import org.jpropeller.map.PropMap;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.path.impl.EditablePathProp;
import org.jpropeller.properties.path.impl.EditablePathPropBuilder;
import org.jpropeller.reference.Reference;
import org.jpropeller.system.Props;

/**
 * A {@link Reference} where the {@link #value()} property is
 * an {@link EditablePathProp}
 *
 * @param <M>
 * 		The type of value
 */
public class EditablePathReference<M> implements Reference<M> {

	ExtendedPropMap propMap = Props.getPropSystem().createExtendedPropMap(this);
	EditablePathProp<M> model;
	
	private EditablePathReference(EditablePathProp<M> model) {
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
	 * This is the first stage in creating a {@link EditablePathReference} - 
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
	 * 		A builder - call {@link EditablePathReferenceBuilder#via(PropName)}
	 * and {@link EditablePathReferenceBuilder#toReference(PropName)} on this builder
	 * to build the entire path and then return the actual {@link EditablePathReference}
	 * 
	 */
	public static <P extends EditableProp<T>, T> EditablePathPropBuilder<P, T> from(Class<T> clazz, Bean pathRoot) {
		return new EditablePathReferenceBuilder<P, T>(clazz, pathRoot);
	}
	
	//FIXME would be neater to make a different visible builder interface
	//where "to" method could return the reference - this would still want to
	//use an EditablePathPropBuilder, but not subclass it, so that the
	//"to" method name is still available 
	/**
	 * A class used to build a {@link EditablePathReference}
	 *
	 * @param <P>
	 * 		The type of property at the end of the path for the reference
	 * @param <T>
	 * 		The type of value in the property at the end of the path for the reference
	 */
	public static class EditablePathReferenceBuilder<P extends EditableProp<T>, T> extends EditablePathPropBuilder<P, T> {
		protected EditablePathReferenceBuilder(Class<T> clazz, Bean pathRoot) {
			super("model", clazz, pathRoot);
		}
		
		/**
		 * Complete the building process and produce a {@link EditablePathReference}
		 * instance 
		 * @param lastName
		 * 		The name of the last step in the path
		 * @return
		 * 		The {@link EditablePathReference}
		 */
		public EditablePathReference<T> toReference(PropName<? extends P, T> lastName) {
			EditablePathProp<T> prop = super.to(lastName);
			return new EditablePathReference<T>(prop);
		}
	}
}
