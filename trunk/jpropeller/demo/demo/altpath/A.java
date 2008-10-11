package demo.altpath;

import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.properties.EditableProp;

/**
 * Simple demo class
 */
public class A extends BeanDefault {

	private EditableProp<B> b = editable(B.class, "b", null);
	
	/**
	 * B property
	 * @return b
	 */
	public EditableProp<B> b() {return b;} 
}
