package demo.view;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jpropeller.properties.Prop;
import org.jpropeller.system.Props;
import org.jpropeller.view.impl.HidingView;
import org.jpropeller.view.primitive.impl.BooleanPropCheckboxEditor;

/**
 * Demonstrate {@link HidingView}
 */
public class HidingViewDemo {
	/**
	 * Demonstrate {@link HidingView}
	 * @param args	Ignored
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				showApp();
			}
		});
	}
	
	private static void showApp() {
		Prop<Boolean> show = Props.editable("show", true);
		BooleanPropCheckboxEditor showView = BooleanPropCheckboxEditor.create(show, Props.create("showName", "show"));
		
		HidingView hiding = new HidingView(show, true, new JLabel("Hidable"));
		
		JPanel mainPanel = new JPanel();
		mainPanel.add(showView.getComponent());
		mainPanel.add(new JLabel("start"));
		mainPanel.add(hiding.getComponent());
		mainPanel.add(new JLabel("end"));

		JFrame frame = new JFrame("HidingView Demo");
		frame.add(mainPanel);
		frame.pack();
		frame.setVisible(true);
	}
}
