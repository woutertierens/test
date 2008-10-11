package demo.properties.calculated;

import org.jpropeller.bean.Bean;
import org.jpropeller.map.ExtendedPropMap;
import org.jpropeller.map.PropMap;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.calculated.impl.CalculatedProp;
import org.jpropeller.properties.calculated.impl.PropAddition;
import org.jpropeller.properties.event.PropEvent;
import org.jpropeller.properties.event.PropListener;
import org.jpropeller.system.Props;

/**
 * Demonstrate the use of a calculated property
 * @author shingoki
 *
 */
public class AdditionBean implements Bean {
	
	//Standard code block for a bean
	ExtendedPropMap propMap = Props.getPropSystem().createExtendedPropMap(this);
	
	@Override
	public PropMap props() {
		return propMap;
	}

	//Set up Props
	EditableProp<Double> a = propMap.editable("a", 42d);
	EditableProp<Double> b = propMap.editable("b", 0.4);
	@SuppressWarnings("unchecked")
	Prop<Double> c = propMap.add(new CalculatedProp<Double>(
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
		String s = "a = " + a.get() + ", b = " + b.get() + ", c = " + c.get();
		return s;
	}
	
	/**
	 * Demonstrate use of the bean
	 * @param args
	 */
	public final static void main(String[] args) {
		AdditionBean x = new AdditionBean();
		System.out.println(x);
		
		x.props().addListener(new PropListener() {
			@Override
			public <T> void propChanged(PropEvent<T> event) {
				System.out.println(event);
			}
		});
		
		System.out.println("Setting x.a to 24");
		x.a().set(24d);
		System.out.println(x);
	}
	
}
