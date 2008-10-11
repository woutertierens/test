package demo.altpath;

import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.properties.EditableProp;

/**
 * Simple demo class
 */
public class B extends BeanDefault {

	private EditableProp<C> c = editable(C.class, "c", null);
	
	/**
	 * C property
	 * @return c
	 */
	public EditableProp<C> c() {return c;} 

}
