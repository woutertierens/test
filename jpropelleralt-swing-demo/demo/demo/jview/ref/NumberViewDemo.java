package demo.jview.ref;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jpropelleralt.box.Boxes;
import org.jpropelleralt.jview.ref.impl.BooleanView;
import org.jpropelleralt.jview.ref.impl.NumberView;
import org.jpropelleralt.ref.Ref;
import org.jpropelleralt.ref.impl.RefDefault;
import org.jpropelleralt.utils.impl.LoggerUtils;
import org.jpropelleralt.view.Views;

/**
 * Demonstrate {@link NumberView}
 */
public class NumberViewDemo {

	/**
	 * Demonstrate
	 * @param args	Ignored
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				runApp();
			}
		});
	}
	
	private static void runApp() {
		LoggerUtils.enableConsoleLogging();
		Views.initialiseLookAndFeel();
		
		JFrame frame = new JFrame("Boolean Checkbox View");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		frame.getContentPane().add(panel);

		final Ref<Double> d = Boxes.create(42d);
		final Ref<Boolean> locked = RefDefault.create(false);
		
		BooleanView lockedView = BooleanView.createCheck(locked, "Locked");
		
		NumberView<Double> dView = NumberView.createDouble(d, locked, 0.1);
		
		JButton randomise = new JButton(new AbstractAction("Randomise") {
			@Override
			public void actionPerformed(ActionEvent e) {
				double r = Math.random();
				System.out.println("Randomising to " + r);
				d.set(r);
			}
		});

		panel.add(dView.getComponent());
		panel.add(lockedView.getComponent());
		panel.add(randomise);
		
		frame.pack();
		frame.setVisible(true);
	}
	
}
