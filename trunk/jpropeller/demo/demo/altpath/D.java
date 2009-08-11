package demo.altpath;

import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.properties.Prop;

/**
 * Simple demo bean
 */
public class D extends BeanDefault {

	private Prop<Integer> i = editable("i", 0);

	/**
	 * I property
	 * @return i
	 */
	public Prop<Integer> i() {return i;} 

}
