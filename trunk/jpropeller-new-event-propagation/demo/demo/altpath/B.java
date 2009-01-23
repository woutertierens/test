package demo.altpath;

import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.EditableProp;

/**
 * Simple demo class
 */
public class B extends BeanDefault {

	/**
	 * The {@link PropName} of the {@link C} property
	 */
	public final static PropName<EditableProp<C>, C> C_NAME = PropName.editable("c", C.class); 
	
	private EditableProp<C> c = editable(C.class, "c", null);
	
	/**
	 * C property
	 * @return c
	 */
	public EditableProp<C> c() {return c;} 

}
