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
import org.jpropelleralt.jview.ref.impl.NumberRangeView;
import org.jpropelleralt.jview.ref.impl.NumberView;
import org.jpropelleralt.ref.Ref;
import org.jpropelleralt.ref.impl.RefDefault;
import org.jpropelleralt.utils.impl.LoggerUtils;
import org.jpropelleralt.view.Views;

/**
 * Demonstrate {@link NumberView}
 */
public class NumberRangeViewDemo {

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
		
		JFrame frame = new JFrame("Number Range View");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		frame.getContentPane().add(panel);

		final Ref<Integer> value = Boxes.create(42);
		final Ref<Boolean> locked = RefDefault.create(false);
		
		BooleanView lockedView = BooleanView.createCheck(locked, "Locked");
		
		NumberRangeView rangeView = NumberRangeView.createSlider(value, 0, 100, locked);
		NumberRangeView barView = NumberRangeView.createProgressBar(value, 0, 100, locked);
		NumberView<Integer> intView = NumberView.createInteger(value, locked, 1);
		
		JButton randomise = new JButton(new AbstractAction("Randomise") {
			@Override
			public void actionPerformed(ActionEvent e) {
				int r = (int)(100 * Math.random());
				System.out.println("Randomising to " + r);
				value.set(r);
			}
		});

		panel.add(rangeView.getComponent());
		panel.add(barView.getComponent());
		panel.add(intView.getComponent());
		panel.add(lockedView.getComponent());
		panel.add(randomise);
		
		frame.pack();
		frame.setVisible(true);
	}
	
}
