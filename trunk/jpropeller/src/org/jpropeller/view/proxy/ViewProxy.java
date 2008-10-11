package org.jpropeller.view.proxy;

import org.jpropeller.bean.Bean;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.path.impl.PathProp;
import org.jpropeller.view.View;

/**
 * A {@link ViewProxy} is a {@link Bean} in the model layer, but
 * separate from the actual data model, which allows {@link View}s
 * to track the object they are displaying, and provides for more
 * flexible redirection of views, for example to display objects
 * referenced via paths (e.g. using {@link PathProp}s)
 *
 * @param <M>
 * 		The type of model
 */
public interface ViewProxy<M> extends Bean {
	
	/**
	 * A {@link Prop} whose value is the current
	 * model value of the {@link View} 
	 * @return
	 * 		The model
	 */
	public Prop<M> model();
	
}
