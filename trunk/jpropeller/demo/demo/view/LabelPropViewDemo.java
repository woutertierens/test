package demo.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jpropeller.reference.impl.ReferenceToChangeable;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.util.PrintingChangeListener;
import org.jpropeller.view.impl.LabelPropView;
import org.jpropeller.view.primitive.impl.NumberProgressBarEditor;
import org.jpropeller.view.update.impl.UpdateManagerDefault;

import demo.bean.TestStringBean;

/**
 * Demonstration of {@link LabelPropView}
 */
public class LabelPropViewDemo {

	/**
	 * Demonstrate
	 * @param args
	 * 		ignored
	 */
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				GeneralUtils.enableConsoleLogging(UpdateManagerDefault.class);
				GeneralUtils.enableNimbus();
				
				TestStringBean s = new TestStringBean();
				
				s.features().addListener(new PrintingChangeListener());
				
				ReferenceToChangeable<TestStringBean> ref = ReferenceToChangeable.create(TestStringBean.class, s);
				
				
				final LabelPropView<String> nameView = 
					LabelPropView.create(
							ref, 
							s.name().getName());

				final LabelPropView<String> streetView = 
					LabelPropView.create(
							ref, 
							s.street().getName());
				
				final NumberProgressBarEditor<Double> progressView = 
					NumberProgressBarEditor.createDouble(ref, s.progress().getName());

				final NumberProgressBarEditor<Integer> percentView = 
					NumberProgressBarEditor.createInteger(ref, s.percent().getName());

				JFrame frame = new JFrame("Label View Demo");
				frame.setLayout(new BorderLayout());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				JPanel labelPanel = new JPanel(new FlowLayout());
				labelPanel.add(nameView.getComponent());
				labelPanel.add(streetView.getComponent());
				labelPanel.add(progressView.getComponent());
				labelPanel.add(percentView.getComponent());
				frame.add(labelPanel, BorderLayout.CENTER);

				JPanel panel = new JPanel(new FlowLayout());
				
				JButton randomChangeCurrent = new JButton("randomChange current");
				randomChangeCurrent.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						TestStringBean current = (TestStringBean)nameView.getModel().value().get();
						current.name().set("Random Name " + Math.random()*100);
						current.street().set("Random Street " + Math.random()*100);
						current.progress().set(Math.random());
						current.percent().set((int)(Math.random() * 100));
					}
				});
				panel.add(randomChangeCurrent);
				
				JButton manyRandomChangesCurrent = new JButton("many random changes to current");
				manyRandomChangesCurrent.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						TestStringBean current = (TestStringBean)nameView.getModel().value().get();
						long startTime = System.currentTimeMillis();
						int repeats = 100;
						for (int i = 0; i < repeats; i++) {
							current.name().set("Random Name " + Math.random()*100);
							current.street().set("Random Street " + Math.random()*100);
							current.progress().set(Math.random());
							current.percent().set((int)(Math.random() * 100));

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
