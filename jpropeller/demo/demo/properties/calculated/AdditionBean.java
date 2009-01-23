package demo.properties.calculated;

import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.calculated.impl.CalculatedProp;
import org.jpropeller.properties.calculated.impl.PropAddition;
import org.jpropeller.util.PrintingChangeListener;

/**
 * Demonstrate the use of a calculated property
 * @author shingoki
 *
 */
public class AdditionBean extends BeanDefault {
	
	//Set up Props
	EditableProp<Double> a = editable("a", 42d);
	EditableProp<Double> b = editable("b", 0.4);
	
	//The generic array is unimportant - we know CalculatedProp never relies on its type, but unfortunately we can't suppress the warning in CalculatedProp itself
	@SuppressWarnings("unchecked")
	Prop<Double> c = addProp(new CalculatedProp<Double>(
			PropName.create("c", Double.class), new PropAddition(a, b)));

	/**
	 * Access property a
	 * @return
	 * 		property a
	 */
	public EditableProp<Double> a() {return a;};
	/**
	 * Access property b
	 * @return
	 * 		property b
	 */
	public EditableProp<Double> b() {return b;};
	/**
	 * Access property c
	 * @return
	 * 		property c
	 */
	public Prop<Double> c() {return c;};
	
	@Override
	public String toString() {
		String s = "AdditionBean, a = " + a.get() + ", b = " + b.get() + ", c = " + c.get();
		return s;
	}
	
	/**
	 * Demonstrate use of the bean
	 * @param args
	 */
	public final static void main(String[] args) {
		AdditionBean x = new AdditionBean();
		System.out.println(x);
		
		x.features().addListener(new PrintingChangeListener());
		
		System.out.println("Setting x.a to 24");
		x.a().set(24d);
		System.out.println(x);
	}
	
}
