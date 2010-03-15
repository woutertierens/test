package demo.jview.ref;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jpropelleralt.jview.ref.impl.BooleanView;
import org.jpropelleralt.jview.ref.impl.StringView;
import org.jpropelleralt.jview.ref.impl.BooleanView.ControlType;
import org.jpropelleralt.ref.Ref;
import org.jpropelleralt.ref.impl.RefDefault;
import org.jpropelleralt.utils.impl.LoggerUtils;
import org.jpropelleralt.view.Views;

/**
 * Demonstrate {@link BooleanView}
 */
public class StringViewDemo {

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
		
		final Ref<Boolean> b = RefDefault.create(true);
		Ref<String> name = RefDefault.create("Boolean value");
		final Ref<Boolean> locked = RefDefault.create(false);
		
		BooleanView booleanView = BooleanView.create(b, name, locked, ControlType.CHECK_BOX);
		StringView stringView = StringView.create(name, false, locked);
		BooleanView lockedView = BooleanView.createCheck(locked, "Locked");

		panel.add(booleanView.getComponent());
		panel.add(stringView.getComponent());
		panel.add(lockedView.getComponent());
		
		frame.pack();
		frame.setVisible(true);
	}
	
}
