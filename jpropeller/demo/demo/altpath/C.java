package demo.altpath;

import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;

/**
 * Simple demo class
 */
public class C extends BeanDefault {

	/**
	 * The {@link PropName} of the {@link D} property
	 */
	public final static PropName<D> D_NAME = PropName.create("d", D.class); 

	private Prop<D> d = editable(D.class, "d", null);

	/**
	 * D property
	 * @return d
	 */
	public Prop<D> d() {return d;} 

}
