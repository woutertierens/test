package demo.properties.calculated;

import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.EditableProp;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.calculated.background.impl.BackgroundCalculatedProp;
import org.jpropeller.properties.calculated.impl.PropAddition;
import org.jpropeller.util.PrintingChangeListener;

/**
 * Demonstrate the use of a calculated property
 * @author shingoki
 *
 */
public class BackgroundAdditionBean extends BeanDefault {
	
	//Set up Props
	EditableProp<Double> a = editable("a", 42d);
	EditableProp<Double> b = editable("b", 0.4);
	
	//The generic array is unimportant - we know CalculatedProp never relies on its type, but unfortunately we can't suppress the warning in CalculatedProp itself
	@SuppressWarnings("unchecked")
	Prop<Double> c = addProp(new BackgroundCalculatedProp<Double>(
			PropName.create("c", Double.class), 
			new PropAddition(a, b),
			-1d	//initial value
			));

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
		String s = "BackgroundAdditionBean, a = " + a.get() + ", b = " + b.get() + ", c = " + c.get();
		return s;
	}
	
	/**
	 * Demonstrate use of the bean
	 * @param args
	 */
	public final static void main(String[] args) {
		
		try {
			BackgroundAdditionBean x = new BackgroundAdditionBean();
			System.out.println(x);
			
			x.features().addListener(new PrintingChangeListener());
			
			//System.out.println("Setting x.a to 24");
			x.a().set(24d);
			//System.out.println(x);
			
			Thread.sleep(2000);
			x.a().set(100d);
			
			for (int i = 0; i < 10000; i++) {
				x.a().set((double)i);
			}
			
			Thread.sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}
	
//	/**
//	 * Demonstrate
//	 * @param args
//	 * 		ignored
//	 */
//	public static void main(String[] args) {
//		
//		SwingUtilities.invokeLater(new Runnable() {
//			@Override
//			public void run() {
//
//				//GeneralUtils.enableConsoleLogging(UpdateManagerDefault.class, SwingUpdateDispatcher.class);//, BeanPropListEditor.class, NumberSpinnerEditor.class);
//				GeneralUtils.enableNimbus();
//				
//				BackgroundAdditionBean x = new BackgroundAdditionBean();
//
//				final EditableBeanReference<BackgroundAdditionBean> model = EditableBeanReference.create(BackgroundAdditionBean.class, x);
//				
//				final BeanPropListEditor<BackgroundAdditionBean> editor = BeanPropListEditor.create(model);
//				
//				JFrame frame = new JFrame("Background Addition View Demo");
//				frame.setLayout(new BorderLayout());
//				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//				frame.add(editor.getComponent(), BorderLayout.CENTER);
//				
//				frame.pack();
//				frame.setVisible(true);
//			}
//		});
//		
//	}
	
	
}
