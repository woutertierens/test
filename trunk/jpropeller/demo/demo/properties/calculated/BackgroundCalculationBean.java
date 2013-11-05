package demo.properties.calculated;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jpropeller.bean.impl.BeanDefault;
import org.jpropeller.calculation.Calculation;
import org.jpropeller.name.PropName;
import org.jpropeller.properties.Prop;
import org.jpropeller.properties.calculated.background.impl.BackgroundCalculatedProp;
import org.jpropeller.properties.calculated.impl.CalculationDefault;
import org.jpropeller.properties.calculated.impl.ListCalculation;
import org.jpropeller.reference.impl.ReferenceToChangeable;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.view.bean.impl.BeanEditor;

/**
 * Demonstrate the use of a slow background calculated property
 */
public class BackgroundCalculationBean extends BeanDefault {
	
	//Set up Props
	private final Prop<Long> i = editable("i", 10L);
	
	//Calculation using calc method
	private final ListCalculation<Prop<? extends Long>, Long> slowCalculation = new ListCalculation<Prop<? extends Long>, Long>() {
		@Override
		public Long calculate(List<? extends Prop<? extends Long>> inputs) {
			return calc(inputs.get(0).get());
		}
	};

	//Default calc using slowCalculation
	@SuppressWarnings("unchecked")	//Don't care about generic arrays for varargs parameter - would LOVE a definition-site suppress!
	private final Calculation<Long> calculation = new CalculationDefault<Prop<? extends Long>, Long>(slowCalculation, i);
	
	//Property using calculation
	Prop<Long> calculated = addProp(new BackgroundCalculatedProp<Long>(PropName.create(Long.class, "calculated"), calculation, 1L));

	/**
	 * Simulate a calculation - just returns i, but delays by
	 * i * 10 milliseconds. 
	 * @param i input
	 * @return output
	 */
	public final static long calc(long i) {
		long startTime = System.currentTimeMillis();
		try {
			Thread.sleep(i * 10);
		} catch (InterruptedException e) {
		}
		System.out.println("Took " + (System.currentTimeMillis()-startTime));
		return i;		
	}

	/**
	 * Access property i
	 * @return
	 * 		property i
	 */
	public Prop<Long> i() {return i;};
	
	/**
	 * Access calculated property - just mirrors i, but with simulated delay fro calculation
	 * @return
	 * 		calculated property
	 */
	public Prop<Long> calculated() {return calculated;};
	
	@Override
	public String toString() {
		String s = "BackgroundFactorialBean, i = " + i.get() + ", calculated i = " + calculated().get();
		return s;
	}
	
	/**
	 * Demonstrate
	 * @param args ignored
	 */
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				//GeneralUtils.enableConsoleLogging(UpdateManagerDefault.class, SwingUpdateDispatcher.class);//, BeanPropListEditor.class, NumberSpinnerEditor.class);
				GeneralUtils.enableNimbus();
				
				BackgroundCalculationBean x = new BackgroundCalculationBean();

				final ReferenceToChangeable<BackgroundCalculationBean> model = ReferenceToChangeable.create(BackgroundCalculationBean.class, x);
				
				final BeanEditor<BackgroundCalculationBean> editor = BeanEditor.create(model);
				
				JFrame frame = new JFrame("Background Addition View Demo");
				frame.setLayout(new BorderLayout());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				frame.add(editor.getComponent(), BorderLayout.CENTER);
				
				frame.pack();
				frame.setVisible(true);
			}
		});
		
	}
	
	
}
