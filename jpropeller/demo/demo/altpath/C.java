package demo.altpath;

import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.EditableProp;

/**
 * Simple demo class
 */
public class C extends BeanDefault {

	/**
	 * The {@link PropName} of the {@link D} property
	 */
	public final static PropName<EditableProp<D>, D> D_NAME = PropName.editable("d", D.class); 

	private EditableProp<D> d = editable(D.class, "d", null);

	/**
	 * D property
	 * @return d
	 */
	public EditableProp<D> d() {return d;} 

}
