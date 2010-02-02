package org.jpropeller.view.bean.impl;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.reference.Reference;
import org.jpropeller.view.JView;

/**
 * A factory for {@link JView}s for viewing {@link Prop} instances
 */
public interface PropViewFactory {

	/**
	 * Get a view suitable for a given {@link Prop}
	 * Note that by its nature this cannot be typesafe - 
	 * unfortunately the {@link JView} returned is a raw type,
	 * and it is up to the {@link PropViewFactory} implementation
	 * to ensure that the {@link JView} will accept instances
	 * of type Prop<M>.
	 * @param <M>
	 * 		The type of data in the {@link Prop}
	 * @param model
	 * 		The reference containing model to display
	 * @param displayedName
	 * 		The name of the property of the model to display
	 * @return
	 * 		A view for the named property of the model in the reference
	 */
	public <M> JView viewFor(Reference<? extends Bean> model, PropName<M> displayedName);
	
	/**
	 * Get a view suitable for a given {@link Prop}
	 * Note that by its nature this cannot be typesafe - 
	 * unfortunately the {@link JView} returned is a raw type,
	 * and it is up to the {@link PropViewFactory} implementation
	 * to ensure that the {@link JView} will accept instances
	 * of type Prop<M>.
	 * @param <M>
	 * 		The type of data in the {@link Prop}
	 * @param model
	 * 		The reference containing model to display
	 * @param displayedName
	 * 		The name of the property of the model to display
	 * @param locked	If this is non-null, the view will not support
	 * 					editing while its value is true.
	 * @return
	 * 		A view for the named property of the model in the reference
	 */
	public <M> JView viewFor(Reference<? extends Bean> model, PropName<M> displayedName, Prop<Boolean> locked);
	
	/**
	 * @param displayedName
	 * @return true if the factory can provide a view for the given name.
	 */
	public boolean providesFor(PropName<?> displayedName);
	
	
}
