package org.jpropeller.view.bean.impl;

import org.jpropeller.bean.Bean;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.GeneralProp;
import org.jpropeller.reference.Reference;
import org.jpropeller.view.JView;

/**
 * A factory for {@link JView}s for viewing {@link GeneralProp} instances
 * @author bwebster
 *
 */
public interface PropViewFactory {

	/**
	 * Get a view suitable for a given {@link GeneralProp}
	 * Note that by its nature this cannot be typesafe - 
	 * unfortunately the {@link JView} returned is a raw type,
	 * and it is up to the {@link PropViewFactory} implementation
	 * to ensure that the {@link JView} will accept instances
	 * of type P.
	 * @param <P>
	 * 		The type of {@link GeneralProp}
	 * @param <M>
	 * 		The type of data in the {@link GeneralProp}
	 * @param model
	 * 		The reference containing model to display
	 * @param displayedName
	 * 		The name of the property of the model to display
	 * @return
	 * 		A view for the named property of the model in the reference
	 */
	@SuppressWarnings("unchecked")
	public <P extends GeneralProp<M>, M> JView viewFor(Reference<? extends Bean> model,
			PropName<P, M> displayedName);

}
