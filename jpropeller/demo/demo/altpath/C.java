package demo.altpath;

import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.properties.EditableProp;

/**
 * Simple demo class
 */
public class C extends BeanDefault {

	private EditableProp<D> d = editable(D.class, "d", null);

	/**
	 * D property
	 * @return d
	 */
	public EditableProp<D> d() {return d;} 

}
