package demo.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jpropeller.reference.impl.EditableBeanReference;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.view.bean.impl.BeanPropListEditor;

import test.example.LotsOfProps;

/**
 * Demonstration of {@link BeanPropListEditor}
 */
public class BeanViewDemo {

	/**
	 * Demonstrate
	 * @param args
	 * 		ignored
	 */
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				//GeneralUtils.enableConsoleLogging(UpdateManagerDefault.class, SwingUpdateDispatcher.class);//, BeanPropListEditor.class, NumberSpinnerEditor.class);
				GeneralUtils.enableNimbus();
				
		        //Locale.setDefault(Locale.FRANCE);
		        
				final LotsOfProps a = new LotsOfProps();
				
				/*
				a.props().addListener(new PropListener() {
					@Override
					public <T> void propChanged(PropEvent<T> event) {
						System.out.println(event);
					}
				});
				*/

				//Make a ref to "a"
				final EditableBeanReference<LotsOfProps> model = EditableBeanReference.create(LotsOfProps.class, a);
				
				final BeanPropListEditor<LotsOfProps> editor = BeanPropListEditor.create(model);
				
				JFrame frame = new JFrame("Bean View Demo");
				frame.setLayout(new BorderLayout());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				frame.add(editor.getComponent(), BorderLayout.CENTER);

				JPanel panel = new JPanel(new FlowLayout());
				
				JButton newBean = new JButton("new bean");
				newBean.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						model.value().set(new LotsOfProps());
					}
				});
				panel.add(newBean);

				JButton print = new JButton("print original");
				print.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						System.out.println(a);
					}
				});
				panel.add(print);

				JButton randomChange = new JButton("randomChange original");
				randomChange.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						a.stringProp().set("Random String " + Math.random()*100);
						a.intProp().set((int)(Math.random()*100));
						a.longProp().set((long)(Math.random()*100));
						a.floatProp().set((float)Math.random()*100);
						a.doubleProp().set((double)Math.random()*100);
					}
				});
				panel.add(randomChange);

				JButton printCurrent = new JButton("print current");
				printCurrent.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						System.out.println(model.value().get());
					}
				});
				panel.add(printCurrent);

				JButton randomChangeCurrent = new JButton("randomChange current");
				randomChangeCurrent.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						LotsOfProps current = (LotsOfProps) model.value().get();
						current.stringProp().set("Random String " + Math.random()*100);
						current.intProp().set((int)(Math.random()*100));
						current.longProp().set((long)(Math.random()*100));
						current.floatProp().set((float)Math.random()*100);
						current.doubleProp().set((double)Math.random()*100);
					}
				});
				panel.add(randomChangeCurrent);
				
				JButton manyRandomChangesCurrent = new JButton("many random changes to current");
				manyRandomChangesCurrent.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						LotsOfProps current = (LotsOfProps) model.value().get();
						long startTime = System.currentTimeMillis();
						int repeats = 15360;
						for (int i = 0; i < repeats; i++) {
							current.stringProp().set("Random String " + Math.random()*100);
							current.intProp().set((int)(Math.random()*100));
							current.longProp().set((long)(Math.random()*100));
							current.floatProp().set((float)Math.random()*100);
							current.doubleProp().set((double)Math.random()*100);
						}
						long endTime = System.currentTimeMillis();
						double time = (endTime-startTime);
						System.out.println(repeats + " updates took " + time + "ms");
						System.out.println(time/((double)repeats) + " ms per update");
					}
				});
				panel.add(manyRandomChangesCurrent);

				
				frame.add(panel, BorderLayout.SOUTH);
				
				frame.pack();
				frame.setVisible(true);
			}
		});
		
	}
	
}
