package org.jpropeller.reference;

import org.jpropeller.bean.Bean;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.path.impl.PathProp;
import org.jpropeller.view.View;

/**
 * A {@link Reference} is a {@link Bean} with a single property giving
 * a value - it is the simplest possible Bean.
 * <br />
 * This acts as a reference in the sense that it refers to a value,
 * where the value referred to may change (by having a new instance set
 * or by a deep change). This is used for example as a model for 
 * a {@link View}, which allows {@link View}s
 * to track the object they are displaying, and provides for more
 * flexible redirection of views, for example to display objects
 * referenced via paths (e.g. using a {@link Reference} having a 
 * {@link #value()} that is a {@link PathProp})
 *
 * @param <M>
 * 		The type of value
 */
public interface Reference<M> extends Bean {
	
	/**
	 * A {@link Prop} giving the referenced value 
	 * @return
	 * 		The value
	 */
	public Prop<M> value();
	
}
