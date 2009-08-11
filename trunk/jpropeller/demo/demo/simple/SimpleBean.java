package demo.simple;

import org.jpropeller.bean.Bean;
import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.properties.Prop;
import org.jpropeller.util.PrintingChangeListener;

/**
 * About the simplest possible {@link Bean}
 */
public class SimpleBean extends BeanDefault {

	private Prop<Double> d = editable("d", 1.1d);
	
	/**
	 * A double value
	 * @return d
	 */
	public Prop<Double> d(){return d;}

	@Override
	public String toString() {
		return "SimpleBean with value " + d().get();
	}
	
	/**
	 * Demonstrate the bean
	 * @param args
	 * 		ignored
	 */
	public static void main(String[] args) {
		SimpleBean b = new SimpleBean();
	
		b.features().addListener(new PrintingChangeListener());
		
		System.out.println("Initial, " + b.d().get());
		b.d().set(42d);
		System.out.println("After set to 42, " + b.d().get());
	}
	
}
