package demo.view;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jpropeller.reference.impl.EditableBeanReference;
import org.jpropeller.view.primitive.impl.NumberSpinnerEditor;
import org.jpropeller.view.primitive.impl.StringTextFieldEditor;

import test.example.contacts.Address;

/**
 * Demonstration of simple primitive views
 * @author bwebster
 */
public class PrimitiveViewDemo {

	/**
	 * Demonstrate
	 * @param args
	 * 		ignored
	 */
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				
				final Address a = new Address();
				a.houseNumber().set(42);

				final Address b = new Address();
				
				/*
				a.props().addListener(new PropListener() {
					@Override
					public <T> void propChanged(PropEvent<T> event) {
						System.out.println(event);
					}
				});
				*/
				
				//We make the model with "b"... 
				EditableBeanReference<Address> aModel = EditableBeanReference.create(Address.class, b);
				
				//(Note also that we can share the model between two views)
				NumberSpinnerEditor<Integer> hnView = NumberSpinnerEditor.create(aModel, b.houseNumber().getName(), 1, 1000, 1);
				StringTextFieldEditor streetView = StringTextFieldEditor.create(aModel, b.street().getName());
				NumberSpinnerEditor<Integer> hnView2 = NumberSpinnerEditor.create(aModel, b.houseNumber().getName(), 1, 1000, 1);
				NumberSpinnerEditor<Integer> hnView3 = NumberSpinnerEditor.create(aModel, b.houseNumber().getName(), 1, 1000, 1);
				NumberSpinnerEditor<Integer> hnView4 = NumberSpinnerEditor.create(aModel, b.houseNumber().getName(), 1, 1000, 1);
				NumberSpinnerEditor<Integer> hnView5 = NumberSpinnerEditor.create(aModel, b.houseNumber().getName(), 1, 1000, 1);
				NumberSpinnerEditor<Integer> hnView6 = NumberSpinnerEditor.create(aModel, b.houseNumber().getName(), 1, 1000, 1);

				//But now we set the model to have model "a" - note that the views display and edit "a"
				aModel.value().set(a);
				
				JFrame frame = new JFrame("Primitive View Demo");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setLayout(new FlowLayout());

				frame.add(hnView.getComponent());
				frame.add(streetView.getComponent());

				frame.add(hnView2.getComponent());
				frame.add(hnView3.getComponent());
				frame.add(hnView4.getComponent());
				frame.add(hnView5.getComponent());
				frame.add(hnView6.getComponent());

				JButton print = new JButton("print");
				print.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						System.out.println(a);
					}
				});
				frame.add(print);

				JButton randomChange = new JButton("randomChange");
				randomChange.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						int repeats = 15360;
						long startTime = System.currentTimeMillis();
						for (int i = 0; i < repeats; i++) {
							a.houseNumber().set((int)(Math.random() * 100 + 1.5));
							a.street().set("Random Street Name " + Math.random()*100);
						}
						long endTime = System.currentTimeMillis();
						long time = endTime - startTime;
						System.out.println(repeats + " changes in " + time + "ms");
						
					}
				});
				frame.add(randomChange);

				frame.pack();
				frame.setVisible(true);
			}
		});
		
	}
	
}
