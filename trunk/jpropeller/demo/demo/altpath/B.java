package demo.altpath;

import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;

/**
 * Simple demo class
 */
public class B extends BeanDefault {

	/**
	 * The {@link PropName} of the {@link C} property
	 */
	public final static PropName<C> C_NAME = PropName.create(C.class, "c"); 
	
	private Prop<C> c = editable(C.class, "c", null);
	
	/**
	 * C property
	 * @return c
	 */
	public Prop<C> c() {return c;} 

}
