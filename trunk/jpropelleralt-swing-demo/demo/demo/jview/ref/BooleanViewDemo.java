package demo.jview.ref;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jpropelleralt.jview.ref.impl.BooleanView;
import org.jpropelleralt.ref.Ref;
import org.jpropelleralt.ref.impl.RefDefault;
import org.jpropelleralt.utils.impl.LoggerUtils;
import org.jpropelleralt.view.Views;

/**
 * Demonstrate {@link BooleanView}
 */
public class BooleanViewDemo {

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
		frame.getContentPane().add(panel);
		
		final Ref<Boolean> b = RefDefault.create(true);
		Ref<String> name = RefDefault.create("Boolean value");
		
		BooleanView view = BooleanView.createCheck(b, name);
		BooleanView view2 = BooleanView.createButton(b, name);

		JButton toggle = new JButton(new AbstractAction("Toggle") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Boolean boo = b.get();
				if (boo == null) {
					boo = false;
				}
				b.set(!boo);
			}
		});

		JButton nullify = new JButton(new AbstractAction("Nullify") {
			@Override
			public void actionPerformed(ActionEvent e) {
				b.set(null);
			}
		});

		panel.add(view.getComponent());
		panel.add(view2.getComponent());
		panel.add(toggle);
		panel.add(nullify);
		
		frame.pack();
		frame.setVisible(true);
	}
	
}
