package demo.altpath;

import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.properties.EditableProp;

/**
 * Simple demo bean
 */
public class D extends BeanDefault {

	private EditableProp<Integer> i = editable("i", 0);

	/**
	 * I property
	 * @return i
	 */
	public EditableProp<Integer> i() {return i;} 

}
