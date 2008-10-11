package demo.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jpropeller.properties.event.PropEvent;
import org.jpropeller.properties.event.PropListener;
import org.jpropeller.util.GeneralUtils;
import org.jpropeller.view.impl.LabelPropView;
import org.jpropeller.view.proxy.impl.ViewProxyEditableBean;
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
				
				s.props().addListener(new PropListener() {
					@Override
					public <T> void propChanged(PropEvent<T> event) {
						System.out.println(event);
					}
				});
				
				final LabelPropView<String> nameView = 
					LabelPropView.create(
							ViewProxyEditableBean.create(TestStringBean.class, s), 
							s.name().getName());

				final LabelPropView<String> streetView = 
					LabelPropView.create(
							ViewProxyEditableBean.create(TestStringBean.class, s), 
							s.street().getName());

				JFrame frame = new JFrame("Label View Demo");
				frame.setLayout(new BorderLayout());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				JPanel labelPanel = new JPanel(new FlowLayout());
				labelPanel.add(nameView.getComponent());
				labelPanel.add(streetView.getComponent());
				frame.add(labelPanel, BorderLayout.CENTER);

				JPanel panel = new JPanel(new FlowLayout());
				
				JButton randomChangeCurrent = new JButton("randomChange current");
				randomChangeCurrent.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						TestStringBean current = (TestStringBean)nameView.getProxy().model().get();
						current.name().set("Random Name " + Math.random()*100);
						current.street().set("Random Street " + Math.random()*100);
					}
				});
				panel.add(randomChangeCurrent);
				
				JButton manyRandomChangesCurrent = new JButton("many random changes to current");
				manyRandomChangesCurrent.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						TestStringBean current = (TestStringBean)nameView.getProxy().model().get();
						long startTime = System.currentTimeMillis();
						int repeats = 100;
						for (int i = 0; i < repeats; i++) {
							current.name().set("Random Name " + Math.random()*100);
							current.street().set("Random Street " + Math.random()*100);
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
