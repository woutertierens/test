package demo.simple;

import org.jpropeller.bean.Bean;
import org.jpropeller.bean.BeanFeatures;
import org.jpropeller.bean.MutableBeanFeatures;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.changeable.impl.ChangeablePropDefault;
import org.jpropeller.properties.immutable.impl.PropImmutable;
import org.jpropeller.system.Props;
import org.jpropeller.util.PrintingChangeListener;

/**
 * A {@link Bean} that has a {@link Double} value, but also a {@link SimpleBean} value
 */
public class SimpleParentBean implements Bean {

	private MutableBeanFeatures services = Props.getPropSystem().createBeanFeatures(this);
	@Override
	public BeanFeatures features() {
		return services;
	}

	private Prop<Double> d = services.add(PropImmutable.editable("d", 1.1d));
	private Prop<SimpleBean> s = services.add(ChangeablePropDefault.editable(SimpleBean.class, "s", null));
	
	/**
	 * Property
	 * @return d
	 */
	public Prop<Double> d(){return d;}

	/**
	 * Property
	 * @return s
	 */
	public Prop<SimpleBean> s(){return s;}

	@Override
	public String toString() {
		return "SimpleParentBean with value " + d().get() + " and SimpleBean " + s().get();
	}
	
	/**
	 * Demonstrate the bean
	 * @param args
	 * 		ignored
	 */
	public static void main(String[] args) {
		SimpleParentBean b = new SimpleParentBean();
	
		b.features().addListener(new PrintingChangeListener());
		
		System.out.println("Initial, " + b.d().get());
		b.d().set(42d);
		System.out.println("After set to 42, " + b.d().get());
		
		System.out.println("s is " + b.s());
		
		SimpleBean sb = new SimpleBean();
		b.s().set(sb);
		
		sb.d().set(84d);
	}
	
}
